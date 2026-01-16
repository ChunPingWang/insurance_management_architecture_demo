<!--
================================================================================
SYNC IMPACT REPORT
================================================================================
Version Change: 0.0.0 → 1.0.0 (MAJOR - initial ratification from constitution_v2.md)

Modified Principles: N/A (initial creation)

Added Sections:
- Core Principles (5 principles derived from constitution_v2.md)
- Hexagonal Architecture section with explicit dependency prohibition
- Development Workflow section with TDD/BDD requirements
- Governance section with amendment procedures

Removed Sections: N/A (initial creation)

Templates Requiring Updates:
- .specify/templates/plan-template.md ✅ (no changes needed - Constitution Check section already exists)
- .specify/templates/spec-template.md ✅ (no changes needed - requirements format compatible)
- .specify/templates/tasks-template.md ✅ (no changes needed - phase structure compatible)

Follow-up TODOs: None

Source Document: constitution_v2.md (version 2.0, dated 2026-01-04)
================================================================================
-->

# 銀行客戶優惠系統 Constitution

## Core Principles

### I. Domain-Driven Design (DDD)

核心業務邏輯必須以領域模型為中心進行設計與實作。

- 使用通用語言 (Ubiquitous Language) 命名所有類別、方法與變數
- 領域層 (Domain Layer) 必須保持純淨，禁止任何框架依賴
- 實體 (Entity)、值物件 (Value Object)、領域服務 (Domain Service) 必須明確區分
- 領域事件 (Domain Event) 用於跨聚合 (Aggregate) 通訊

### II. SOLID Principles

所有物件導向設計必須遵循 SOLID 原則。

- **單一職責 (SRP)**: 每個類別只有一個改變的理由
- **開放封閉 (OCP)**: 對擴展開放，對修改封閉；透過介面擴展功能
- **里氏替換 (LSP)**: 子類別必須能完全替換父類別
- **介面隔離 (ISP)**: 細粒度介面優於肥大介面
- **依賴反轉 (DIP)**: 高層模組依賴抽象，不依賴具體實作

### III. Hexagonal Architecture (Ports and Adapters)

系統架構必須遵循六角形架構，確保領域邏輯與技術實作解耦。

**分層結構：**

```
┌─────────────────────────────────────────────────────────────────┐
│                     Infrastructure Layer                         │
│  (REST Controller, Database, External API, Drools, Excel Parser) │
│                              │                                   │
│                     implements Port                              │
│                              ▼                                   │
├─────────────────────────────────────────────────────────────────┤
│                      Application Layer                           │
│           (Application Service, Use Case, Port Interface)        │
│                              │                                   │
│                           依賴                                   │
│                              ▼                                   │
├─────────────────────────────────────────────────────────────────┤
│                        Domain Layer                              │
│    (Entity, Value Object, Domain Service, Domain Event)          │
│                     【最內層，無外部依賴】                         │
└─────────────────────────────────────────────────────────────────┘
```

**依賴方向規則 (NON-NEGOTIABLE)：**

| 依賴方向 | 允許 |
|----------|------|
| Infrastructure → Application | ✅ |
| Application → Domain | ✅ |
| Application → Infrastructure | ❌ **禁止** |
| Domain → Application | ❌ **禁止** |
| Domain → Infrastructure | ❌ **禁止** |

**Application Layer 禁止依賴 Infrastructure Layer 的理由：**
- 確保業務邏輯可獨立於技術實作進行測試
- 允許技術實作替換而不影響應用服務
- Port 介面由 Application Layer 定義，Adapter 由 Infrastructure Layer 實作
- 違反此規則將導致緊耦合，破壞架構彈性

**Domain Layer 規範：**
- ❌ 禁止使用 Spring 註解 (@Service, @Component, @Autowired)
- ❌ 禁止依賴 JPA/Hibernate 註解 (@Entity, @Table)
- ❌ 禁止使用任何框架特定的類別
- ❌ 禁止直接存取資料庫或外部服務
- ✅ 使用純 Java 類別
- ✅ 包含業務邏輯與驗證
- ✅ 保持可測試性（不需 Spring Context）

### IV. Test-Driven Development (TDD / BDD)

測試優先開發是不可協商的基本原則。

**TDD 循環：**
1. **Red**: 撰寫失敗的測試
2. **Green**: 撰寫最少程式碼使測試通過
3. **Refactor**: 重構程式碼，保持測試通過

**測試層級要求：**

