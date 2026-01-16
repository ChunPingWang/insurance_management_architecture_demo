# 保戶基本資料管理系統 - 技術規格文件 (TECH)

## 文件資訊

| 項目 | 內容 |
|------|------|
| 文件名稱 | 保戶基本資料管理系統技術規格 |
| 版本 | 1.0.0 |
| 建立日期 | 2025-01-15 |
| 狀態 | Draft |

---

## 1. 技術架構概述

### 1.1 架構原則

本系統採用以下架構原則與設計模式：

| 原則/模式 | 說明 |
|-----------|------|
| Domain-Driven Design (DDD) | 以領域模型為核心的設計方法 |
| Hexagonal Architecture | 六角形架構（端口與適配器） |
| CQRS Level 2 | 命令查詢職責分離（模型分離） |
| SOLID Principles | 物件導向設計原則 |
| Event-Driven | 事件驅動架構 |

### 1.2 技術堆疊

| 層級 | 技術選型 |
|------|----------|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| API Spec | OpenAPI 3.0 (Swagger) |
| Database | H2 Database (開發/測試) |
| ORM | Spring Data JPA |
| Build Tool | Gradle / Maven |
| Testing | JUnit 5, Mockito, ArchUnit |

---

## 2. 六角形架構設計

### 2.1 架構層級圖

```
┌─────────────────────────────────────────────────────────────────┐
│                      Infrastructure Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ REST API    │  │ H2 Database │  │ Event Store             │  │
│  │ (Adapter)   │  │ (Adapter)   │  │ (Adapter)               │  │
│  └──────┬──────┘  └──────┬──────┘  └───────────┬─────────────┘  │
│         │                │                     │                 │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌───────────▼─────────────┐  │
│  │ Input Port  │  │ Output Port │  │ Event Publisher Port    │  │
│  │ (Interface) │  │ (Interface) │  │ (Interface)             │  │
└──┼─────────────┼──┼─────────────┼──┼─────────────────────────┼──┘
   │             │  │             │  │                         │
┌──┼─────────────┼──┼─────────────┼──┼─────────────────────────┼──┐
│  │      Application Layer (Use Cases)                        │  │
│  │  ┌──────────────────┐  ┌──────────────────────────────┐   │  │
│  │  │ Command Handlers │  │ Query Handlers               │   │  │
│  │  │ (Write Side)     │  │ (Read Side)                  │   │  │
│  │  └────────┬─────────┘  └──────────────┬───────────────┘   │  │
│  │           │                           │                   │  │
└──┼───────────┼───────────────────────────┼───────────────────┼──┘
   │           │                           │                   │
┌──┼───────────┼───────────────────────────┼───────────────────┼──┐
│  │           │    Domain Layer           │                   │  │
│  │  ┌────────▼─────────────────────────────────────────┐     │  │
│  │  │              PolicyHolder (Aggregate Root)        │     │  │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌──────────┐  │     │  │
│  │  │  │ Policy      │  │ Address     │  │ Domain   │  │     │  │
│  │  │  │ (Entity)    │  │ (Value Obj) │  │ Events   │  │     │  │
│  │  │  └─────────────┘  └─────────────┘  └──────────┘  │     │  │
│  │  └──────────────────────────────────────────────────┘     │  │
│  │                                                           │  │
└──┼───────────────────────────────────────────────────────────┼──┘
   │                                                           │
   └───────────────────────────────────────────────────────────┘
```

### 2.2 層級職責與依賴規則

| 層級 | 職責 | 依賴方向 |
|------|------|----------|
| Domain Layer | 領域模型、業務邏輯、領域事件 | 無外部依賴（最內層） |
| Application Layer | 用例編排、命令/查詢處理 | 僅依賴 Domain Layer |
| Infrastructure Layer | 技術實作、外部整合 | 可依賴所有內層 |

**依賴規則**：
- ✅ Infrastructure → Application → Domain（允許）
- ❌ Domain → Application → Infrastructure（禁止）
- 內層透過 Interface（Port）與外層溝通

---

## 3. 領域模型設計 (Domain Layer)

