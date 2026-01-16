# Implementation Plan: 保戶基本資料管理系統

**Branch**: `001-policyholder-management` | **Date**: 2026-01-16 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-policyholder-management/spec.md`

## Summary

本系統提供保戶基本資料的完整生命週期管理（CRUD），支援多種保單類型的關聯管理，並建立事件驅動的架構基礎。系統採用 DDD + 六角形架構 + CQRS Level 2 設計，確保領域邏輯與技術實作解耦，同時支援大型資料規模（保戶 > 100,000 筆，保單 > 500,000 筆）。

## Technical Context

**Language/Version**: Java 17+
**Primary Dependencies**: Spring Boot 3.x, Spring Data JPA, OpenAPI 3.0 (Swagger)
**Storage**: H2 Database (開發/測試環境)
**Testing**: JUnit 5, Mockito, ArchUnit, Cucumber
**Target Platform**: Linux Server / Container
**Project Type**: Single backend service (REST API)
**Performance Goals**: API 回應 P95 < 200ms, 查詢 P95 < 500ms, 100 concurrent users
**Constraints**: 敏感資料加密儲存, 領域事件持久化, 軟刪除機制
**Scale/Scope**: 保戶 > 100,000 筆, 保單 > 500,000 筆

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| 原則 | 狀態 | 說明 |
|------|------|------|
| I. DDD | ✅ PASS | 聚合根 (PolicyHolder)、實體 (Policy)、值物件 (Address, Money) 已明確定義 |
| II. SOLID | ✅ PASS | Command/Query 分離、Repository 介面隔離、依賴注入設計 |
| III. Hexagonal Architecture | ✅ PASS | Port/Adapter 分離，Application 不依賴 Infrastructure |
| IV. TDD/BDD | ✅ PASS | 規劃 JUnit 5 單元測試 + Cucumber BDD 測試 |
| V. Code Quality | ✅ PASS | 遵循命名規範與套件結構 |

**依賴方向驗證：**
- ✅ Infrastructure → Application → Domain
- ✅ Domain Layer 無框架依賴（純 Java）
- ✅ Application Layer 僅依賴 Port 介面

## Project Structure

### Documentation (this feature)

```text
specs/001-policyholder-management/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── domain-model.md      # Phase 1 output (DDD Domain Model)
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (OpenAPI specs)
└── tasks.md             # Phase 2 output (/speckit.tasks)
```

### Source Code (repository root)

```text
src/main/java/com/insurance/policyholder/
├── domain/                          # Domain Layer (純 Java)
│   ├── model/
│   │   ├── aggregate/
│   │   │   └── PolicyHolder.java    # Aggregate Root
│   │   ├── entity/
│   │   │   └── Policy.java          # Entity
│   │   ├── valueobject/
│   │   │   ├── PolicyHolderId.java
│   │   │   ├── NationalId.java
│   │   │   ├── Address.java
│   │   │   ├── Money.java
│   │   │   └── PersonalInfo.java
│   │   └── enums/
│   │       ├── Gender.java
│   │       ├── PolicyHolderStatus.java
│   │       ├── PolicyType.java
│   │       └── PolicyStatus.java
│   ├── event/
│   │   ├── DomainEvent.java
│   │   ├── PolicyHolderCreated.java
│   │   ├── PolicyHolderUpdated.java
│   │   ├── PolicyHolderDeleted.java
│   │   └── PolicyAdded.java
│   ├── service/
│   │   └── PolicyHolderDomainService.java
│   └── exception/
│       ├── DomainException.java
│       └── PolicyHolderNotActiveException.java
│
├── application/                     # Application Layer
│   ├── command/
│   │   ├── CreatePolicyHolderCommand.java
│   │   ├── UpdatePolicyHolderCommand.java
│   │   ├── DeletePolicyHolderCommand.java
│   │   └── AddPolicyCommand.java
│   ├── commandhandler/
│   │   ├── CreatePolicyHolderCommandHandler.java
│   │   ├── UpdatePolicyHolderCommandHandler.java
│   │   ├── DeletePolicyHolderCommandHandler.java
│   │   └── AddPolicyCommandHandler.java
│   ├── query/
│   │   ├── GetPolicyHolderQuery.java
│   │   ├── GetPolicyHolderByNationalIdQuery.java
│   │   └── SearchPolicyHoldersQuery.java
│   ├── queryhandler/
│   │   ├── GetPolicyHolderQueryHandler.java
│   │   └── SearchPolicyHoldersQueryHandler.java
│   ├── readmodel/
│   │   ├── PolicyHolderReadModel.java
│   │   ├── PolicyHolderListItemReadModel.java
│   │   └── PolicyReadModel.java
│   └── port/
│       ├── input/
│       │   ├── CommandHandler.java
│       │   └── QueryHandler.java
│       └── output/
│           ├── PolicyHolderRepository.java
│           ├── PolicyHolderQueryRepository.java
│           ├── DomainEventPublisher.java
│           └── EventStore.java
│
└── infrastructure/                  # Infrastructure Layer
    ├── adapter/
    │   ├── input/
    │   │   └── rest/
    │   │       ├── PolicyHolderController.java
    │   │       ├── request/
    │   │       └── response/
    │   └── output/
    │       ├── persistence/
    │       │   ├── entity/
    │       │   ├── repository/
    │       │   └── adapter/
    │       └── event/
    ├── config/
    └── exception/
        └── GlobalExceptionHandler.java

src/test/java/com/insurance/policyholder/
├── domain/
├── application/
├── infrastructure/
└── architecture/
    └── ArchitectureTest.java        # ArchUnit Tests
```

**Structure Decision**: 採用 TECH.md 中定義的六角形架構，單一 Spring Boot 專案，分為 Domain / Application / Infrastructure 三層。

## Complexity Tracking

> 無 Constitution 違規需要說明。

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| (無) | - | - |
