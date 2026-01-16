# 保戶基本資料管理系統 (PolicyHolder Management System)

[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Tests](https://img.shields.io/badge/Tests-209%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/Coverage-81%25-brightgreen)]()
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

人壽保險保戶基本資料管理系統，提供保戶與保單的完整生命週期管理。

---

## 專案概述

本系統是一個基於 **Domain-Driven Design (DDD)** 設計的企業級應用程式，採用 **六角形架構 (Hexagonal Architecture)** 與 **CQRS Level 2** 模式，提供符合 **OpenAPI 3.0** 規範的 RESTful API。

### 已實作功能 (User Stories)

| User Story | 功能 | API | 狀態 |
|------------|------|-----|------|
| US1 | 新增保戶資料 | `POST /api/v1/policyholders` | ✅ |
| US2 | 查詢保戶資料 | `GET /api/v1/policyholders/{id}` | ✅ |
| US3 | 修改保戶資料 | `PUT /api/v1/policyholders/{id}` | ✅ |
| US4 | 刪除保戶資料 | `DELETE /api/v1/policyholders/{id}` | ✅ |
| US5 | 新增保單 | `POST /api/v1/policyholders/{id}/policies` | ✅ |
| US6 | 查詢保單 | `GET /api/v1/policyholders/{id}/policies` | ✅ |

### 技術亮點

- ✅ Domain-Driven Design 戰術設計模式
- ✅ 六角形架構（端口與適配器）
- ✅ CQRS Level 2（讀寫模型分離）
- ✅ 領域事件持久化
- ✅ SOLID 原則
- ✅ ArchUnit 架構測試
- ✅ TDD 測試驅動開發
- ✅ 台灣身分證字號驗證

---

## 架構設計

### 架構層級圖

```
┌────────────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │  REST API    │  │  H2 / JPA    │  │     Event Store          │  │
│  │  (Adapter)   │  │  (Adapter)   │  │     (Adapter)            │  │
│  └───────┬──────┘  └───────┬──────┘  └────────────┬─────────────┘  │
└──────────┼─────────────────┼──────────────────────┼────────────────┘
           │                 │                      │
           ▼                 ▼                      ▼
┌──────────────────────────────────────────────────────────────────┐
│                     Application Layer                             │
│  ┌─────────────────────┐    ┌─────────────────────────────────┐  │
│  │   Command Handlers  │    │       Query Handlers            │  │
│  │   (Write Side)      │    │       (Read Side)               │  │
│  └──────────┬──────────┘    └───────────────┬─────────────────┘  │
└─────────────┼───────────────────────────────┼────────────────────┘
              │                               │
              ▼                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Domain Layer                                │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │               PolicyHolder (Aggregate Root)                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌────────────────────┐  │  │
│  │  │   Policy    │  │   Address   │  │   Domain Events    │  │  │
│  │  │  (Entity)   │  │ (Value Obj) │  │                    │  │  │
│  │  └─────────────┘  └─────────────┘  └────────────────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
```

### 依賴規則

```
Infrastructure ──depends on──► Application ──depends on──► Domain
     │                              │                         │
     │                              │                         │
     ▼                              ▼                         ▼
 實作 Port                      定義 Port                   純領域邏輯
 (Adapter)                     (Interface)                 (無外部依賴)
```

**重要原則**：
- ✅ 外層可以依賴內層
- ❌ 內層不可依賴外層
- 內層透過 Port（Interface）與外層溝通

---

## 專案結構

```
src/main/java/com/insurance/policyholder/
│
├── domain/                          # 🔵 Domain Layer（最內層）
│   ├── model/
│   │   ├── aggregate/               # Aggregate Root
│   │   │   └── PolicyHolder.java
│   │   ├── entity/                  # Entity
│   │   │   └── Policy.java
│   │   ├── valueobject/             # Value Objects
│   │   │   ├── PolicyHolderId.java
│   │   │   ├── PolicyId.java
│   │   │   ├── NationalId.java
│   │   │   ├── PersonalInfo.java
│   │   │   ├── ContactInfo.java
│   │   │   ├── Address.java
│   │   │   └── Money.java
│   │   └── enums/                   # Domain Enums
│   │       ├── Gender.java
│   │       ├── PolicyHolderStatus.java
│   │       ├── PolicyType.java
│   │       └── PolicyStatus.java
│   ├── event/                       # Domain Events
│   │   ├── DomainEvent.java
│   │   ├── PolicyHolderCreated.java
│   │   ├── PolicyHolderUpdated.java
│   │   ├── PolicyHolderDeleted.java
│   │   └── PolicyAdded.java
│   ├── service/                     # Domain Services
│   │   └── PolicyHolderDomainService.java
│   └── exception/                   # Domain Exceptions
│       ├── DomainException.java
│       ├── PolicyHolderNotFoundException.java
│       ├── PolicyHolderNotActiveException.java
│       └── PolicyNotFoundException.java
│
├── application/                     # 🟢 Application Layer
│   ├── command/                     # Commands (Write)
│   │   ├── CreatePolicyHolderCommand.java
│   │   ├── UpdatePolicyHolderCommand.java
│   │   ├── DeletePolicyHolderCommand.java
│   │   └── AddPolicyCommand.java
│   ├── commandhandler/              # Command Handlers
│   │   ├── CreatePolicyHolderCommandHandler.java
│   │   ├── UpdatePolicyHolderCommandHandler.java
│   │   ├── DeletePolicyHolderCommandHandler.java
│   │   └── AddPolicyCommandHandler.java
│   ├── query/                       # Queries (Read)
│   │   ├── GetPolicyHolderQuery.java
│   │   ├── GetPolicyHolderByNationalIdQuery.java
│   │   ├── SearchPolicyHoldersQuery.java
│   │   ├── GetPolicyHolderPoliciesQuery.java
│   │   └── GetPolicyQuery.java
│   ├── queryhandler/                # Query Handlers
│   │   ├── GetPolicyHolderQueryHandler.java
│   │   ├── SearchPolicyHoldersQueryHandler.java
│   │   ├── GetPolicyHolderPoliciesQueryHandler.java
│   │   └── GetPolicyQueryHandler.java
│   ├── readmodel/                   # Read Models (DTOs)
│   │   ├── PolicyHolderReadModel.java
│   │   ├── PolicyHolderListItemReadModel.java
│   │   ├── PolicyReadModel.java
│   │   └── PagedResult.java
│   └── port/
│       ├── input/                   # Input Ports
│       │   ├── CommandHandler.java
│       │   └── QueryHandler.java
│       └── output/                  # Output Ports
│           ├── PolicyHolderRepository.java
│           ├── PolicyHolderQueryRepository.java
│           ├── DomainEventPublisher.java
│           └── EventStore.java
│
└── infrastructure/                  # 🟠 Infrastructure Layer（最外層）
    ├── adapter/
    │   ├── input/rest/              # REST API Adapter
    │   │   ├── PolicyHolderController.java
    │   │   ├── mapper/
    │   │   │   └── PolicyHolderRestMapper.java
    │   │   ├── request/
    │   │   │   ├── CreatePolicyHolderRequest.java
    │   │   │   ├── UpdatePolicyHolderRequest.java
    │   │   │   ├── AddPolicyRequest.java
    │   │   │   └── AddressRequest.java
    │   │   └── response/
    │   │       ├── ApiResponse.java
    │   │       ├── ErrorResponse.java
    │   │       ├── PolicyHolderResponse.java
    │   │       ├── PolicyResponse.java
    │   │       └── PageResponse.java
    │   └── output/
    │       ├── persistence/         # JPA Adapter
    │       │   ├── adapter/
    │       │   ├── entity/
    │       │   ├── mapper/
    │       │   └── repository/
    │       └── event/               # Event Store Adapter
    │           ├── DomainEventPublisherAdapter.java
    │           └── EventStoreAdapter.java
    ├── config/                      # Spring Configurations
    │   └── JpaConfig.java
    └── exception/                   # Global Exception Handler
        └── GlobalExceptionHandler.java
```

---

## 領域模型

### Aggregate 設計

```
PolicyHolder Aggregate
│
├── PolicyHolder (Aggregate Root)
│   ├── PolicyHolderId      ─── Value Object (格式: PH + 10位數字)
│   ├── NationalId          ─── Value Object (台灣身分證驗證)
│   ├── PersonalInfo        ─── Value Object (姓名、性別、生日)
│   ├── ContactInfo         ─── Value Object (手機、Email)
│   ├── Address             ─── Value Object (郵遞區號、縣市、區域、街道)
│   ├── status              ─── Enum (ACTIVE, INACTIVE, SUSPENDED)
│   ├── policies            ─── Entity Collection
│   └── domainEvents        ─── Event List
│
└── Policy (Entity)
    ├── PolicyId            ─── Value Object (格式: PO + 10位數字)
    ├── policyType          ─── Enum (LIFE, HEALTH, ACCIDENT, TRAVEL, PROPERTY, AUTO, SAFETY)
    ├── premium             ─── Money Value Object
    ├── sumInsured          ─── Money Value Object
    ├── startDate           ─── LocalDate
    ├── endDate             ─── LocalDate (nullable, 終身險無到期日)
    └── status              ─── Enum (ACTIVE, EXPIRED, CANCELLED)
```

### 領域事件

| 事件 | 觸發時機 | 用途 |
|------|----------|------|
| `PolicyHolderCreated` | 新增保戶成功 | 通知下游系統、建立初始資料 |
| `PolicyHolderUpdated` | 修改保戶成功 | 同步更新、稽核記錄 |
| `PolicyHolderDeleted` | 刪除保戶成功 | 清理關聯資料（軟刪除） |
| `PolicyAdded` | 新增保單成功 | 觸發保單生效流程 |

### 業務規則

| 規則 | 說明 |
|------|------|
| 身分證字號不可修改 | 建立後為唯一識別，不允許變更 |
| 年齡限制 | 保戶須年滿 18 歲 |
| 軟刪除 | 刪除保戶時狀態改為 INACTIVE |
| 保單新增限制 | 僅 ACTIVE 狀態保戶可新增保單 |
| SUSPENDED 狀態 | 停權狀態禁止任何修改操作 |

---

## CQRS 設計

本系統採用 **CQRS Level 2**（模型分離，同資料庫）：

### Command Side（寫入端）

```
Request → Controller → CommandHandler → Domain Model → Repository → Database
                              │
                              ▼
                       Domain Events → Event Store
```

### Query Side（讀取端）

```
Request → Controller → QueryHandler → Read Model ← QueryRepository ← Database
```

### 為什麼選擇 Level 2？

| Level | 說明 | 優點 | 缺點 |
|-------|------|------|------|
| Level 1 | 單一模型 | 簡單 | 讀寫耦合 |
| **Level 2** | **模型分離，同 DB** | **讀寫分離、查詢優化** | **需維護兩套模型** |
| Level 3 | 讀寫 DB 分離 | 高效能、可擴展 | 複雜、最終一致性 |

---

## 技術堆疊

| 類別 | 技術 | 版本 |
|------|------|------|
| 語言 | Java | 17+ |
| 框架 | Spring Boot | 3.x |
| API 文件 | OpenAPI / Swagger | 3.0 |
| 資料庫 | H2 Database (In-Memory) | Latest |
| ORM | Spring Data JPA / Hibernate | 3.x |
| 建置工具 | Gradle | 8.x |
| 單元測試 | JUnit 5, Mockito, AssertJ | Latest |
| 架構測試 | ArchUnit | Latest |
| BDD 測試 | Cucumber | Latest |

---

## 快速開始

### 前置需求

- JDK 17+
- Gradle 8+

### 建置與執行

```bash
# Clone 專案
git clone <repository-url>
cd insurance_management_architecture_demo

# 建置專案
gradle build

# 執行測試
gradle test

# 啟動應用程式
gradle bootRun
```

### 存取服務

| 服務 | URL |
|------|-----|
| API Base URL | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI Docs | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

---

## API 端點

### 保戶管理 API

| Method | Endpoint | 說明 |
|--------|----------|------|
| `POST` | `/api/v1/policyholders` | 新增保戶 |
| `GET` | `/api/v1/policyholders/{id}` | 依 ID 查詢保戶 |
| `GET` | `/api/v1/policyholders/national-id/{nationalId}` | 依身分證字號查詢 |
| `GET` | `/api/v1/policyholders` | 搜尋保戶（支援分頁、篩選） |
| `PUT` | `/api/v1/policyholders/{id}` | 修改保戶聯絡資訊 |
| `DELETE` | `/api/v1/policyholders/{id}` | 軟刪除保戶 |

### 保單管理 API

| Method | Endpoint | 說明 |
|--------|----------|------|
| `POST` | `/api/v1/policyholders/{id}/policies` | 新增保單 |
| `GET` | `/api/v1/policyholders/{id}/policies` | 查詢保戶所有保單 |
| `GET` | `/api/v1/policyholders/{id}/policies/{policyId}` | 查詢單一保單 |

### 查詢參數

**搜尋保戶 `GET /api/v1/policyholders`**

| 參數 | 類型 | 說明 |
|------|------|------|
| `name` | String | 姓名模糊搜尋 |
| `status` | String | 狀態篩選 (ACTIVE, INACTIVE, SUSPENDED) |
| `page` | Integer | 頁碼（從 0 開始） |
| `size` | Integer | 每頁筆數（預設 20） |

**查詢保單 `GET /api/v1/policyholders/{id}/policies`**

| 參數 | 類型 | 說明 |
|------|------|------|
| `type` | String | 保單類型篩選 (LIFE, HEALTH, ACCIDENT, etc.) |
| `status` | String | 保單狀態篩選 (ACTIVE, EXPIRED, CANCELLED) |

---

## API 使用範例

### 新增保戶

```bash
curl -X POST http://localhost:8080/api/v1/policyholders \
  -H "Content-Type: application/json" \
  -d '{
    "nationalId": "A123456789",
    "name": "王小明",
    "gender": "MALE",
    "birthDate": "1990-01-15",
    "mobilePhone": "0912345678",
    "email": "wang@example.com",
    "address": {
      "zipCode": "100",
      "city": "台北市",
      "district": "中正區",
      "street": "重慶南路一段1號"
    }
  }'
```

**回應：**
```json
{
  "success": true,
  "data": {
    "id": "PH0000000001",
    "nationalId": "A123456789",
    "name": "王小明",
    "gender": "MALE",
    "birthDate": "1990-01-15",
    "mobilePhone": "0912345678",
    "email": "wang@example.com",
    "address": {
      "zipCode": "100",
      "city": "台北市",
      "district": "中正區",
      "street": "重慶南路一段1號"
    },
    "status": "ACTIVE"
  },
  "message": "PolicyHolder created successfully"
}
```

### 新增保單

```bash
curl -X POST http://localhost:8080/api/v1/policyholders/PH0000000001/policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyType": "LIFE",
    "premium": 10000,
    "sumInsured": 1000000,
    "startDate": "2026-01-16",
    "endDate": "2027-01-16"
  }'
```

### 查詢保戶列表（分頁 + 篩選）

```bash
curl "http://localhost:8080/api/v1/policyholders?name=王&status=ACTIVE&page=0&size=10"
```

### 查詢保戶保單（類型篩選）

```bash
curl "http://localhost:8080/api/v1/policyholders/PH0000000001/policies?type=LIFE"
```

---

## 資料庫設計

### ER Diagram

```
┌─────────────────────────────┐       ┌─────────────────────────────┐
│      policy_holders          │       │         policies             │
├─────────────────────────────┤       ├─────────────────────────────┤
│ PK id           VARCHAR(13)  │───┐   │ PK id           VARCHAR(12)  │
│    national_id  VARCHAR(10)  │   │   │ FK policy_holder_id          │
│    name         VARCHAR(50)  │   └──►│    policy_type  VARCHAR(20)  │
│    gender       VARCHAR(10)  │       │    premium_amount DECIMAL    │
│    birth_date   DATE         │       │    sum_insured   DECIMAL     │
│    mobile_phone VARCHAR(10)  │       │    start_date    DATE        │
│    email        VARCHAR(100) │       │    end_date      DATE        │
│    zip_code     VARCHAR(5)   │       │    status        VARCHAR(20) │
│    city         VARCHAR(10)  │       │    created_at    TIMESTAMP   │
│    district     VARCHAR(10)  │       │    updated_at    TIMESTAMP   │
│    street       VARCHAR(100) │       └─────────────────────────────┘
│    status       VARCHAR(20)  │
│    created_at   TIMESTAMP    │       ┌─────────────────────────────┐
│    updated_at   TIMESTAMP    │       │       domain_events          │
│    version      BIGINT       │       ├─────────────────────────────┤
└─────────────────────────────┘       │ PK event_id      VARCHAR(36)  │
                                       │    aggregate_id  VARCHAR(50)  │
                                       │    aggregate_type VARCHAR(50) │
                                       │    event_type    VARCHAR(100) │
                                       │    event_data    CLOB         │
                                       │    occurred_on   TIMESTAMP    │
                                       │    published     BOOLEAN      │
                                       └─────────────────────────────┘
```

### 索引設計

**policy_holders 表**
- `idx_national_id` ON (national_id) - UNIQUE
- `idx_name` ON (name)
- `idx_status` ON (status)

**policies 表**
- `idx_policy_holder_id` ON (policy_holder_id)
- `idx_policy_type` ON (policy_type)
- `idx_status` ON (status)

**domain_events 表**
- `idx_aggregate` ON (aggregate_id, aggregate_type)
- `idx_event_type` ON (event_type)
- `idx_published` ON (published)

---

## 測試

### 測試統計

| 類型 | 數量 |
|------|------|
| 單元測試 | 177 |
| 整合測試 | 16 |
| 架構測試 | 16 |
| **總計** | **209** |

### 覆蓋率

| 指標 | 數值 |
|------|------|
| 指令覆蓋率 | 81% |
| 分支覆蓋率 | 65% |

### 執行測試

```bash
# 執行所有測試
gradle test

# 執行架構測試
gradle test --tests "*ArchitectureTest*"

# 執行整合測試
gradle test --tests "*IntegrationTest*"

# 產生覆蓋率報告
gradle test jacocoTestReport

# 查看報告
open build/reports/jacoco/test/html/index.html
```

### 架構測試 (ArchUnit)

確保六角形架構的依賴規則：

```java
@ArchTest
static final ArchRule domain_should_not_depend_on_application =
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..application..");

@ArchTest
static final ArchRule domain_should_not_depend_on_infrastructure =
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..");

@ArchTest
static final ArchRule application_should_not_depend_on_infrastructure =
    noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..");
```

### BDD 測試 (Cucumber Features)

```
src/test/resources/features/
├── create-policyholder.feature    # US1: 新增保戶
├── query-policyholder.feature     # US2: 查詢保戶
├── update-policyholder.feature    # US3: 修改保戶
├── delete-policyholder.feature    # US4: 刪除保戶
├── add-policy.feature             # US5: 新增保單
└── query-policies.feature         # US6: 查詢保單
```

---

## 設定說明

### application.yml

```yaml
spring:
  application:
    name: policyholder-management

  datasource:
    url: jdbc:h2:mem:policyholderdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    com.insurance.policyholder: DEBUG
    org.hibernate.SQL: DEBUG
```

---

## 錯誤處理

### 錯誤回應格式

```json
{
  "timestamp": "2026-01-16T15:30:00",
  "status": 404,
  "error": "POLICY_HOLDER_NOT_FOUND",
  "message": "Policy holder not found: PH9999999999",
  "path": "/api/v1/policyholders/PH9999999999"
}
```

### 錯誤代碼

| 錯誤代碼 | HTTP Status | 說明 |
|----------|-------------|------|
| `POLICY_HOLDER_NOT_FOUND` | 404 | 保戶不存在 |
| `POLICY_NOT_FOUND` | 404 | 保單不存在 |
| `POLICY_HOLDER_NOT_ACTIVE` | 400 | 保戶非活動狀態 |
| `VALIDATION_ERROR` | 400 | 輸入驗證錯誤 |
| `INVALID_ARGUMENT` | 400 | 非法參數 |
| `INTERNAL_ERROR` | 500 | 系統內部錯誤 |

---

## 相關文件

| 文件 | 說明 |
|------|------|
| `specs/001-policyholder-management/spec.md` | 功能規格文件 |
| `specs/001-policyholder-management/plan.md` | 實作計畫文件 |
| `specs/001-policyholder-management/tasks.md` | 任務清單 |
| `specs/001-policyholder-management/domain-model.md` | 領域模型設計 |
| `.specify/memory/constitution.md` | 專案架構原則 |

---

## 未來規劃

- [ ] 升級至 CQRS Level 3（讀寫資料庫分離）
- [ ] 整合訊息佇列（Kafka / RabbitMQ）
- [ ] 實作完整 Event Sourcing
- [ ] 新增保單受益人管理功能
- [ ] 整合外部身分驗證服務
- [ ] 新增 Kubernetes 部署配置
- [ ] 實作 API Rate Limiting

---

## 授權

本專案採用 MIT 授權 - 詳見 [LICENSE](LICENSE) 檔案

---

## 貢獻者

Built with Claude Opus 4.5