### 3.1 聚合設計

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
│   │   └── email: String
│   ├── Address (Value Object)
│   │   ├── zipCode: String
│   │   ├── city: String
│   │   ├── district: String
│   │   └── street: String
│   ├── status: PolicyHolderStatus
│   ├── policies: List<Policy> (Entity Collection)
│   └── domainEvents: List<DomainEvent>
│
└── Policy (Entity within Aggregate)
    ├── PolicyId (Value Object - Identity)
    ├── policyNumber: String
    ├── policyType: PolicyType
    ├── policyName: String
    ├── premiumAmount: Money
    ├── sumInsured: Money
    ├── effectiveDate: LocalDate
    ├── expiryDate: LocalDate
    └── status: PolicyStatus
```

### 3.2 領域物件分類

#### 3.2.1 Aggregate Root

```java
// PolicyHolder.java - Aggregate Root
public class PolicyHolder extends AggregateRoot<PolicyHolderId> {
    
    private final PolicyHolderId id;
    private final NationalId nationalId;
    private PersonalInfo personalInfo;
    private ContactInfo contactInfo;
    private Address address;
    private PolicyHolderStatus status;
    private final List<Policy> policies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Factory Method - 確保聚合一致性
    public static PolicyHolder create(
        NationalId nationalId,
        PersonalInfo personalInfo,
        ContactInfo contactInfo,
        Address address
    ) {
        // 業務規則驗證
        validateAge(personalInfo.getBirthDate());
        
        PolicyHolder holder = new PolicyHolder(
            PolicyHolderId.generate(),
            nationalId,
            personalInfo,
            contactInfo,
            address
        );
        
        // 發布領域事件
        holder.registerEvent(new PolicyHolderCreated(holder));
        
        return holder;
    }
    
    // 業務方法
    public void updateContactInfo(ContactInfo newContactInfo) {
        this.contactInfo = newContactInfo;
        this.updatedAt = LocalDateTime.now();
        registerEvent(new PolicyHolderUpdated(this));
    }
    
    public void addPolicy(Policy policy) {
        validateCanAddPolicy();
        this.policies.add(policy);
        registerEvent(new PolicyAdded(this.id, policy));
    }
    
    // 不變量驗證
    private void validateCanAddPolicy() {
        if (this.status != PolicyHolderStatus.ACTIVE) {
            throw new PolicyHolderNotActiveException(this.id);
        }
    }
}
```

#### 3.2.2 Entity

```java
// Policy.java - Entity within Aggregate
public class Policy {
    
    private final PolicyId id;
    private final String policyNumber;
    private final PolicyType policyType;
    private final String policyName;
    private final Money premiumAmount;
    private final Money sumInsured;
    private final LocalDate effectiveDate;
    private final LocalDate expiryDate;
    private PolicyStatus status;
    
    // Entity 透過 ID 識別
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Policy)) return false;
        Policy policy = (Policy) o;
        return Objects.equals(id, policy.id);
    }
}
```

#### 3.2.3 Value Objects

```java
// Address.java - Value Object (Immutable)
public record Address(
    String zipCode,
    String city,
    String district,
    String street
) {
    // Value Object 驗證於建構時
    public Address {
        Objects.requireNonNull(zipCode, "ZipCode is required");
        Objects.requireNonNull(city, "City is required");
        Objects.requireNonNull(district, "District is required");
        Objects.requireNonNull(street, "Street is required");
    }
    
    // Value Object 透過所有屬性比較相等性
    // record 自動實作 equals/hashCode
}

// Money.java - Value Object
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
    
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
}

// PolicyHolderId.java - Identity Value Object
public record PolicyHolderId(String value) {
    
    private static final String PREFIX = "PH";
    
    public PolicyHolderId {
        Objects.requireNonNull(value);
        if (!value.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid PolicyHolderId format");
        }
    }
    
    public static PolicyHolderId generate() {
        return new PolicyHolderId(PREFIX + generateSequence());
    }
}
```

### 3.3 領域事件

```java
// DomainEvent.java - Base Event
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String aggregateId;
    private final String aggregateType;
    
    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }
}

// PolicyHolderCreated.java
public class PolicyHolderCreated extends DomainEvent {
    private final PolicyHolderSnapshot snapshot;
    
