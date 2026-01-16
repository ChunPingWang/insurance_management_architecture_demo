# Tasks: 保戶基本資料管理系統

**Input**: Design documents from `/specs/001-policyholder-management/`
**Prerequisites**: plan.md ✅, spec.md ✅, domain-model.md ✅, contracts/openapi.yaml ✅, research.md ✅, quickstart.md ✅

**Tests**: 包含 TDD/BDD 測試（依據 Constitution 原則 IV 與 SC-008 要求 80% 覆蓋率）

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

Base path: `src/main/java/com/insurance/policyholder/`
Test path: `src/test/java/com/insurance/policyholder/`

---

## Phase 1: Setup (專案初始化)

**Purpose**: Project initialization and basic structure

- [x] T001 Create Spring Boot project with Gradle and dependencies per quickstart.md
- [x] T002 [P] Create package structure per plan.md (domain, application, infrastructure layers)
- [x] T003 [P] Configure application.yml with H2, JPA, and SpringDoc settings
- [x] T004 [P] Setup ArchUnit dependency for architecture tests in build.gradle
- [x] T005 [P] Setup Cucumber dependency for BDD tests in build.gradle

---

## Phase 2: Foundational (核心基礎建設)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Architecture Tests (First - TDD)

- [x] T006 Create ArchitectureTest for hexagonal architecture validation in src/test/java/com/insurance/policyholder/architecture/ArchitectureTest.java

### Domain Layer - Enums & Base Classes

- [x] T007 [P] Create Gender enum in src/main/java/com/insurance/policyholder/domain/model/enums/Gender.java
- [x] T008 [P] Create PolicyHolderStatus enum in src/main/java/com/insurance/policyholder/domain/model/enums/PolicyHolderStatus.java
- [x] T009 [P] Create PolicyType enum in src/main/java/com/insurance/policyholder/domain/model/enums/PolicyType.java
- [x] T010 [P] Create PolicyStatus enum in src/main/java/com/insurance/policyholder/domain/model/enums/PolicyStatus.java
- [x] T011 [P] Create DomainEvent base class in src/main/java/com/insurance/policyholder/domain/event/DomainEvent.java
- [x] T012 [P] Create DomainException base class in src/main/java/com/insurance/policyholder/domain/exception/DomainException.java

### Domain Layer - Value Objects (with tests first)

- [x] T013 [P] Create PolicyHolderIdTest in src/test/java/com/insurance/policyholder/domain/model/valueobject/PolicyHolderIdTest.java
- [x] T014 [P] Create NationalIdTest with Taiwan ID validation in src/test/java/com/insurance/policyholder/domain/model/valueobject/NationalIdTest.java
- [x] T015 [P] Create AddressTest in src/test/java/com/insurance/policyholder/domain/model/valueobject/AddressTest.java
- [x] T016 [P] Create MoneyTest in src/test/java/com/insurance/policyholder/domain/model/valueobject/MoneyTest.java
- [x] T017 [P] Create PersonalInfoTest in src/test/java/com/insurance/policyholder/domain/model/valueobject/PersonalInfoTest.java
- [x] T018 [P] Create ContactInfoTest in src/test/java/com/insurance/policyholder/domain/model/valueobject/ContactInfoTest.java
- [x] T019 [P] Implement PolicyHolderId value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/PolicyHolderId.java
- [x] T020 [P] Implement NationalId value object with Taiwan ID validation in src/main/java/com/insurance/policyholder/domain/model/valueobject/NationalId.java
- [x] T021 [P] Implement Address value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/Address.java
- [x] T022 [P] Implement Money value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/Money.java
- [x] T023 [P] Implement PersonalInfo value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/PersonalInfo.java
- [x] T024 [P] Implement ContactInfo value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/ContactInfo.java
- [x] T025 [P] Create PolicyId value object in src/main/java/com/insurance/policyholder/domain/model/valueobject/PolicyId.java

### Application Layer - Ports (Interfaces)

