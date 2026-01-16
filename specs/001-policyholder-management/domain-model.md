# Data Model: 保戶基本資料管理系統

**Date**: 2026-01-16
**Feature**: 001-policyholder-management

## 聚合設計 (Aggregate Design)

### PolicyHolder Aggregate

```
PolicyHolder Aggregate
├── PolicyHolder (Aggregate Root)
│   ├── PolicyHolderId (Value Object - Identity)
│   ├── NationalId (Value Object)
│   ├── PersonalInfo (Value Object)
│   │   ├── name: String
│   │   ├── gender: Gender
│   │   └── birthDate: LocalDate
│   ├── ContactInfo (Value Object)
│   │   ├── mobilePhone: String
│   │   └── email: String (optional)
│   ├── Address (Value Object)
│   │   ├── zipCode: String
│   │   ├── city: String
│   │   ├── district: String
│   │   └── street: String
│   ├── status: PolicyHolderStatus
│   ├── policies: List<Policy>
│   ├── createdAt: LocalDateTime
│   ├── updatedAt: LocalDateTime
│   └── version: Long (樂觀鎖)
│
└── Policy (Entity within Aggregate)
    ├── PolicyId (Value Object - Identity)
    ├── policyNumber: String
    ├── policyType: PolicyType
    ├── policyName: String
    ├── premiumAmount: Money
    ├── sumInsured: Money
    ├── effectiveDate: LocalDate
    ├── expiryDate: LocalDate (optional)
    ├── status: PolicyStatus
    ├── createdAt: LocalDateTime
    └── updatedAt: LocalDateTime
```

---

## 實體與值物件定義

### Aggregate Root: PolicyHolder

| 屬性 | 型別 | 必填 | 說明 |
|------|------|------|------|
| id | PolicyHolderId | ✅ | 系統產生，格式：PH + 10位數字 |
| nationalId | NationalId | ✅ | 身分證字號，唯一 |
| personalInfo | PersonalInfo | ✅ | 個人基本資料 |
| contactInfo | ContactInfo | ✅ | 聯絡資訊 |
| address | Address | ✅ | 通訊地址 |
| status | PolicyHolderStatus | ✅ | 保戶狀態 |
| policies | List\<Policy\> | - | 持有保單 |
| createdAt | LocalDateTime | ✅ | 建立時間 |
| updatedAt | LocalDateTime | ✅ | 更新時間 |
| version | Long | ✅ | 樂觀鎖版本 |

**業務規則：**
- 身分證字號建立後不可修改
- 出生日期需年滿 18 歲
- 狀態為 SUSPENDED 時禁止修改
- 刪除採軟刪除（狀態改為 INACTIVE）

---

### Entity: Policy

| 屬性 | 型別 | 必填 | 說明 |
|------|------|------|------|
| id | PolicyId | ✅ | 系統產生 UUID |
| policyNumber | String | ✅ | 業務識別碼，唯一 |
| policyType | PolicyType | ✅ | 保單類型 |
| policyName | String | ✅ | 商品名稱 |
| premiumAmount | Money | ✅ | 年繳保費 |
| sumInsured | Money | ✅ | 保險金額 |
| effectiveDate | LocalDate | ✅ | 生效日期 |
| expiryDate | LocalDate | - | 到期日期（終身險無） |
| status | PolicyStatus | ✅ | 保單狀態 |
| createdAt | LocalDateTime | ✅ | 建立時間 |
| updatedAt | LocalDateTime | ✅ | 更新時間 |

**業務規則：**
- 生效日期不可早於今日
- 保單號碼全系統唯一
- 僅能新增至 ACTIVE 狀態的保戶

---

### Value Objects

#### PolicyHolderId
```java
public record PolicyHolderId(String value) {
    private static final String PREFIX = "PH";
    private static final int SEQUENCE_LENGTH = 10;

    // 格式驗證：PH + 10位數字
    // 範例：PH0000000001
}
```

#### NationalId
```java
public record NationalId(String value) {
    // 格式：首位英文 + 9位數字
    // 驗證：台灣身分證檢查碼
    // 範例：A123456789
}
```