    public PolicyHolderCreated(PolicyHolder policyHolder) {
        super(policyHolder.getId().value(), "PolicyHolder");
        this.snapshot = PolicyHolderSnapshot.from(policyHolder);
    }
}

// PolicyHolderUpdated.java
public class PolicyHolderUpdated extends DomainEvent {
    private final PolicyHolderSnapshot beforeSnapshot;
    private final PolicyHolderSnapshot afterSnapshot;
}
```

### 3.4 領域服務

```java
// PolicyHolderDomainService.java
public interface PolicyHolderDomainService {
    
    // 跨聚合的業務邏輯
    boolean isNationalIdUnique(NationalId nationalId);
    
    // 複雜的業務規則驗證
    ValidationResult validatePolicyHolderCreation(CreatePolicyHolderCommand command);
}
```

---

## 4. 應用層設計 (Application Layer)

### 4.1 CQRS 設計 - Level 2

```
Application Layer
├── Command Side (Write Model)
│   ├── Commands
│   │   ├── CreatePolicyHolderCommand
│   │   ├── UpdatePolicyHolderCommand
│   │   ├── DeletePolicyHolderCommand
│   │   └── AddPolicyCommand
│   ├── Command Handlers
│   │   ├── CreatePolicyHolderCommandHandler
│   │   ├── UpdatePolicyHolderCommandHandler
│   │   ├── DeletePolicyHolderCommandHandler
│   │   └── AddPolicyCommandHandler
│   └── Ports (Output)
│       ├── PolicyHolderRepository
│       └── DomainEventPublisher
│
└── Query Side (Read Model)
    ├── Queries
    │   ├── GetPolicyHolderQuery
    │   ├── GetPolicyHolderByNationalIdQuery
    │   ├── SearchPolicyHoldersQuery
    │   └── GetPolicyHolderPoliciesQuery
    ├── Query Handlers
    │   ├── GetPolicyHolderQueryHandler
    │   └── SearchPolicyHoldersQueryHandler
    ├── Read Models (DTOs)
    │   ├── PolicyHolderReadModel
    │   ├── PolicyHolderListItemReadModel
    │   └── PolicyReadModel
    └── Ports (Output)
        └── PolicyHolderQueryRepository
```

### 4.2 Command 設計

```java
// CreatePolicyHolderCommand.java
public record CreatePolicyHolderCommand(
    String nationalId,
    String name,
    String gender,
    LocalDate birthDate,
    String mobilePhone,
    String email,
    AddressDto address
) implements Command<PolicyHolderCreatedResult> {}