| 測試類型 | 範圍 | 框架 | 覆蓋率要求 |
|----------|------|------|-----------|
| 單元測試 | 單一類別/方法 | JUnit 5 + Mockito | ≥ 80% |
| 整合測試 | 多個元件協作 | Spring Boot Test + Testcontainers | 關鍵路徑 |
| BDD 測試 | 業務場景 | Cucumber | 100% 場景通過 |

**BDD Step Definitions 規範：**
- ✅ Step 註解必須使用英文套件：`io.cucumber.java.en.*` (@Given, @When, @Then, @And)
- ❌ 禁止使用中文套件：`io.cucumber.java.zh_tw.*`
- ✅ 註解內的 Step 文字可使用中文

### V. Code Quality

程式碼品質標準必須在所有開發階段維持。

**命名規範：**

| 類型 | 命名規則 | 範例 |
|------|----------|------|
| 類別 | PascalCase | `DiscountService`, `UsageRecord` |
| 介面 | PascalCase | `DiscountRuleEngine`, `UsageRecordRepository` |
| 方法 | camelCase，動詞開頭 | `evaluate()`, `findByKey()`, `consumeUsage()` |
| 常數 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT` |
| 變數 | camelCase | `discountAmount`, `remainingCount` |
| 資料表 | UPPER_SNAKE_CASE | `DECISION_TREE`, `USAGE_COUNT_DTL` |

**套件結構：**

```
com.bank.discount
├── domain                    # 領域層
│   ├── model
│   │   ├── entity           # 實體
│   │   └── valueobject      # 值物件
│   ├── service              # 領域服務
│   └── exception            # 領域例外
├── application              # 應用層
│   ├── service              # 應用服務
│   ├── usecase              # 使用案例
│   └── port                 # 埠介面
│       ├── in               # 輸入埠
│       └── out              # 輸出埠
└── infrastructure           # 基礎設施層
    ├── adapter
    │   ├── in               # 輸入轉接器
    │   └── out              # 輸出轉接器
    └── config               # 配置
```

## Hexagonal Architecture Enforcement

此區塊強調 Application Layer 與 Infrastructure Layer 的依賴禁止規則。

### Port 與 Adapter 關係

```java
// Port (定義於 Application Layer)
public interface UsageRecordRepository {
    Optional<UsageRecord> findByKey(UsageRecordKey key);
    void save(UsageRecord record);
}

// Adapter (實作於 Infrastructure Layer)
@Repository
public class JpaUsageRecordRepository implements UsageRecordRepository {
    // JPA 實作細節
}

// Application Service (依賴 Port 介面，NOT Adapter)
@Service
public class DiscountServiceImpl implements DiscountService {
    private final UsageRecordRepository repository; // ✅ 依賴介面
    // private final JpaUsageRecordRepository jpaRepo; // ❌ 禁止直接依賴 Adapter
}
```

### 違規檢測指標

以下 import 模式在 Application Layer 中視為違規：

- `import ...infrastructure.*`
- `import ...adapter.*`
- `import org.springframework.data.jpa.*`
- `import javax.persistence.*`

## Development Workflow

### 新功能開發流程

1. 撰寫 Feature 檔案 (BDD Gherkin 語法)
2. 撰寫 Step Definitions (使用英文註解套件)
3. 撰寫單元測試 (TDD Red)
4. 實作 Domain Layer (純 Java)
5. 實作 Application Layer (定義 Port 介面)
6. 實作 Infrastructure Layer (Adapter)
7. 確保所有測試通過 (TDD Green)
8. 重構 (TDD Refactor)

### Code Review 檢查清單

- [ ] 分層依賴是否正確？
- [ ] Application Layer 是否無 Infrastructure 依賴？
- [ ] Domain Layer 是否無框架註解？
- [ ] 是否有適當的測試？
- [ ] 命名是否清晰？
- [ ] 是否遵循 SOLID 原則？

## Governance

### 憲章效力

本憲章為專案開發的最高指導原則，所有開發人員必須嚴格遵守。

### 修訂程序

1. 提出修訂提案並說明理由
2. 經團隊討論並取得共識
3. 更新憲章版本號 (遵循語意化版本)
4. 更新相關文件與模板
5. 通知所有開發人員

### 版本控制

- **MAJOR**: 核心原則變更或移除
- **MINOR**: 新增原則或重大擴充
- **PATCH**: 澄清、措辭修正、非語意性調整

### 合規審查

- 所有 PR 必須通過分層依賴檢查
- 每季進行架構合規審查
- 使用 `.specify/memory/constitution.md` 作為參考文件

**Version**: 1.0.0 | **Ratified**: 2026-01-16 | **Last Amended**: 2026-01-16