- [x] T026 [P] Create CommandHandler interface in src/main/java/com/insurance/policyholder/application/port/input/CommandHandler.java
- [x] T027 [P] Create QueryHandler interface in src/main/java/com/insurance/policyholder/application/port/input/QueryHandler.java
- [x] T028 [P] Create PolicyHolderRepository port in src/main/java/com/insurance/policyholder/application/port/output/PolicyHolderRepository.java
- [x] T029 [P] Create PolicyHolderQueryRepository port in src/main/java/com/insurance/policyholder/application/port/output/PolicyHolderQueryRepository.java
- [x] T030 [P] Create DomainEventPublisher port in src/main/java/com/insurance/policyholder/application/port/output/DomainEventPublisher.java
- [x] T031 [P] Create EventStore port in src/main/java/com/insurance/policyholder/application/port/output/EventStore.java

### Infrastructure Layer - Common Components

- [x] T032 Create GlobalExceptionHandler in src/main/java/com/insurance/policyholder/infrastructure/exception/GlobalExceptionHandler.java
- [x] T033 [P] Create JPA configuration in src/main/java/com/insurance/policyholder/infrastructure/config/JpaConfig.java
- [x] T034 [P] Create ApiResponse wrapper in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/ApiResponse.java
- [x] T035 [P] Create ErrorResponse in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/ErrorResponse.java

### Infrastructure Layer - Persistence Entities

- [x] T036 [P] Create PolicyHolderJpaEntity in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/entity/PolicyHolderJpaEntity.java
- [x] T037 [P] Create PolicyJpaEntity in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/entity/PolicyJpaEntity.java
- [x] T038 [P] Create DomainEventJpaEntity in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/entity/DomainEventJpaEntity.java

### Infrastructure Layer - JPA Repositories

- [x] T039 [P] Create PolicyHolderJpaRepository in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/repository/PolicyHolderJpaRepository.java
- [x] T040 [P] Create PolicyJpaRepository in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/repository/PolicyJpaRepository.java
- [x] T041 [P] Create DomainEventJpaRepository in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/repository/DomainEventJpaRepository.java

### Infrastructure Layer - Mappers

- [x] T042 [P] Create PolicyHolderMapper in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/mapper/PolicyHolderMapper.java
- [x] T043 [P] Create PolicyMapper in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/mapper/PolicyMapper.java

### Infrastructure Layer - Repository Adapters

- [x] T044 Create PolicyHolderRepositoryAdapter in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/adapter/PolicyHolderRepositoryAdapter.java
- [x] T045 Create PolicyHolderQueryRepositoryAdapter in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/adapter/PolicyHolderQueryRepositoryAdapter.java
- [x] T046 Create EventStoreAdapter in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/event/EventStoreAdapter.java
- [x] T047 Create DomainEventPublisherAdapter in src/main/java/com/insurance/policyholder/infrastructure/adapter/output/event/DomainEventPublisherAdapter.java

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - 新增保戶資料 (Priority: P1) 🎯 MVP

**Goal**: 內勤人員可建立新保戶基本資料，系統產生唯一保戶編號

**Independent Test**: POST /api/v1/policyholders 成功回傳 201 與保戶編號（PH + 10位數字）

### Tests for User Story 1

- [x] T048 [P] [US1] Create PolicyHolderTest for aggregate root in src/test/java/com/insurance/policyholder/domain/model/aggregate/PolicyHolderTest.java
- [x] T049 [P] [US1] Create CreatePolicyHolderCommandHandlerTest in src/test/java/com/insurance/policyholder/application/commandhandler/CreatePolicyHolderCommandHandlerTest.java
- [x] T050 [P] [US1] Create PolicyHolderControllerCreateTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyHolderControllerCreateTest.java
- [x] T051 [US1] Create Cucumber feature for US1 in src/test/resources/features/create-policyholder.feature

### Domain Layer for User Story 1

- [x] T052 [US1] Create PolicyHolder aggregate root in src/main/java/com/insurance/policyholder/domain/model/aggregate/PolicyHolder.java
- [x] T053 [US1] Create PolicyHolderCreated event in src/main/java/com/insurance/policyholder/domain/event/PolicyHolderCreated.java