// CreatePolicyHolderCommandHandler.java
@Service
@Transactional
public class CreatePolicyHolderCommandHandler 
    implements CommandHandler<CreatePolicyHolderCommand, PolicyHolderCreatedResult> {
    
    private final PolicyHolderRepository repository;          // Output Port
    private final DomainEventPublisher eventPublisher;        // Output Port
    private final PolicyHolderDomainService domainService;    // Domain Service
    
    @Override
    public PolicyHolderCreatedResult handle(CreatePolicyHolderCommand command) {
        // 1. 驗證業務規則
        NationalId nationalId = new NationalId(command.nationalId());
        if (!domainService.isNationalIdUnique(nationalId)) {
            throw new DuplicateNationalIdException(nationalId);
        }
        
        // 2. 建立聚合
        PolicyHolder policyHolder = PolicyHolder.create(
            nationalId,
            mapToPersonalInfo(command),
            mapToContactInfo(command),
            mapToAddress(command.address())
        );
        
        // 3. 持久化
        repository.save(policyHolder);
        
        // 4. 發布領域事件
        policyHolder.getDomainEvents().forEach(eventPublisher::publish);
        
        // 5. 回傳結果
        return new PolicyHolderCreatedResult(policyHolder.getId().value());
    }
}
```

### 4.3 Query 設計（獨立的 Read Model）

```java
// PolicyHolderReadModel.java - 專為查詢優化的模型
public record PolicyHolderReadModel(
    String policyHolderId,
    String nationalId,
    String name,
    String gender,
    LocalDate birthDate,
    String mobilePhone,
    String email,
    AddressReadModel address,
    String status,
    List<PolicyReadModel> policies,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

// GetPolicyHolderQueryHandler.java
@Service
@Transactional(readOnly = true)
public class GetPolicyHolderQueryHandler 
    implements QueryHandler<GetPolicyHolderQuery, PolicyHolderReadModel> {
    
    private final PolicyHolderQueryRepository queryRepository;  // Output Port (Read)
    
    @Override
    public PolicyHolderReadModel handle(GetPolicyHolderQuery query) {
        return queryRepository.findById(query.policyHolderId())
            .orElseThrow(() -> new PolicyHolderNotFoundException(query.policyHolderId()));
    }
}

// SearchPolicyHoldersQuery.java
public record SearchPolicyHoldersQuery(
    String name,
    String nationalId,
    String status,
    int page,
    int size,
    String sortBy,
    String sortDirection
) implements Query<Page<PolicyHolderListItemReadModel>> {}
```

### 4.4 Port 定義（Interface）

```java
// === Output Ports (Application Layer 定義，Infrastructure 實作) ===

// PolicyHolderRepository.java - Write Repository Port
public interface PolicyHolderRepository {
    void save(PolicyHolder policyHolder);
    Optional<PolicyHolder> findById(PolicyHolderId id);
    boolean existsByNationalId(NationalId nationalId);
    void delete(PolicyHolder policyHolder);
}

// PolicyHolderQueryRepository.java - Read Repository Port
public interface PolicyHolderQueryRepository {
    Optional<PolicyHolderReadModel> findById(String policyHolderId);
    Optional<PolicyHolderReadModel> findByNationalId(String nationalId);
    Page<PolicyHolderListItemReadModel> search(PolicyHolderSearchCriteria criteria);
}

// DomainEventPublisher.java - Event Publisher Port
public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}

// EventStore.java - Event Store Port
public interface EventStore {
    void append(DomainEvent event);
    List<DomainEvent> getEvents(String aggregateId);
    List<DomainEvent> getEventsByType(String eventType);
}
```

---

## 5. 基礎設施層設計 (Infrastructure Layer)

### 5.1 REST API Adapter (Input Adapter)

```java
// PolicyHolderController.java
@RestController
@RequestMapping("/api/v1/policyholders")
@Tag(name = "PolicyHolder", description = "保戶管理 API")
public class PolicyHolderController {
    
    private final CommandBus commandBus;
    private final QueryBus queryBus;
    
    @PostMapping
    @Operation(summary = "新增保戶")
    @ApiResponse(responseCode = "201", description = "保戶建立成功")
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> createPolicyHolder(
        @Valid @RequestBody CreatePolicyHolderRequest request
    ) {
        CreatePolicyHolderCommand command = mapper.toCommand(request);
        PolicyHolderCreatedResult result = commandBus.dispatch(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(mapper.toResponse(result)));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查詢保戶")
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> getPolicyHolder(
        @PathVariable String id
    ) {
        GetPolicyHolderQuery query = new GetPolicyHolderQuery(id);
        PolicyHolderReadModel readModel = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(mapper.toResponse(readModel)));
    }
    
    @GetMapping
    @Operation(summary = "查詢保戶列表")
    public ResponseEntity<ApiResponse<PageResponse<PolicyHolderListResponse>>> searchPolicyHolders(
        @Valid PolicyHolderSearchRequest request
    ) {
        SearchPolicyHoldersQuery query = mapper.toQuery(request);
        Page<PolicyHolderListItemReadModel> result = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(mapper.toPageResponse(result)));
    }
}
```

### 5.2 JPA Repository Adapter (Output Adapter)

```java
// === Write Repository Implementation ===

// PolicyHolderJpaRepository.java
public interface PolicyHolderJpaRepository extends JpaRepository<PolicyHolderJpaEntity, String> {
    boolean existsByNationalId(String nationalId);
}

// PolicyHolderRepositoryAdapter.java
@Repository
public class PolicyHolderRepositoryAdapter implements PolicyHolderRepository {
    
    private final PolicyHolderJpaRepository jpaRepository;
    private final PolicyHolderEntityMapper mapper;
    
