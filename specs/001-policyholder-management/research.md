# Research: 保戶基本資料管理系統

**Date**: 2026-01-16
**Feature**: 001-policyholder-management

## 研究項目

本文件記錄 Phase 0 研究階段的技術決策與最佳實踐調研結果。由於 TECH.md 已提供完整的技術規格，本研究主要確認技術選型的適用性並補充實作細節。

---

## 1. 六角形架構最佳實踐

### Decision
採用 TECH.md 定義的六角形架構（Hexagonal Architecture / Ports and Adapters）。

### Rationale
- 符合 Constitution 原則 III（Hexagonal Architecture）
- 確保 Domain Layer 無框架依賴，可獨立測試
- Application Layer 僅依賴 Port 介面，不依賴 Infrastructure
- 支援技術實作替換（如從 H2 遷移至 PostgreSQL）

### Alternatives Considered
| 方案 | 優點 | 缺點 | 決策 |
|------|------|------|------|
| 傳統三層架構 | 簡單直接 | 領域邏輯易與技術耦合 | 拒絕 |
| Clean Architecture | 類似六角形 | 增加額外層級 | 不採用 |
| 六角形架構 | 清晰的依賴方向 | 初期設置較複雜 | **採用** |

---

## 2. CQRS Level 2 實作策略

### Decision
採用 CQRS Level 2（模型分離，同一資料庫）。

### Rationale
- Write Model (Domain Model) 專注於業務邏輯與不變量
- Read Model (DTO/ReadModel) 專為查詢優化
- 開發環境使用 H2，不適合資料庫分離（Level 3）
- 為未來升級至 Event Sourcing 預留空間

### Implementation Details
```
Write Side:
  Command → CommandHandler → Domain Model → Repository.save()

Read Side:
  Query → QueryHandler → QueryRepository → ReadModel (DTO)
```

### Alternatives Considered
| Level | 說明 | 適用場景 | 決策 |
|-------|------|----------|------|
| Level 1 | 單一模型 | 簡單 CRUD | 不採用 |
| Level 2 | 模型分離 | 中等複雜度 | **採用** |
| Level 3 | 資料庫分離 | 高讀寫比 | 未來可升級 |

---

## 3. 領域事件處理策略

### Decision
事件先持久化至 Event Store，再同步發布到 Spring ApplicationEventPublisher。

### Rationale
- 確保事件不會遺失（持久化優先）
- 支援事件重播與稽核追蹤
- 符合 FR-018 ~ FR-021 的事件發布需求
- 為未來的事件溯源（Event Sourcing）預留基礎

### Implementation Details
```java
@Transactional
public void publish(DomainEvent event) {
    // 1. 先持久化
    eventStore.append(event);

    // 2. 再發布（同步）
    springEventPublisher.publishEvent(event);
}
```

---

## 4. 大型資料規模處理策略

### Decision
針對 10 萬+ 保戶、50 萬+ 保單的規模進行索引與分頁優化。

### Rationale
- Spec 明確指出資料規模為「大型」
- 需要支援 P95 < 500ms 的查詢效能

### Implementation Details

**資料庫索引策略：**
```sql
-- 高頻查詢索引
CREATE INDEX idx_policy_holders_national_id ON policy_holders(national_id);
CREATE INDEX idx_policy_holders_name ON policy_holders(name);
CREATE INDEX idx_policy_holders_status ON policy_holders(status);

-- 保單查詢索引
CREATE INDEX idx_policies_policy_holder_id ON policies(policy_holder_id);
CREATE INDEX idx_policies_policy_type ON policies(policy_type);
CREATE INDEX idx_policies_status ON policies(status);
```

**分頁策略：**
- 使用 Spring Data JPA 的 `Pageable` 與 `Page<T>`
- 預設頁面大小：20 筆
- 最大頁面大小：100 筆

---

## 5. 身分證字號驗證與加密

### Decision
採用台灣身分證字號驗證演算法，敏感資料加密儲存。

### Rationale
- FR-002 要求驗證身分證格式
- SC-006 要求敏感資料保護

### Implementation Details

**驗證規則：**
- 格式：首位英文（A-Z）+ 9 位數字
- 檢查碼驗證：加權計算

**加密策略（未來實作）：**
- 儲存時使用 AES-256 加密
- 查詢時透過加密後的值比對
- 或使用資料庫層級的透明資料加密（TDE）

---

## 6. 樂觀鎖機制

### Decision
使用 JPA `@Version` 註解實作樂觀鎖。

### Rationale
- Edge Case 明確要求處理並發編輯衝突
- 樂觀鎖適合讀多寫少的場景

### Implementation Details
```java
@Entity
public class PolicyHolderJpaEntity {
    @Version
    private Long version;
}
```

當版本衝突時，拋出 `OptimisticLockingFailureException`，由 GlobalExceptionHandler 轉換為使用者友善訊息。

---

## 7. 測試策略

### Decision
採用 TDD + BDD 雙軌測試策略。

### Rationale
- Constitution 原則 IV 要求 TDD/BDD
- SC-008 要求 80% 單元測試覆蓋率

### Test Layers
| 測試類型 | 工具 | 覆蓋目標 |
|----------|------|----------|
| 單元測試 | JUnit 5 + Mockito | Domain, Application Layer |
| 架構測試 | ArchUnit | 依賴方向驗證 |
| 整合測試 | Spring Boot Test + H2 | Repository, Controller |
| BDD 測試 | Cucumber | User Story 驗收場景 |

---

## 8. API 設計原則

### Decision
採用 RESTful API 設計，遵循 Richardson Maturity Model Level 2。

### Rationale
- TECH.md 明確定義 API 端點
- OpenAPI 3.0 規範文件化

### API Conventions
| 操作 | HTTP Method | URL Pattern | 狀態碼 |
|------|-------------|-------------|--------|
| 新增 | POST | /api/v1/policyholders | 201 |
| 查詢單一 | GET | /api/v1/policyholders/{id} | 200 |
| 查詢列表 | GET | /api/v1/policyholders | 200 |
| 修改 | PUT | /api/v1/policyholders/{id} | 200 |
| 刪除 | DELETE | /api/v1/policyholders/{id} | 204 |

---

## 結論

所有技術選型與最佳實踐已確認，無 NEEDS CLARIFICATION 項目。可進入 Phase 1 設計階段。

### 關鍵決策摘要

| 項目 | 決策 |
|------|------|
| 架構模式 | 六角形架構 (Hexagonal) |
| CQRS Level | Level 2 (模型分離) |
| 事件處理 | 先持久化再發布 |
| 並發控制 | 樂觀鎖 (@Version) |
| 測試策略 | TDD + BDD + ArchUnit |
| API 風格 | RESTful Level 2 |