### Application Layer for User Story 1

- [x] T054 [P] [US1] Create CreatePolicyHolderCommand in src/main/java/com/insurance/policyholder/application/command/CreatePolicyHolderCommand.java
- [x] T055 [P] [US1] Create PolicyHolderReadModel in src/main/java/com/insurance/policyholder/application/readmodel/PolicyHolderReadModel.java
- [x] T056 [US1] Create CreatePolicyHolderCommandHandler in src/main/java/com/insurance/policyholder/application/commandhandler/CreatePolicyHolderCommandHandler.java

### Infrastructure Layer for User Story 1

- [x] T057 [P] [US1] Create CreatePolicyHolderRequest in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/request/CreatePolicyHolderRequest.java
- [x] T058 [P] [US1] Create AddressRequest in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/request/AddressRequest.java
- [x] T059 [P] [US1] Create PolicyHolderResponse in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/PolicyHolderResponse.java
- [x] T060 [P] [US1] Create AddressResponse in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/AddressResponse.java
- [x] T061 [P] [US1] Create request/response mapper in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/mapper/PolicyHolderRestMapper.java
- [x] T062 [US1] Implement createPolicyHolder endpoint in PolicyHolderController in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyHolderController.java

**Checkpoint**: User Story 1 (新增保戶) fully functional and testable independently

---

## Phase 4: User Story 2 - 查詢保戶資料 (Priority: P1)

**Goal**: 客服/業務可透過保戶編號、身分證字號查詢保戶，支援姓名模糊搜尋與分頁

**Independent Test**: GET /api/v1/policyholders/{id} 成功回傳保戶詳細資料

### Tests for User Story 2

- [x] T063 [P] [US2] Create GetPolicyHolderQueryHandlerTest in src/test/java/com/insurance/policyholder/application/queryhandler/GetPolicyHolderQueryHandlerTest.java
- [x] T064 [P] [US2] Create SearchPolicyHoldersQueryHandlerTest in src/test/java/com/insurance/policyholder/application/queryhandler/SearchPolicyHoldersQueryHandlerTest.java
- [x] T065 [P] [US2] Create PolicyHolderControllerQueryTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyHolderControllerQueryTest.java
- [x] T066 [US2] Create Cucumber feature for US2 in src/test/resources/features/query-policyholder.feature

### Application Layer for User Story 2

- [x] T067 [P] [US2] Create GetPolicyHolderQuery in src/main/java/com/insurance/policyholder/application/query/GetPolicyHolderQuery.java
- [x] T068 [P] [US2] Create GetPolicyHolderByNationalIdQuery in src/main/java/com/insurance/policyholder/application/query/GetPolicyHolderByNationalIdQuery.java
- [x] T069 [P] [US2] Create SearchPolicyHoldersQuery in src/main/java/com/insurance/policyholder/application/query/SearchPolicyHoldersQuery.java
- [x] T070 [P] [US2] Create PolicyHolderListItemReadModel in src/main/java/com/insurance/policyholder/application/readmodel/PolicyHolderListItemReadModel.java
- [x] T071 [US2] Create GetPolicyHolderQueryHandler in src/main/java/com/insurance/policyholder/application/queryhandler/GetPolicyHolderQueryHandler.java
- [x] T072 [US2] Create SearchPolicyHoldersQueryHandler in src/main/java/com/insurance/policyholder/application/queryhandler/SearchPolicyHoldersQueryHandler.java

### Infrastructure Layer for User Story 2

- [x] T073 [P] [US2] Create PolicyHolderListItemResponse in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/PolicyHolderListItemResponse.java
- [x] T074 [P] [US2] Create PageResponse wrapper in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/PageResponse.java
- [x] T075 [US2] Implement getPolicyHolder endpoint in PolicyHolderController
- [x] T076 [US2] Implement searchPolicyHolders endpoint in PolicyHolderController

**Checkpoint**: User Story 2 (查詢保戶) fully functional and testable independently

---

## Phase 5: User Story 3 - 修改保戶資料 (Priority: P2)