    @Override
    public void save(PolicyHolder policyHolder) {
        PolicyHolderJpaEntity entity = mapper.toEntity(policyHolder);
        jpaRepository.save(entity);
    }
    
    @Override
    public Optional<PolicyHolder> findById(PolicyHolderId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByNationalId(NationalId nationalId) {
        return jpaRepository.existsByNationalId(nationalId.value());
    }
}

// === Read Repository Implementation ===

// PolicyHolderQueryRepositoryAdapter.java
@Repository
public class PolicyHolderQueryRepositoryAdapter implements PolicyHolderQueryRepository {
    
    private final PolicyHolderJpaRepository jpaRepository;
    private final PolicyHolderReadModelMapper mapper;
    
    @Override
    public Optional<PolicyHolderReadModel> findById(String policyHolderId) {
        return jpaRepository.findById(policyHolderId)
            .map(mapper::toReadModel);  // 直接映射到 Read Model
    }
    
    @Override
    public Page<PolicyHolderListItemReadModel> search(PolicyHolderSearchCriteria criteria) {
        Specification<PolicyHolderJpaEntity> spec = buildSpecification(criteria);
        Pageable pageable = buildPageable(criteria);
        
        return jpaRepository.findAll(spec, pageable)
            .map(mapper::toListItemReadModel);
    }
}
```

### 5.3 JPA Entity 設計

```java
// PolicyHolderJpaEntity.java
@Entity
@Table(name = "policy_holders")
public class PolicyHolderJpaEntity {
    
    @Id
    @Column(name = "policy_holder_id", length = 12)
    private String policyHolderId;
    
    @Column(name = "national_id", length = 10, unique = true, nullable = false)
    private String nationalId;
    
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;
    
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    @Column(name = "mobile_phone", length = 10, nullable = false)
    private String mobilePhone;
    
    @Column(name = "email", length = 100)
    private String email;
    
    // Embedded Value Object
    @Embedded
    private AddressEmbeddable address;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PolicyHolderStatus status;
    