#### PersonalInfo
```java
public record PersonalInfo(
    String name,        // 最大 50 字元
    Gender gender,      // MALE / FEMALE
    LocalDate birthDate // 需年滿 18 歲
) {}
```

#### ContactInfo
```java
public record ContactInfo(
    String mobilePhone, // 格式：09xxxxxxxx
    String email        // Optional, Email 格式驗證
) {}
```

#### Address
```java
public record Address(
    String zipCode,     // 5 位數
    String city,        // 縣市
    String district,    // 區域
    String street       // 街道地址
) {}
```

#### Money
```java
public record Money(
    BigDecimal amount,
    Currency currency   // 預設 TWD
) {
    // 金額不可為負
}
```

---

### Enumerations

#### Gender
```java
public enum Gender {
    MALE,
    FEMALE
}
```

#### PolicyHolderStatus
```java
public enum PolicyHolderStatus {
    ACTIVE,     // 有效
    INACTIVE,   // 停用（軟刪除）
    SUSPENDED   // 停權
}
```

#### PolicyType
```java
public enum PolicyType {
    LIFE,       // 人壽險
    ACCIDENT,   // 意外險
    SAFETY      // 平安險
}
```

#### PolicyStatus
```java
public enum PolicyStatus {
    ACTIVE,     // 有效
    LAPSED,     // 失效
    TERMINATED  // 終止
}
```

---

## 領域事件

### PolicyHolderCreated
```java
public class PolicyHolderCreated extends DomainEvent {
    private PolicyHolderSnapshot snapshot;
}
```

### PolicyHolderUpdated
```java
public class PolicyHolderUpdated extends DomainEvent {
    private PolicyHolderSnapshot beforeSnapshot;
    private PolicyHolderSnapshot afterSnapshot;
}
```

### PolicyHolderDeleted
```java
public class PolicyHolderDeleted extends DomainEvent {
    private String policyHolderId;
    private LocalDateTime deletedAt;
}
```

### PolicyAdded
```java
public class PolicyAdded extends DomainEvent {
    private String policyHolderId;
    private PolicySnapshot policySnapshot;
}
```

---

## 資料庫實體映射

### policy_holders 表

| 欄位 | 型別 | 約束 | 說明 |
|------|------|------|------|
| policy_holder_id | VARCHAR(12) | PK | 保戶編號 |
| national_id | VARCHAR(10) | UNIQUE, NOT NULL | 身分證字號 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(10) | NOT NULL | 性別 |
| birth_date | DATE | NOT NULL | 出生日期 |
| mobile_phone | VARCHAR(10) | NOT NULL | 手機號碼 |
| email | VARCHAR(100) | | 電子郵件 |
| zip_code | VARCHAR(5) | | 郵遞區號 |
| city | VARCHAR(10) | | 縣市 |
| district | VARCHAR(10) | | 區域 |
| street | VARCHAR(100) | | 街道地址 |
| status | VARCHAR(20) | NOT NULL | 保戶狀態 |
| created_at | TIMESTAMP | NOT NULL | 建立時間 |
| updated_at | TIMESTAMP | NOT NULL | 更新時間 |
| version | BIGINT | NOT NULL | 樂觀鎖版本 |

**索引：**
- `idx_policy_holders_national_id` ON (national_id)
- `idx_policy_holders_name` ON (name)
- `idx_policy_holders_status` ON (status)

---

### policies 表

| 欄位 | 型別 | 約束 | 說明 |
|------|------|------|------|
| policy_id | VARCHAR(36) | PK | 保單編號 (UUID) |
| policy_holder_id | VARCHAR(12) | FK, NOT NULL | 保戶編號 |
| policy_number | VARCHAR(20) | UNIQUE, NOT NULL | 保單號碼 |
| policy_type | VARCHAR(20) | NOT NULL | 保單類型 |
| policy_name | VARCHAR(100) | NOT NULL | 保單名稱 |
| premium_amount | DECIMAL(15,2) | NOT NULL | 保費金額 |
| sum_insured | DECIMAL(15,2) | NOT NULL | 保險金額 |
| effective_date | DATE | NOT NULL | 生效日期 |
| expiry_date | DATE | | 到期日期 |
| status | VARCHAR(20) | NOT NULL | 保單狀態 |
| created_at | TIMESTAMP | NOT NULL | 建立時間 |
| updated_at | TIMESTAMP | NOT NULL | 更新時間 |