**Goal**: 內勤人員可更新保戶聯絡資訊，但身分證字號不可修改

**Independent Test**: PUT /api/v1/policyholders/{id} 成功更新資料並回傳新版本號

### Tests for User Story 3

- [x] T077 [P] [US3] Create UpdatePolicyHolderCommandHandlerTest in src/test/java/com/insurance/policyholder/application/commandhandler/UpdatePolicyHolderCommandHandlerTest.java
- [x] T078 [P] [US3] Create PolicyHolderControllerUpdateTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyHolderControllerUpdateTest.java
- [x] T079 [US3] Create Cucumber feature for US3 in src/test/resources/features/update-policyholder.feature

### Domain Layer for User Story 3

- [x] T080 [US3] Add update methods to PolicyHolder aggregate (updateContactInfo, updateAddress)
- [x] T081 [US3] Create PolicyHolderUpdated event in src/main/java/com/insurance/policyholder/domain/event/PolicyHolderUpdated.java

### Application Layer for User Story 3

- [x] T082 [P] [US3] Create UpdatePolicyHolderCommand in src/main/java/com/insurance/policyholder/application/command/UpdatePolicyHolderCommand.java
- [x] T083 [US3] Create UpdatePolicyHolderCommandHandler in src/main/java/com/insurance/policyholder/application/commandhandler/UpdatePolicyHolderCommandHandler.java

### Infrastructure Layer for User Story 3

- [x] T084 [P] [US3] Create UpdatePolicyHolderRequest in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/request/UpdatePolicyHolderRequest.java
- [x] T085 [US3] Implement updatePolicyHolder endpoint in PolicyHolderController

**Checkpoint**: User Story 3 (修改保戶) fully functional and testable independently

---

## Phase 6: User Story 4 - 刪除保戶資料 (Priority: P3)

**Goal**: 內勤人員可軟刪除保戶（狀態改為 INACTIVE）

**Independent Test**: DELETE /api/v1/policyholders/{id} 成功回傳 204，保戶狀態變為 INACTIVE

### Tests for User Story 4

- [x] T086 [P] [US4] Create DeletePolicyHolderCommandHandlerTest in src/test/java/com/insurance/policyholder/application/commandhandler/DeletePolicyHolderCommandHandlerTest.java
- [x] T087 [P] [US4] Create PolicyHolderControllerDeleteTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyHolderControllerDeleteTest.java
- [x] T088 [US4] Create Cucumber feature for US4 in src/test/resources/features/delete-policyholder.feature

### Domain Layer for User Story 4

- [x] T089 [US4] Add deactivate method to PolicyHolder aggregate
- [x] T090 [US4] Create PolicyHolderDeleted event in src/main/java/com/insurance/policyholder/domain/event/PolicyHolderDeleted.java

### Application Layer for User Story 4

- [x] T091 [P] [US4] Create DeletePolicyHolderCommand in src/main/java/com/insurance/policyholder/application/command/DeletePolicyHolderCommand.java
- [x] T092 [US4] Create DeletePolicyHolderCommandHandler in src/main/java/com/insurance/policyholder/application/commandhandler/DeletePolicyHolderCommandHandler.java

### Infrastructure Layer for User Story 4

- [x] T093 [US4] Implement deletePolicyHolder endpoint in PolicyHolderController

**Checkpoint**: User Story 4 (刪除保戶) fully functional and testable independently

---

## Phase 7: User Story 5 - 新增保單 (Priority: P2)

**Goal**: 內勤人員可為 ACTIVE 保戶新增保單

**Independent Test**: POST /api/v1/policyholders/{id}/policies 成功建立保單並關聯至保戶

### Tests for User Story 5

- [ ] T094 [P] [US5] Create PolicyTest for entity in src/test/java/com/insurance/policyholder/domain/model/entity/PolicyTest.java
- [ ] T095 [P] [US5] Create AddPolicyCommandHandlerTest in src/test/java/com/insurance/policyholder/application/commandhandler/AddPolicyCommandHandlerTest.java
- [ ] T096 [P] [US5] Create PolicyControllerAddTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyControllerAddTest.java
- [ ] T097 [US5] Create Cucumber feature for US5 in src/test/resources/features/add-policy.feature