    // One-to-Many with Policy Entity
    @OneToMany(mappedBy = "policyHolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyJpaEntity> policies = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

// AddressEmbeddable.java - Embedded Value Object
@Embeddable
public class AddressEmbeddable {
    
    @Column(name = "zip_code", length = 5)
    private String zipCode;
    
    @Column(name = "city", length = 10)
    private String city;
    
    @Column(name = "district", length = 10)
    private String district;
    
    @Column(name = "street", length = 100)
    private String street;
}
```

### 5.4 Event Store 實作

```java
// DomainEventJpaEntity.java
@Entity
@Table(name = "domain_events")
public class DomainEventJpaEntity {
    
    @Id
    @Column(name = "event_id", length = 36)
    private String eventId;
    
    @Column(name = "aggregate_id", length = 50, nullable = false)
    private String aggregateId;
    
    @Column(name = "aggregate_type", length = 50, nullable = false)
    private String aggregateType;
    
    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;
    
    @Column(name = "event_data", columnDefinition = "CLOB", nullable = false)
    private String eventData;  // JSON serialized
    
    @Column(name = "occurred_on", nullable = false)
    private LocalDateTime occurredOn;
    
    @Column(name = "published", nullable = false)
    private boolean published = false;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}

// JpaEventStore.java
@Repository
public class JpaEventStore implements EventStore {
    
    private final DomainEventJpaRepository repository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public void append(DomainEvent event) {
        DomainEventJpaEntity entity = new DomainEventJpaEntity();
        entity.setEventId(event.getEventId());
        entity.setAggregateId(event.getAggregateId());
        entity.setAggregateType(event.getAggregateType());
        entity.setEventType(event.getClass().getSimpleName());
        entity.setEventData(objectMapper.writeValueAsString(event));
        entity.setOccurredOn(event.getOccurredOn());
        entity.setPublished(false);
        
        repository.save(entity);
    }
}

// TransactionalDomainEventPublisher.java
@Service
public class TransactionalDomainEventPublisher implements DomainEventPublisher {
    
    private final EventStore eventStore;
    private final ApplicationEventPublisher springEventPublisher;
    
    @Override
    @Transactional
    public void publish(DomainEvent event) {
        // 1. 先持久化事件（確保事件不會遺失）
        eventStore.append(event);
        
        // 2. 發布到 Spring 應用程式內部（同步處理）
        springEventPublisher.publishEvent(event);
    }
}
```

---

## 6. 資料庫設計

### 6.1 ER Diagram

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

### 6.2 DDL Scripts

```sql
-- policy_holders table
CREATE TABLE policy_holders (
    policy_holder_id VARCHAR(12) PRIMARY KEY,
    national_id VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    birth_date DATE NOT NULL,
    mobile_phone VARCHAR(10) NOT NULL,
    email VARCHAR(100),
    zip_code VARCHAR(5),
    city VARCHAR(10),
    district VARCHAR(10),
    street VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policy_holders_national_id ON policy_holders(national_id);
CREATE INDEX idx_policy_holders_name ON policy_holders(name);
CREATE INDEX idx_policy_holders_status ON policy_holders(status);

-- policies table
CREATE TABLE policies (
    policy_id VARCHAR(36) PRIMARY KEY,
    policy_holder_id VARCHAR(12) NOT NULL,
    policy_number VARCHAR(20) NOT NULL UNIQUE,
    policy_type VARCHAR(20) NOT NULL,
    policy_name VARCHAR(100) NOT NULL,
    premium_amount DECIMAL(15,2) NOT NULL,
    sum_insured DECIMAL(15,2) NOT NULL,
    effective_date DATE NOT NULL,
    expiry_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_policies_policy_holder 
        FOREIGN KEY (policy_holder_id) REFERENCES policy_holders(policy_holder_id)
);

CREATE INDEX idx_policies_policy_holder_id ON policies(policy_holder_id);
CREATE INDEX idx_policies_policy_type ON policies(policy_type);
CREATE INDEX idx_policies_status ON policies(status);

-- domain_events table (Event Store)
CREATE TABLE domain_events (
    event_id VARCHAR(36) PRIMARY KEY,
    aggregate_id VARCHAR(50) NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_data CLOB NOT NULL,
    occurred_on TIMESTAMP NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP
);

CREATE INDEX idx_domain_events_aggregate ON domain_events(aggregate_id, aggregate_type);
CREATE INDEX idx_domain_events_type ON domain_events(event_type);
CREATE INDEX idx_domain_events_published ON domain_events(published);
```

---

## 7. 專案結構

```
policyholder-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/insurance/policyholder/
│   │   │       │
│   │   │       ├── domain/                          # Domain Layer
│   │   │       │   ├── model/
│   │   │       │   │   ├── aggregate/
│   │   │       │   │   │   └── PolicyHolder.java    # Aggregate Root
│   │   │       │   │   ├── entity/
│   │   │       │   │   │   └── Policy.java          # Entity
│   │   │       │   │   ├── valueobject/
│   │   │       │   │   │   ├── PolicyHolderId.java
│   │   │       │   │   │   ├── NationalId.java
│   │   │       │   │   │   ├── Address.java
│   │   │       │   │   │   ├── Money.java
│   │   │       │   │   │   └── PersonalInfo.java
│   │   │       │   │   └── enums/
│   │   │       │   │       ├── Gender.java
│   │   │       │   │       ├── PolicyHolderStatus.java
│   │   │       │   │       ├── PolicyType.java
│   │   │       │   │       └── PolicyStatus.java
│   │   │       │   ├── event/
│   │   │       │   │   ├── DomainEvent.java
│   │   │       │   │   ├── PolicyHolderCreated.java
│   │   │       │   │   ├── PolicyHolderUpdated.java
│   │   │       │   │   └── PolicyAdded.java
│   │   │       │   ├── service/
│   │   │       │   │   └── PolicyHolderDomainService.java
│   │   │       │   └── exception/
│   │   │       │       ├── DomainException.java
│   │   │       │       └── PolicyHolderNotActiveException.java
│   │   │       │
│   │   │       ├── application/                     # Application Layer
│   │   │       │   ├── command/
│   │   │       │   │   ├── CreatePolicyHolderCommand.java
│   │   │       │   │   ├── UpdatePolicyHolderCommand.java
│   │   │       │   │   ├── DeletePolicyHolderCommand.java
│   │   │       │   │   └── AddPolicyCommand.java
│   │   │       │   ├── commandhandler/
│   │   │       │   │   ├── CreatePolicyHolderCommandHandler.java
│   │   │       │   │   ├── UpdatePolicyHolderCommandHandler.java
│   │   │       │   │   ├── DeletePolicyHolderCommandHandler.java
│   │   │       │   │   └── AddPolicyCommandHandler.java
│   │   │       │   ├── query/
│   │   │       │   │   ├── GetPolicyHolderQuery.java
│   │   │       │   │   ├── GetPolicyHolderByNationalIdQuery.java
│   │   │       │   │   └── SearchPolicyHoldersQuery.java
│   │   │       │   ├── queryhandler/
│   │   │       │   │   ├── GetPolicyHolderQueryHandler.java
│   │   │       │   │   └── SearchPolicyHoldersQueryHandler.java
│   │   │       │   ├── readmodel/
│   │   │       │   │   ├── PolicyHolderReadModel.java
│   │   │       │   │   ├── PolicyHolderListItemReadModel.java
│   │   │       │   │   └── PolicyReadModel.java
│   │   │       │   ├── port/
│   │   │       │   │   ├── input/                   # Input Ports (Use Cases)
│   │   │       │   │   │   ├── CommandHandler.java
│   │   │       │   │   │   └── QueryHandler.java
│   │   │       │   │   └── output/                  # Output Ports
│   │   │       │   │       ├── PolicyHolderRepository.java
│   │   │       │   │       ├── PolicyHolderQueryRepository.java
│   │   │       │   │       ├── DomainEventPublisher.java
│   │   │       │   │       └── EventStore.java
│   │   │       │   ├── dto/
│   │   │       │   │   └── AddressDto.java
│   │   │       │   └── service/
│   │   │       │       └── PolicyHolderApplicationService.java
│   │   │       │
│   │   │       └── infrastructure/                  # Infrastructure Layer
│   │   │           ├── adapter/
│   │   │           │   ├── input/
│   │   │           │   │   └── rest/
│   │   │           │   │       ├── PolicyHolderController.java
│   │   │           │   │       ├── request/
│   │   │           │   │       │   ├── CreatePolicyHolderRequest.java
│   │   │           │   │       │   └── UpdatePolicyHolderRequest.java
│   │   │           │   │       ├── response/
│   │   │           │   │       │   ├── PolicyHolderResponse.java
│   │   │           │   │       │   └── ApiResponse.java
│   │   │           │   │       └── mapper/
│   │   │           │   │           └── PolicyHolderRestMapper.java
│   │   │           │   └── output/
│   │   │           │       ├── persistence/
│   │   │           │       │   ├── entity/
│   │   │           │       │   │   ├── PolicyHolderJpaEntity.java
│   │   │           │       │   │   ├── PolicyJpaEntity.java
│   │   │           │       │   │   └── DomainEventJpaEntity.java
│   │   │           │       │   ├── repository/
│   │   │           │       │   │   ├── PolicyHolderJpaRepository.java
│   │   │           │       │   │   └── DomainEventJpaRepository.java
│   │   │           │       │   ├── adapter/
│   │   │           │       │   │   ├── PolicyHolderRepositoryAdapter.java
│   │   │           │       │   │   └── PolicyHolderQueryRepositoryAdapter.java
│   │   │           │       │   └── mapper/
│   │   │           │       │       ├── PolicyHolderEntityMapper.java
│   │   │           │       │       └── PolicyHolderReadModelMapper.java
│   │   │           │       └── event/
│   │   │           │           ├── JpaEventStore.java
│   │   │           │           └── TransactionalDomainEventPublisher.java
│   │   │           ├── config/
│   │   │           │   ├── JpaConfig.java
│   │   │           │   ├── OpenApiConfig.java
│   │   │           │   └── BeanConfig.java
│   │   │           └── exception/
│   │   │               └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/
│   │           └── migration/
│   │               └── V1__init_schema.sql
│   │
│   └── test/
│       └── java/
│           └── com/insurance/policyholder/
│               ├── domain/
│               │   └── model/
│               │       └── PolicyHolderTest.java
│               ├── application/
│               │   └── commandhandler/
│               │       └── CreatePolicyHolderCommandHandlerTest.java
│               ├── infrastructure/
│               │   └── adapter/
│               │       └── rest/
│               │           └── PolicyHolderControllerTest.java
│               └── architecture/
│                   └── ArchitectureTest.java        # ArchUnit Tests
│
├── build.gradle
├── settings.gradle
├── README.md
├── PRD.md
└── TECH.md
```

---

## 8. SOLID 原則遵循說明

### 8.1 Single Responsibility Principle (SRP)

| 類別 | 單一職責 |
|------|----------|
| PolicyHolder | 管理保戶聚合的業務邏輯與不變量 |
| CreatePolicyHolderCommandHandler | 處理建立保戶的用例 |
| PolicyHolderRepositoryAdapter | 負責保戶持久化的技術實作 |
| PolicyHolderController | 處理 HTTP 請求與回應轉換 |

### 8.2 Open/Closed Principle (OCP)

- 新增保單類型只需擴展 `PolicyType` 列舉
- 新增查詢條件透過 `Specification` 模式擴展
- 新增事件處理器不影響現有程式碼

### 8.3 Liskov Substitution Principle (LSP)

- 所有 Repository 實作可替換為測試用的 Mock
- 所有 Adapter 可替換為不同技術實作

### 8.4 Interface Segregation Principle (ISP)

- `PolicyHolderRepository`（Write）與 `PolicyHolderQueryRepository`（Read）分離
- Command 與 Query Handler 分離

### 8.5 Dependency Inversion Principle (DIP)

- Domain/Application Layer 依賴抽象（Port/Interface）
- Infrastructure Layer 實作這些抽象
- 使用依賴注入（Spring DI）

---

## 9. 架構測試 (ArchUnit)

```java
@AnalyzeClasses(packages = "com.insurance.policyholder")
public class ArchitectureTest {

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

    @ArchTest
    static final ArchRule controllers_should_only_call_application_layer =
        classes()
            .that().resideInAPackage("..adapter.input.rest..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(
                "..application..",
                "..adapter.input.rest..",
                "java..",
                "javax..",
                "org.springframework.."
            );
}
```

---

## 10. API 規格 (OpenAPI 3.0)

詳見專案啟動後的 Swagger UI：`http://localhost:8080/swagger-ui.html`

### 10.1 API Endpoints Summary

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | /api/v1/policyholders | 新增保戶 | CreatePolicyHolderRequest | PolicyHolderResponse |
| PUT | /api/v1/policyholders/{id} | 修改保戶 | UpdatePolicyHolderRequest | PolicyHolderResponse |
| DELETE | /api/v1/policyholders/{id} | 刪除保戶 | - | - |
| GET | /api/v1/policyholders/{id} | 查詢保戶 | - | PolicyHolderResponse |
| GET | /api/v1/policyholders | 保戶列表 | Query Params | Page<PolicyHolderListResponse> |
| POST | /api/v1/policyholders/{id}/policies | 新增保單 | AddPolicyRequest | PolicyResponse |
| GET | /api/v1/policyholders/{id}/policies | 保戶保單 | - | List<PolicyResponse> |

---

## 附錄 A：技術決策記錄 (ADR)

### ADR-001: 採用 CQRS Level 2

**狀態**: Accepted

**背景**: 需要在簡單性與可擴展性之間取得平衡

**決策**: 採用 CQRS Level 2（模型分離，同庫）

**原因**:
- 開發環境使用 H2，不適合資料庫分離
- Read Model 與 Write Model 分離有助於查詢優化
- 為未來升級到 Level 3 預留空間

### ADR-002: 領域事件持久化策略

**狀態**: Accepted

**背景**: 需要支援事件驅動架構

**決策**: 事件先持久化到 Event Store，再同步發布

**原因**:
- 確保事件不會遺失
- 支援事件重播
- 為未來的事件溯源預留基礎
