# 保戶基本資料管理系統 (PolicyHolder Management System)

[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Tests](https://img.shields.io/badge/Tests-209%20passing-brightgreen)]()
[![Coverage](https://img.shields.io/badge/Coverage-81%25-brightgreen)]()
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

人壽保險保戶基本資料管理系統，提供保戶與保單的完整生命週期管理。

---

## 目錄

- [專案概述](#專案概述)
- [架構設計圖](#架構設計圖)
  - [六角形架構圖](#六角形架構圖)
  - [系統元件圖](#系統元件圖)
  - [CQRS 架構圖](#cqrs-架構圖)
- [類別圖](#類別圖)
  - [領域層類別圖](#領域層類別圖)
  - [應用層類別圖](#應用層類別圖)
  - [基礎設施層類別圖](#基礎設施層類別圖)
- [時序圖](#時序圖)
  - [新增保戶時序圖](#新增保戶時序圖)
  - [查詢保戶時序圖](#查詢保戶時序圖)
  - [新增保單時序圖](#新增保單時序圖)
  - [刪除保戶時序圖](#刪除保戶時序圖)
- [ER Diagram](#er-diagram)
- [狀態圖](#狀態圖)
- [專案結構](#專案結構)
- [API 端點](#api-端點)
- [快速開始](#快速開始)

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

## 架構設計圖

### 六角形架構圖

```mermaid
graph TB
    subgraph External["外部世界"]
        Client["🖥️ Client<br/>(REST API)"]
        DB["🗄️ H2 Database"]
        EventBus["📨 Event Bus"]
    end

    subgraph Infrastructure["Infrastructure Layer (基礎設施層)"]
        subgraph InputAdapters["Input Adapters (輸入適配器)"]
            RestController["REST Controller<br/>PolicyHolderController"]
        end

        subgraph OutputAdapters["Output Adapters (輸出適配器)"]
            RepoAdapter["Repository Adapter<br/>PolicyHolderRepositoryAdapter"]
            EventAdapter["Event Adapter<br/>DomainEventPublisherAdapter"]
        end
    end

    subgraph Application["Application Layer (應用層)"]
        subgraph InputPorts["Input Ports (輸入端口)"]
            CmdHandler["Command Handlers"]
            QryHandler["Query Handlers"]
        end

        subgraph OutputPorts["Output Ports (輸出端口)"]
            RepoPort["PolicyHolderRepository<br/><<interface>>"]
            EventPort["DomainEventPublisher<br/><<interface>>"]
        end
    end

    subgraph Domain["Domain Layer (領域層)"]
        Aggregate["PolicyHolder<br/>(Aggregate Root)"]
        Entity["Policy<br/>(Entity)"]
        ValueObj["Value Objects<br/>Address, Money, etc."]
        DomainEvent["Domain Events"]
    end

    Client --> RestController
    RestController --> CmdHandler
    RestController --> QryHandler

    CmdHandler --> Aggregate
    QryHandler --> RepoPort
    Aggregate --> DomainEvent

    CmdHandler --> RepoPort
    CmdHandler --> EventPort

    RepoPort -.->|implements| RepoAdapter
    EventPort -.->|implements| EventAdapter

    RepoAdapter --> DB
    EventAdapter --> EventBus

    style Domain fill:#e1f5fe
    style Application fill:#fff3e0
    style Infrastructure fill:#fce4ec
    style External fill:#f5f5f5
```

### 系統元件圖

```mermaid
graph LR
    subgraph Presentation["展示層"]
        API["REST API<br/>/api/v1/policyholders"]
        Swagger["Swagger UI<br/>/swagger-ui.html"]
    end

    subgraph Application["應用層"]
        subgraph Commands["Commands (寫入)"]
            CreateCmd["CreatePolicyHolder"]
            UpdateCmd["UpdatePolicyHolder"]
            DeleteCmd["DeletePolicyHolder"]
            AddPolicyCmd["AddPolicy"]
        end

        subgraph Queries["Queries (讀取)"]
            GetByIdQry["GetPolicyHolder"]
            SearchQry["SearchPolicyHolders"]
            GetPoliciesQry["GetPolicies"]
        end
    end

    subgraph Domain["領域層"]
        PHAggregate["PolicyHolder<br/>Aggregate"]
        PolicyEntity["Policy<br/>Entity"]
        Events["Domain Events"]
    end

    subgraph Persistence["持久層"]
        JPA["JPA Entities"]
        H2["H2 Database"]
        EventStore["Event Store"]
    end

    API --> Commands
    API --> Queries
    Swagger --> API

    Commands --> PHAggregate
    PHAggregate --> PolicyEntity
    PHAggregate --> Events

    Queries --> JPA
    Commands --> JPA
    Events --> EventStore

    JPA --> H2
    EventStore --> H2

    style Presentation fill:#c8e6c9
    style Application fill:#fff9c4
    style Domain fill:#bbdefb
    style Persistence fill:#ffccbc
```

### CQRS 架構圖

```mermaid
graph TB
    subgraph Client["客戶端"]
        Request["HTTP Request"]
    end

    subgraph WriteModel["Command Side (寫入端)"]
        WController["Controller"]
        CmdHandler["Command Handler"]
        Aggregate["PolicyHolder<br/>Aggregate"]
        WriteRepo["Repository<br/>(Write)"]
        EventPublisher["Event Publisher"]
    end

    subgraph ReadModel["Query Side (讀取端)"]
        RController["Controller"]
        QryHandler["Query Handler"]
        ReadModel2["Read Model<br/>(DTO)"]
        QueryRepo["Query Repository<br/>(Read)"]
    end

    subgraph Storage["儲存層"]
        Database[("H2 Database")]
        EventStore[("Event Store")]
    end

    Request -->|POST/PUT/DELETE| WController
    Request -->|GET| RController

    WController --> CmdHandler
    CmdHandler --> Aggregate
    Aggregate -->|save| WriteRepo
    Aggregate -->|publish| EventPublisher
    WriteRepo --> Database
    EventPublisher --> EventStore

    RController --> QryHandler
    QryHandler --> ReadModel2
    QueryRepo --> ReadModel2
    QueryRepo --> Database

    style WriteModel fill:#ffcdd2
    style ReadModel fill:#c8e6c9
    style Storage fill:#e1f5fe
```

---

## 類別圖

### 領域層類別圖

```mermaid
classDiagram
    class PolicyHolder {
        <<Aggregate Root>>
        -PolicyHolderId id
        -NationalId nationalId
        -PersonalInfo personalInfo
        -ContactInfo contactInfo
        -Address address
        -PolicyHolderStatus status
        -List~Policy~ policies
        -List~DomainEvent~ domainEvents
        -Long version
        +create(NationalId, PersonalInfo, ContactInfo, Address) PolicyHolder
        +updateContactInfo(ContactInfo) void
        +updateAddress(Address) void
        +addPolicy(Policy) void
        +deactivate() void
        +isActive() boolean
        +getDomainEvents() List~DomainEvent~
    }

    class Policy {
        <<Entity>>
        -PolicyId id
        -PolicyType policyType
        -Money premium
        -Money sumInsured
        -LocalDate startDate
        -LocalDate endDate
        -PolicyStatus status
        +create(PolicyType, Money, Money, LocalDate, LocalDate) Policy
        +isValidPeriod() boolean
        +isActive() boolean
    }

    class PolicyHolderId {
        <<Value Object>>
        -String value
        +generate() PolicyHolderId
        +of(String) PolicyHolderId
        +getValue() String
    }

    class NationalId {
        <<Value Object>>
        -String value
        +of(String) NationalId
        +validate(String) boolean
        +getValue() String
    }

    class PersonalInfo {
        <<Value Object>>
        -String name
        -Gender gender
        -LocalDate birthDate
        +of(String, Gender, LocalDate) PersonalInfo
        +getAge() int
    }

    class ContactInfo {
        <<Value Object>>
        -String mobilePhone
        -String email
        +of(String, String) ContactInfo
    }

    class Address {
        <<Value Object>>
        -String zipCode
        -String city
        -String district
        -String street
        +of(String, String, String, String) Address
        +getFullAddress() String
    }

    class Money {
        <<Value Object>>
        -BigDecimal amount
        -String currency
        +of(BigDecimal) Money
        +twd(long) Money
        +add(Money) Money
        +getAmount() BigDecimal
    }

    class PolicyHolderStatus {
        <<Enumeration>>
        ACTIVE
        INACTIVE
        SUSPENDED
    }

    class PolicyType {
        <<Enumeration>>
        LIFE
        HEALTH
        ACCIDENT
        TRAVEL
        PROPERTY
        AUTO
        SAFETY
    }

    class PolicyStatus {
        <<Enumeration>>
        ACTIVE
        EXPIRED
        CANCELLED
    }

    class DomainEvent {
        <<Abstract>>
        -String eventId
        -String aggregateId
        -LocalDateTime occurredOn
        +getEventType() String
    }

    class PolicyHolderCreated {
        -PolicyHolderSnapshot snapshot
    }

    class PolicyHolderUpdated {
        -PolicyHolderSnapshot before
        -PolicyHolderSnapshot after
    }

    class PolicyAdded {
        -String policyHolderId
        -PolicySnapshot policySnapshot
    }

    PolicyHolder "1" *-- "0..*" Policy : contains
    PolicyHolder *-- PolicyHolderId
    PolicyHolder *-- NationalId
    PolicyHolder *-- PersonalInfo
    PolicyHolder *-- ContactInfo
    PolicyHolder *-- Address
    PolicyHolder *-- PolicyHolderStatus
    PolicyHolder o-- DomainEvent

    Policy *-- PolicyId
    Policy *-- PolicyType
    Policy *-- PolicyStatus
    Policy *-- Money

    PersonalInfo *-- Gender

    DomainEvent <|-- PolicyHolderCreated
    DomainEvent <|-- PolicyHolderUpdated
    DomainEvent <|-- PolicyAdded
```

### 應用層類別圖

```mermaid
classDiagram
    class CommandHandler~C, R~ {
        <<Interface>>
        +handle(C command) R
    }

    class QueryHandler~Q, R~ {
        <<Interface>>
        +handle(Q query) R
    }

    class CreatePolicyHolderCommandHandler {
        -PolicyHolderRepository repository
        -DomainEventPublisher eventPublisher
        +handle(CreatePolicyHolderCommand) PolicyHolderReadModel
    }

    class UpdatePolicyHolderCommandHandler {
        -PolicyHolderRepository repository
        -DomainEventPublisher eventPublisher
        +handle(UpdatePolicyHolderCommand) PolicyHolderReadModel
    }

    class DeletePolicyHolderCommandHandler {
        -PolicyHolderRepository repository
        -DomainEventPublisher eventPublisher
        +handle(DeletePolicyHolderCommand) void
    }

    class AddPolicyCommandHandler {
        -PolicyHolderRepository repository
        -DomainEventPublisher eventPublisher
        +handle(AddPolicyCommand) PolicyReadModel
    }

    class GetPolicyHolderQueryHandler {
        -PolicyHolderRepository repository
        +handle(GetPolicyHolderQuery) PolicyHolderReadModel
    }

    class SearchPolicyHoldersQueryHandler {
        -PolicyHolderQueryRepository queryRepository
        +handle(SearchPolicyHoldersQuery) PagedResult
    }

    class CreatePolicyHolderCommand {
        -String nationalId
        -String name
        -String gender
        -LocalDate birthDate
        -String mobilePhone
        -String email
        -AddressData address
    }

    class UpdatePolicyHolderCommand {
        -String policyHolderId
        -String mobilePhone
        -String email
        -AddressData address
    }

    class AddPolicyCommand {
        -String policyHolderId
        -String policyType
        -BigDecimal premium
        -BigDecimal sumInsured
        -LocalDate startDate
        -LocalDate endDate
    }

    class PolicyHolderReadModel {
        -String id
        -String nationalId
        -String name
        -String gender
        -LocalDate birthDate
        -String mobilePhone
        -String email
        -AddressReadModel address
        -String status
    }

    class PolicyReadModel {
        -String id
        -String policyHolderId
        -String policyType
        -BigDecimal premium
        -BigDecimal sumInsured
        -LocalDate startDate
        -LocalDate endDate
        -String status
    }

    class PolicyHolderRepository {
        <<Interface>>
        +save(PolicyHolder) PolicyHolder
        +findById(PolicyHolderId) Optional~PolicyHolder~
        +findByNationalId(NationalId) Optional~PolicyHolder~
        +existsByNationalId(NationalId) boolean
    }

    class PolicyHolderQueryRepository {
        <<Interface>>
        +findAll(Pageable) Page~PolicyHolderListItemReadModel~
        +searchByName(String, Pageable) Page~PolicyHolderListItemReadModel~
        +findByStatus(String, Pageable) Page~PolicyHolderListItemReadModel~
    }

    class DomainEventPublisher {
        <<Interface>>
        +publish(DomainEvent) void
        +publishAll(List~DomainEvent~) void
    }

    CommandHandler <|.. CreatePolicyHolderCommandHandler
    CommandHandler <|.. UpdatePolicyHolderCommandHandler
    CommandHandler <|.. DeletePolicyHolderCommandHandler
    CommandHandler <|.. AddPolicyCommandHandler

    QueryHandler <|.. GetPolicyHolderQueryHandler
    QueryHandler <|.. SearchPolicyHoldersQueryHandler

    CreatePolicyHolderCommandHandler ..> PolicyHolderRepository
    CreatePolicyHolderCommandHandler ..> DomainEventPublisher
    CreatePolicyHolderCommandHandler ..> CreatePolicyHolderCommand
    CreatePolicyHolderCommandHandler ..> PolicyHolderReadModel

    GetPolicyHolderQueryHandler ..> PolicyHolderRepository
    SearchPolicyHoldersQueryHandler ..> PolicyHolderQueryRepository
```

### 基礎設施層類別圖

```mermaid
classDiagram
    class PolicyHolderController {
        -CreatePolicyHolderCommandHandler createHandler
        -UpdatePolicyHolderCommandHandler updateHandler
        -DeletePolicyHolderCommandHandler deleteHandler
        -AddPolicyCommandHandler addPolicyHandler
        -GetPolicyHolderQueryHandler getHandler
        -SearchPolicyHoldersQueryHandler searchHandler
        +createPolicyHolder(CreatePolicyHolderRequest) ResponseEntity
        +getPolicyHolder(String) ResponseEntity
        +updatePolicyHolder(String, UpdatePolicyHolderRequest) ResponseEntity
        +deletePolicyHolder(String) ResponseEntity
        +addPolicy(String, AddPolicyRequest) ResponseEntity
        +getPolicies(String, String, String) ResponseEntity
    }

    class PolicyHolderRepositoryAdapter {
        -PolicyHolderJpaRepository jpaRepository
        -PolicyHolderMapper mapper
        +save(PolicyHolder) PolicyHolder
        +findById(PolicyHolderId) Optional~PolicyHolder~
        +findByNationalId(NationalId) Optional~PolicyHolder~
    }

    class PolicyHolderQueryRepositoryAdapter {
        -PolicyHolderJpaRepository jpaRepository
        +findAll(Pageable) Page~PolicyHolderListItemReadModel~
        +searchByName(String, Pageable) Page~PolicyHolderListItemReadModel~
    }

    class DomainEventPublisherAdapter {
        -EventStore eventStore
        -ApplicationEventPublisher springPublisher
        +publish(DomainEvent) void
        +publishAll(List~DomainEvent~) void
    }

    class PolicyHolderJpaEntity {
        -String id
        -String nationalId
        -String name
        -Gender gender
        -LocalDate birthDate
        -String mobilePhone
        -String email
        -String zipCode
        -String city
        -String district
        -String street
        -PolicyHolderStatus status
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -Long version
        -List~PolicyJpaEntity~ policies
    }

    class PolicyJpaEntity {
        -String id
        -String policyHolderId
        -PolicyType policyType
        -BigDecimal premiumAmount
        -BigDecimal sumInsured
        -LocalDate startDate
        -LocalDate endDate
        -PolicyStatus status
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
    }

    class DomainEventJpaEntity {
        -String eventId
        -String aggregateId
        -String aggregateType
        -String eventType
        -String eventData
        -LocalDateTime occurredOn
        -boolean published
        -LocalDateTime publishedAt
    }

    class PolicyHolderJpaRepository {
        <<Interface>>
        +findByNationalId(String) Optional~PolicyHolderJpaEntity~
        +existsByNationalId(String) boolean
        +findByNameContaining(String, Pageable) Page~PolicyHolderJpaEntity~
    }

    class GlobalExceptionHandler {
        +handlePolicyHolderNotFoundException(Exception) ResponseEntity
        +handleValidationException(Exception) ResponseEntity
        +handleGenericException(Exception) ResponseEntity
    }

    PolicyHolderController ..> CreatePolicyHolderCommandHandler
    PolicyHolderController ..> GetPolicyHolderQueryHandler

    PolicyHolderRepositoryAdapter ..|> PolicyHolderRepository
    PolicyHolderRepositoryAdapter ..> PolicyHolderJpaRepository
    PolicyHolderRepositoryAdapter ..> PolicyHolderMapper

    PolicyHolderQueryRepositoryAdapter ..|> PolicyHolderQueryRepository

    DomainEventPublisherAdapter ..|> DomainEventPublisher

    PolicyHolderJpaEntity "1" *-- "0..*" PolicyJpaEntity

    PolicyHolderJpaRepository ..> PolicyHolderJpaEntity
```

---

## 時序圖

### 新增保戶時序圖

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as PolicyHolderController
    participant Handler as CreatePolicyHolderCommandHandler
    participant Aggregate as PolicyHolder
    participant Repo as PolicyHolderRepository
    participant EventPub as DomainEventPublisher
    participant DB as Database

    Client->>+Controller: POST /api/v1/policyholders
    Controller->>Controller: Validate Request
    Controller->>+Handler: handle(CreatePolicyHolderCommand)

    Handler->>Repo: existsByNationalId(nationalId)
    Repo->>DB: SELECT COUNT(*)
    DB-->>Repo: count
    Repo-->>Handler: false

    Handler->>+Aggregate: create(nationalId, personalInfo, contactInfo, address)
    Aggregate->>Aggregate: Generate PolicyHolderId
    Aggregate->>Aggregate: Validate business rules
    Aggregate->>Aggregate: Register PolicyHolderCreated event
    Aggregate-->>-Handler: PolicyHolder

    Handler->>+Repo: save(policyHolder)
    Repo->>DB: INSERT INTO policy_holders
    DB-->>Repo: success
    Repo-->>-Handler: savedPolicyHolder

    Handler->>Aggregate: getDomainEvents()
    Aggregate-->>Handler: List<DomainEvent>

    Handler->>+EventPub: publishAll(events)
    EventPub->>DB: INSERT INTO domain_events
    EventPub-->>-Handler: void

    Handler->>Handler: Map to PolicyHolderReadModel
    Handler-->>-Controller: PolicyHolderReadModel

    Controller-->>-Client: 201 Created + PolicyHolderResponse
```

### 查詢保戶時序圖

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as PolicyHolderController
    participant Handler as GetPolicyHolderQueryHandler
    participant Repo as PolicyHolderRepository
    participant Mapper as PolicyHolderMapper
    participant DB as Database

    Client->>+Controller: GET /api/v1/policyholders/{id}
    Controller->>+Handler: handle(GetPolicyHolderQuery)

    Handler->>+Repo: findById(policyHolderId)
    Repo->>+DB: SELECT * FROM policy_holders WHERE id = ?
    DB-->>-Repo: PolicyHolderJpaEntity
    Repo->>+Mapper: toDomain(entity)
    Mapper-->>-Repo: PolicyHolder
    Repo-->>-Handler: Optional<PolicyHolder>

    alt PolicyHolder found
        Handler->>Handler: Map to PolicyHolderReadModel
        Handler-->>Controller: PolicyHolderReadModel
        Controller-->>Client: 200 OK + PolicyHolderResponse
    else PolicyHolder not found
        Handler-->>Controller: throw PolicyHolderNotFoundException
        Controller-->>Client: 404 Not Found + ErrorResponse
    end

    deactivate Handler
    deactivate Controller
```

### 新增保單時序圖

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as PolicyHolderController
    participant Handler as AddPolicyCommandHandler
    participant Repo as PolicyHolderRepository
    participant Aggregate as PolicyHolder
    participant Policy as Policy
    participant EventPub as DomainEventPublisher
    participant DB as Database

    Client->>+Controller: POST /api/v1/policyholders/{id}/policies
    Controller->>Controller: Validate Request
    Controller->>+Handler: handle(AddPolicyCommand)

    Handler->>+Repo: findById(policyHolderId)
    Repo->>DB: SELECT * FROM policy_holders
    DB-->>Repo: entity
    Repo-->>-Handler: Optional<PolicyHolder>

    alt PolicyHolder not found
        Handler-->>Controller: throw PolicyHolderNotFoundException
        Controller-->>Client: 404 Not Found
    end

    Handler->>+Aggregate: isActive()
    Aggregate-->>-Handler: true/false

    alt PolicyHolder not active
        Handler-->>Controller: throw PolicyHolderNotActiveException
        Controller-->>Client: 400 Bad Request
    end

    Handler->>+Policy: create(type, premium, sumInsured, startDate, endDate)
    Policy->>Policy: Generate PolicyId
    Policy->>Policy: Validate dates
    Policy-->>-Handler: Policy

    Handler->>+Aggregate: addPolicy(policy)
    Aggregate->>Aggregate: Add to policies list
    Aggregate->>Aggregate: Register PolicyAdded event
    Aggregate-->>-Handler: void

    Handler->>+Repo: save(policyHolder)
    Repo->>DB: UPDATE policy_holders + INSERT policies
    Repo-->>-Handler: savedPolicyHolder

    Handler->>+EventPub: publishAll(events)
    EventPub->>DB: INSERT INTO domain_events
    EventPub-->>-Handler: void

    Handler-->>-Controller: PolicyReadModel
    Controller-->>-Client: 201 Created + PolicyResponse
```

### 刪除保戶時序圖

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Controller as PolicyHolderController
    participant Handler as DeletePolicyHolderCommandHandler
    participant Repo as PolicyHolderRepository
    participant Aggregate as PolicyHolder
    participant EventPub as DomainEventPublisher
    participant DB as Database

    Client->>+Controller: DELETE /api/v1/policyholders/{id}
    Controller->>+Handler: handle(DeletePolicyHolderCommand)

    Handler->>+Repo: findById(policyHolderId)
    Repo->>DB: SELECT * FROM policy_holders
    DB-->>Repo: entity
    Repo-->>-Handler: Optional<PolicyHolder>

    alt PolicyHolder not found
        Handler-->>Controller: throw PolicyHolderNotFoundException
        Controller-->>Client: 404 Not Found
    end

    Handler->>+Aggregate: isActive()
    Aggregate-->>-Handler: true/false

    alt Already inactive
        Handler-->>Controller: throw IllegalStateException
        Controller-->>Client: 400 Bad Request
    end

    Handler->>+Aggregate: deactivate()
    Aggregate->>Aggregate: Set status = INACTIVE
    Aggregate->>Aggregate: Register PolicyHolderDeleted event
    Aggregate-->>-Handler: void

    Handler->>+Repo: save(policyHolder)
    Repo->>DB: UPDATE policy_holders SET status = 'INACTIVE'
    Repo-->>-Handler: savedPolicyHolder

    Handler->>+EventPub: publishAll(events)
    EventPub->>DB: INSERT INTO domain_events
    EventPub-->>-Handler: void

    Handler-->>-Controller: void
    Controller-->>-Client: 204 No Content

    Note over DB: 軟刪除：資料保留，狀態改為 INACTIVE
```

---

## ER Diagram

```mermaid
erDiagram
    POLICY_HOLDERS {
        varchar(13) id PK "保戶編號 (PH + 10位數字)"
        varchar(10) national_id UK "身分證字號"
        varchar(50) name "姓名"
        varchar(10) gender "性別 (MALE/FEMALE)"
        date birth_date "出生日期"
        varchar(10) mobile_phone "手機號碼"
        varchar(100) email "電子郵件"
        varchar(5) zip_code "郵遞區號"
        varchar(10) city "縣市"
        varchar(10) district "區域"
        varchar(100) street "街道地址"
        varchar(20) status "狀態 (ACTIVE/INACTIVE/SUSPENDED)"
        timestamp created_at "建立時間"
        timestamp updated_at "更新時間"
        bigint version "樂觀鎖版本"
    }

    POLICIES {
        varchar(12) id PK "保單編號 (PO + 10位數字)"
        varchar(13) policy_holder_id FK "保戶編號"
        varchar(20) policy_type "保單類型"
        decimal(15-2) premium_amount "保費金額"
        decimal(15-2) sum_insured "保險金額"
        date start_date "生效日期"
        date end_date "到期日期"
        varchar(20) status "狀態 (ACTIVE/EXPIRED/CANCELLED)"
        timestamp created_at "建立時間"
        timestamp updated_at "更新時間"
    }

    DOMAIN_EVENTS {
        varchar(36) event_id PK "事件 ID (UUID)"
        varchar(50) aggregate_id "聚合 ID"
        varchar(50) aggregate_type "聚合類型"
        varchar(100) event_type "事件類型"
        clob event_data "事件資料 (JSON)"
        timestamp occurred_on "發生時間"
        boolean published "是否已發布"
        timestamp published_at "發布時間"
    }

    POLICY_HOLDERS ||--o{ POLICIES : "has"
    POLICY_HOLDERS ||--o{ DOMAIN_EVENTS : "generates"
```

---

## 狀態圖

### 保戶狀態轉換

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: 新增保戶

    ACTIVE --> ACTIVE: 修改資料
    ACTIVE --> INACTIVE: 軟刪除
    ACTIVE --> SUSPENDED: 停權處理

    SUSPENDED --> ACTIVE: 解除停權
    SUSPENDED --> INACTIVE: 軟刪除

    INACTIVE --> [*]: 資料保留但不可操作

    note right of ACTIVE
        可執行：
        - 修改聯絡資訊
        - 修改地址
        - 新增保單
        - 查詢保單
    end note

    note right of SUSPENDED
        禁止：
        - 所有修改操作
        - 新增保單
        允許：
        - 查詢操作
    end note

    note right of INACTIVE
        軟刪除狀態
        資料保留於資料庫
        禁止所有操作
    end note
```

### 保單狀態轉換

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: 新增保單

    ACTIVE --> EXPIRED: 到期日到達
    ACTIVE --> CANCELLED: 解約/終止

    EXPIRED --> [*]: 保單結束
    CANCELLED --> [*]: 保單結束

    note right of ACTIVE
        有效保單
        - 在保障期間內
        - 可查詢保單資訊
    end note

    note right of EXPIRED
        已到期保單
        - 超過到期日
        - 保障已結束
    end note

    note right of CANCELLED
        已取消保單
        - 主動解約
        - 停效處理
    end note
```

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
│   ├── event/                       # Domain Events
│   ├── service/                     # Domain Services
│   └── exception/                   # Domain Exceptions
│
├── application/                     # 🟢 Application Layer
│   ├── command/                     # Commands (Write)
│   ├── commandhandler/              # Command Handlers
│   ├── query/                       # Queries (Read)
│   ├── queryhandler/                # Query Handlers
│   ├── readmodel/                   # Read Models (DTOs)
│   └── port/
│       ├── input/                   # Input Ports
│       └── output/                  # Output Ports
│
└── infrastructure/                  # 🟠 Infrastructure Layer（最外層）
    ├── adapter/
    │   ├── input/rest/              # REST API Adapter
    │   └── output/
    │       ├── persistence/         # JPA Adapter
    │       └── event/               # Event Store Adapter
    ├── config/                      # Spring Configurations
    └── exception/                   # Global Exception Handler
```

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

# 產生覆蓋率報告
gradle test jacocoTestReport
```

---

## 錯誤代碼

| 錯誤代碼 | HTTP Status | 說明 |
|----------|-------------|------|
| `POLICY_HOLDER_NOT_FOUND` | 404 | 保戶不存在 |
| `POLICY_NOT_FOUND` | 404 | 保單不存在 |
| `POLICY_HOLDER_NOT_ACTIVE` | 400 | 保戶非活動狀態 |
| `VALIDATION_ERROR` | 400 | 輸入驗證錯誤 |
| `INVALID_ARGUMENT` | 400 | 非法參數 |
| `INTERNAL_ERROR` | 500 | 系統內部錯誤 |

---

## 授權

本專案採用 MIT 授權 - 詳見 [LICENSE](LICENSE) 檔案

---

## 貢獻者

Built with Claude Opus 4.5