### Domain Layer for User Story 5

- [ ] T098 [US5] Create Policy entity in src/main/java/com/insurance/policyholder/domain/model/entity/Policy.java
- [ ] T099 [US5] Add addPolicy method to PolicyHolder aggregate
- [ ] T100 [US5] Create PolicyHolderNotActiveException in src/main/java/com/insurance/policyholder/domain/exception/PolicyHolderNotActiveException.java
- [ ] T101 [US5] Create PolicyAdded event in src/main/java/com/insurance/policyholder/domain/event/PolicyAdded.java

### Application Layer for User Story 5

- [ ] T102 [P] [US5] Create AddPolicyCommand in src/main/java/com/insurance/policyholder/application/command/AddPolicyCommand.java
- [ ] T103 [P] [US5] Create PolicyReadModel in src/main/java/com/insurance/policyholder/application/readmodel/PolicyReadModel.java
- [ ] T104 [US5] Create AddPolicyCommandHandler in src/main/java/com/insurance/policyholder/application/commandhandler/AddPolicyCommandHandler.java

### Infrastructure Layer for User Story 5

- [ ] T105 [P] [US5] Create AddPolicyRequest in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/request/AddPolicyRequest.java
- [ ] T106 [P] [US5] Create PolicyResponse in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/response/PolicyResponse.java
- [ ] T107 [US5] Implement addPolicy endpoint in PolicyHolderController

**Checkpoint**: User Story 5 (新增保單) fully functional and testable independently

---

## Phase 8: User Story 6 - 查詢保戶保單 (Priority: P2)

**Goal**: 客服/業務可查詢保戶的所有保單，支援類型與狀態篩選

**Independent Test**: GET /api/v1/policyholders/{id}/policies 成功回傳保單列表

### Tests for User Story 6

- [ ] T108 [P] [US6] Create GetPolicyHolderPoliciesQueryHandlerTest in src/test/java/com/insurance/policyholder/application/queryhandler/GetPolicyHolderPoliciesQueryHandlerTest.java
- [ ] T109 [P] [US6] Create PolicyControllerQueryTest in src/test/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyControllerQueryTest.java
- [ ] T110 [US6] Create Cucumber feature for US6 in src/test/resources/features/query-policies.feature

### Application Layer for User Story 6

- [ ] T111 [P] [US6] Create GetPolicyHolderPoliciesQuery in src/main/java/com/insurance/policyholder/application/query/GetPolicyHolderPoliciesQuery.java
- [ ] T112 [P] [US6] Create GetPolicyQuery in src/main/java/com/insurance/policyholder/application/query/GetPolicyQuery.java
- [ ] T113 [US6] Create GetPolicyHolderPoliciesQueryHandler in src/main/java/com/insurance/policyholder/application/queryhandler/GetPolicyHolderPoliciesQueryHandler.java
- [ ] T114 [US6] Create GetPolicyQueryHandler in src/main/java/com/insurance/policyholder/application/queryhandler/GetPolicyQueryHandler.java

### Infrastructure Layer for User Story 6

- [ ] T115 [US6] Implement getPolicyHolderPolicies endpoint in PolicyHolderController
- [ ] T116 [US6] Implement getPolicy endpoint (single policy) in PolicyController in src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/PolicyController.java

**Checkpoint**: User Story 6 (查詢保單) fully functional and testable independently

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T117 [P] Create PolicyHolderDomainService for cross-aggregate validations in src/main/java/com/insurance/policyholder/domain/service/PolicyHolderDomainService.java
- [ ] T118 [P] Add database indexes per domain-model.md via schema.sql in src/main/resources/schema.sql
- [ ] T119 [P] Create integration test for full API workflow in src/test/java/com/insurance/policyholder/integration/PolicyHolderIntegrationTest.java
- [ ] T120 Run quickstart.md validation - verify all endpoints work correctly
- [ ] T121 Run test coverage report and ensure 80% coverage per SC-008
- [ ] T122 Run ArchUnit tests and verify hexagonal architecture compliance

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-8)**: All depend on Foundational phase completion
  - User stories can then proceed in priority order (P1 → P2 → P3)
  - Within same priority: Can run in parallel if team has capacity