**索引：**
- `idx_policies_policy_holder_id` ON (policy_holder_id)
- `idx_policies_policy_type` ON (policy_type)
- `idx_policies_status` ON (status)

**外鍵：**
- FK `policy_holder_id` → `policy_holders(policy_holder_id)`

---

### domain_events 表

| 欄位 | 型別 | 約束 | 說明 |
|------|------|------|------|
| event_id | VARCHAR(36) | PK | 事件 ID (UUID) |
| aggregate_id | VARCHAR(50) | NOT NULL | 聚合 ID |
| aggregate_type | VARCHAR(50) | NOT NULL | 聚合類型 |
| event_type | VARCHAR(100) | NOT NULL | 事件類型 |
| event_data | CLOB | NOT NULL | 事件資料 (JSON) |
| occurred_on | TIMESTAMP | NOT NULL | 發生時間 |
| published | BOOLEAN | NOT NULL | 是否已發布 |
| published_at | TIMESTAMP | | 發布時間 |

**索引：**
- `idx_domain_events_aggregate` ON (aggregate_id, aggregate_type)
- `idx_domain_events_type` ON (event_type)
- `idx_domain_events_published` ON (published)

---

## ER Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    policy_holders                        │
├─────────────────────────────────────────────────────────┤
│ PK  policy_holder_id    VARCHAR(12)                     │
│     national_id         VARCHAR(10)    UNIQUE NOT NULL  │
│     name                VARCHAR(50)    NOT NULL         │
│     gender              VARCHAR(10)    NOT NULL         │
│     birth_date          DATE           NOT NULL         │
│     mobile_phone        VARCHAR(10)    NOT NULL         │
│     email               VARCHAR(100)                    │
│     zip_code            VARCHAR(5)                      │
│     city                VARCHAR(10)                     │
│     district            VARCHAR(10)                     │
│     street              VARCHAR(100)                    │
│     status              VARCHAR(20)    NOT NULL         │
│     created_at          TIMESTAMP      NOT NULL         │
│     updated_at          TIMESTAMP      NOT NULL         │
│     version             BIGINT         NOT NULL         │
└───────────────────────────┬─────────────────────────────┘
                            │ 1
                            │
                            │ *
┌───────────────────────────┴─────────────────────────────┐
│                       policies                           │
├─────────────────────────────────────────────────────────┤
│ PK  policy_id           VARCHAR(36)                     │
│ FK  policy_holder_id    VARCHAR(12)    NOT NULL         │
│     policy_number       VARCHAR(20)    UNIQUE NOT NULL  │
│     policy_type         VARCHAR(20)    NOT NULL         │
│     policy_name         VARCHAR(100)   NOT NULL         │
│     premium_amount      DECIMAL(15,2)  NOT NULL         │
│     sum_insured         DECIMAL(15,2)  NOT NULL         │
│     effective_date      DATE           NOT NULL         │
│     expiry_date         DATE                            │
│     status              VARCHAR(20)    NOT NULL         │
│     created_at          TIMESTAMP      NOT NULL         │
│     updated_at          TIMESTAMP      NOT NULL         │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    domain_events                         │
├─────────────────────────────────────────────────────────┤
│ PK  event_id            VARCHAR(36)                     │
│     aggregate_id        VARCHAR(50)    NOT NULL         │
│     aggregate_type      VARCHAR(50)    NOT NULL         │
│     event_type          VARCHAR(100)   NOT NULL         │
│     event_data          CLOB           NOT NULL         │
│     occurred_on         TIMESTAMP      NOT NULL         │
│     published           BOOLEAN        NOT NULL         │
│     published_at        TIMESTAMP                       │
└─────────────────────────────────────────────────────────┘
```
