# 保戶基本資料管理系統 (PolicyHolder Management System)

[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

人壽保險保戶基本資料管理系統，提供保戶與保單的完整生命週期管理。

---

## 📋 專案概述

本系統是一個基於 **Domain-Driven Design (DDD)** 設計的企業級應用程式，採用 **六角形架構 (Hexagonal Architecture)** 與 **CQRS** 模式，提供符合 **OpenAPI 3.0** 規範的 RESTful API。

### 核心功能

| 功能 | 說明 |
|------|------|
| 保戶管理 | 新增、修改、刪除、查詢保戶基本資料 |
| 保單管理 | 管理保戶持有的人壽險、意外險、平安險 |
| 事件驅動 | 領域事件持久化，支援事件溯源基礎 |

### 技術亮點

- ✅ Domain-Driven Design 戰術設計模式
- ✅ 六角形架構（端口與適配器）
- ✅ CQRS Level 2（讀寫模型分離）
- ✅ 領域事件持久化
- ✅ SOLID 原則
- ✅ ArchUnit 架構測試

---

## 🏗️ 架構設計

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

## 📁 專案結構

```
policyholder-management/
├── src/main/java/com/insurance/policyholder/
│   │
│   ├── domain/                      # 🔵 Domain Layer（最內層）
│   │   ├── model/
│   │   │   ├── aggregate/           # Aggregate Root
│   │   │   ├── entity/              # Entity
│   │   │   ├── valueobject/         # Value Objects
│   │   │   └── enums/               # Domain Enums
│   │   ├── event/                   # Domain Events
│   │   ├── service/                 # Domain Services
│   │   └── exception/               # Domain Exceptions
│   │
│   ├── application/                 # 🟢 Application Layer
│   │   ├── command/                 # Commands (Write)
│   │   ├── commandhandler/          # Command Handlers
│   │   ├── query/                   # Queries (Read)
│   │   ├── queryhandler/            # Query Handlers
│   │   ├── readmodel/               # Read Models (DTOs)
│   │   ├── port/
│   │   │   ├── input/               # Input Ports
│   │   │   └── output/              # Output Ports
│   │   └── service/                 # Application Services
│   │
│   └── infrastructure/              # 🟠 Infrastructure Layer（最外層）
│       ├── adapter/
│       │   ├── input/rest/          # REST API Adapter
│       │   └── output/
│       │       ├── persistence/     # JPA Adapter
│       │       └── event/           # Event Store Adapter
│       ├── config/                  # Spring Configurations
│       └── exception/               # Global Exception Handler
│
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/                # Database Scripts
│
├── PRD.md                           # 📄 產品需求文件
├── TECH.md                          # 📄 技術規格文件
└── README.md                        # 📄 本文件
```

---

## 🎯 領域模型

### Aggregate 設計

```
PolicyHolder Aggregate
│
├── PolicyHolder (Aggregate Root)
│   ├── PolicyHolderId      ─── Value Object (Identity)
│   ├── NationalId          ─── Value Object
│   ├── PersonalInfo        ─── Value Object
│   ├── ContactInfo         ─── Value Object
│   ├── Address             ─── Value Object
│   ├── status              ─── Enum
│   ├── policies            ─── Entity Collection
│   └── domainEvents        ─── Event List
│
└── Policy (Entity)
    ├── PolicyId            ─── Value Object (Identity)
    ├── policyNumber        ─── String
    ├── policyType          ─── Enum (LIFE, ACCIDENT, SAFETY)
    ├── Money (premium)     ─── Value Object
    └── status              ─── Enum
```

### 領域事件

| 事件 | 觸發時機 | 用途 |
|------|----------|------|
| `PolicyHolderCreated` | 新增保戶成功 | 通知下游系統、建立初始資料 |
| `PolicyHolderUpdated` | 修改保戶成功 | 同步更新、稽核記錄 |
| `PolicyHolderDeleted` | 刪除保戶成功 | 清理關聯資料 |
| `PolicyAdded` | 新增保單成功 | 觸發保單生效流程 |

---

## 🔄 CQRS 設計

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

| Level | 優點 | 缺點 | 適用場景 |
|-------|------|------|----------|
| Level 1 | 簡單 | 讀寫耦合 | 小型專案 |
| **Level 2** | **讀寫分離、查詢優化** | **需維護兩套模型** | **中型專案** |
| Level 3 | 高效能、可擴展 | 複雜、最終一致性 | 大型專案 |

---

## 🛠️ 技術堆疊

| 類別 | 技術 | 版本 |
|------|------|------|
| 語言 | Java | 17+ |
| 框架 | Spring Boot | 3.x |
| API 文件 | OpenAPI / Swagger | 3.0 |
| 資料庫 | H2 Database | Latest |
| ORM | Spring Data JPA | 3.x |
| 建置工具 | Gradle | 8.x |
| 測試 | JUnit 5, Mockito, ArchUnit | Latest |

---

## 🚀 快速開始

### 前置需求

- JDK 17+
- Gradle 8+ 或 Maven 3.8+

### 啟動專案

```bash
# Clone 專案
git clone https://github.com/your-org/policyholder-management.git
cd policyholder-management

# 建置專案
./gradlew build

# 啟動應用程式
./gradlew bootRun
```

### 存取服務

| 服務 | URL |
|------|-----|
| API Base URL | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console |

---

## 📡 API 端點

### 保戶管理

| Method | Endpoint | 說明 |
|--------|----------|------|
| `POST` | `/api/v1/policyholders` | 新增保戶 |
| `PUT` | `/api/v1/policyholders/{id}` | 修改保戶 |
| `DELETE` | `/api/v1/policyholders/{id}` | 刪除保戶 |
| `GET` | `/api/v1/policyholders/{id}` | 查詢單一保戶 |
| `GET` | `/api/v1/policyholders` | 查詢保戶列表 |

### 保單管理

| Method | Endpoint | 說明 |
|--------|----------|------|
| `POST` | `/api/v1/policyholders/{id}/policies` | 新增保單 |
| `GET` | `/api/v1/policyholders/{id}/policies` | 查詢保戶保單 |

### API 範例

#### 新增保戶

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

#### 查詢保戶列表

```bash
curl "http://localhost:8080/api/v1/policyholders?page=0&size=10&status=ACTIVE"
```

---

## 🗄️ 資料庫設計

### ER Diagram

```
┌─────────────────────┐       ┌─────────────────────┐
│   policy_holders    │       │      policies       │
├─────────────────────┤       ├─────────────────────┤
│ PK policy_holder_id │───┐   │ PK policy_id        │
│    national_id      │   │   │ FK policy_holder_id │
│    name             │   └──►│    policy_number    │
│    gender           │       │    policy_type      │
│    birth_date       │       │    policy_name      │
│    mobile_phone     │       │    premium_amount   │
│    email            │       │    sum_insured      │
│    address (embed)  │       │    effective_date   │
│    status           │       │    expiry_date      │
│    created_at       │       │    status           │
│    updated_at       │       └─────────────────────┘
└─────────────────────┘
                              ┌─────────────────────┐
                              │   domain_events     │
                              ├─────────────────────┤
                              │ PK event_id         │
                              │    aggregate_id     │
                              │    aggregate_type   │
                              │    event_type       │
                              │    event_data       │
                              │    occurred_on      │
                              │    published        │
                              └─────────────────────┘
```

---

## ✅ 測試

### 執行測試

```bash
# 執行所有測試
./gradlew test

# 執行架構測試
./gradlew test --tests "*ArchitectureTest*"

# 產生測試報告
./gradlew jacocoTestReport
```

### 架構測試 (ArchUnit)

確保六角形架構的依賴規則：

```java
@ArchTest
static final ArchRule domain_should_not_depend_on_infrastructure =
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..");
```

---

## 📚 相關文件

| 文件 | 說明 |
|------|------|
| [PRD.md](PRD.md) | 產品需求文件 - 業務需求與功能規格 |
| [TECH.md](TECH.md) | 技術規格文件 - 架構設計與技術細節 |

---

## 🔧 設定說明

### application.yml

```yaml
spring:
  application:
    name: policyholder-management
  
  datasource:
    url: jdbc:h2:mem:policyholder
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

server:
  port: 8080
```

---

## 📈 未來規劃

- [ ] 升級至 CQRS Level 3（讀寫資料庫分離）
- [ ] 整合訊息佇列（Kafka / RabbitMQ）
- [ ] 實作 Event Sourcing
- [ ] 新增保單受益人管理功能
- [ ] 整合外部身分驗證服務

---

## 🤝 貢獻指南

1. Fork 本專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'Add amazing feature'`)
4. 推送分支 (`git push origin feature/amazing-feature`)
5. 開啟 Pull Request

---

## 📄 授權

本專案採用 MIT 授權 - 詳見 [LICENSE](LICENSE) 檔案

---

## 📞 聯絡方式

如有任何問題，請透過以下方式聯繫：

- 提交 Issue
- 發送郵件至專案維護者