- **Polish (Phase 9)**: Depends on all user stories being complete

### User Story Dependencies

| User Story | Priority | Dependencies | Can Start After |
|------------|----------|--------------|-----------------|
| US1 - 新增保戶 | P1 | None | Phase 2 |
| US2 - 查詢保戶 | P1 | US1 (needs data) | US1 |
| US3 - 修改保戶 | P2 | US1 (needs policyholder) | US1 |
| US4 - 刪除保戶 | P3 | US1 (needs policyholder) | US1 |
| US5 - 新增保單 | P2 | US1 (needs active policyholder) | US1 |
| US6 - 查詢保單 | P2 | US5 (needs policies) | US5 |

### Within Each User Story

1. Tests (TDD) MUST be written and FAIL before implementation
2. Domain Layer before Application Layer
3. Application Layer before Infrastructure Layer
4. Core implementation before integration
5. Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Value Object tests (T013-T018) can all run in parallel
- Value Object implementations (T019-T025) can all run in parallel
- Port interfaces (T026-T031) can all run in parallel
- JPA entities and repositories (T036-T043) can all run in parallel

---

## Parallel Example: Foundational Phase

```bash
# Launch all value object tests together:
T013: PolicyHolderIdTest
T014: NationalIdTest
T015: AddressTest
T016: MoneyTest
T017: PersonalInfoTest
T018: ContactInfoTest

# Launch all value object implementations together:
T019: PolicyHolderId
T020: NationalId
T021: Address
T022: Money
T023: PersonalInfo
T024: ContactInfo
```

## Parallel Example: User Story 1

```bash
# Launch all tests for US1 together:
T048: PolicyHolderTest
T049: CreatePolicyHolderCommandHandlerTest
T050: PolicyHolderControllerCreateTest

# Launch all request/response DTOs together:
T057: CreatePolicyHolderRequest
T058: AddressRequest
T059: PolicyHolderResponse
T060: AddressResponse
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (新增保戶)
4. **STOP and VALIDATE**: Test User Story 1 independently via quickstart.md curl examples
5. Deploy/demo if ready - 內勤人員可建立新保戶

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 (新增保戶) → Test independently → **MVP Ready!**
3. Add User Story 2 (查詢保戶) → Test independently → 客服可查詢保戶
4. Add User Story 3 (修改保戶) → Test independently → 可維護保戶資料
5. Add User Story 5 (新增保單) → Test independently → 可建立保單
6. Add User Story 6 (查詢保單) → Test independently → 完整保單查詢
7. Add User Story 4 (刪除保戶) → Test independently → 完整 CRUD

### Recommended Order (Single Developer)

Phase 1 → Phase 2 → Phase 3 (US1) → Phase 4 (US2) → Phase 5 (US3) → Phase 7 (US5) → Phase 8 (US6) → Phase 6 (US4) → Phase 9

---

## Summary

| Phase | Description | Task Count | Parallel Tasks |
|-------|-------------|------------|----------------|
| Phase 1 | Setup | 5 | 4 |
| Phase 2 | Foundational | 42 | 35 |
| Phase 3 | US1 - 新增保戶 | 15 | 9 |
| Phase 4 | US2 - 查詢保戶 | 14 | 8 |
| Phase 5 | US3 - 修改保戶 | 9 | 5 |
| Phase 6 | US4 - 刪除保戶 | 8 | 4 |
| Phase 7 | US5 - 新增保單 | 14 | 9 |
| Phase 8 | US6 - 查詢保單 | 9 | 5 |
| Phase 9 | Polish | 6 | 3 |
| **Total** | | **122** | **82** |

---

## Notes

- [P] tasks = different files, no dependencies on incomplete tasks
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- TDD: Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All domain layer classes must be pure Java (no Spring annotations)
- Application layer only depends on Port interfaces
- Infrastructure layer implements Port interfaces
