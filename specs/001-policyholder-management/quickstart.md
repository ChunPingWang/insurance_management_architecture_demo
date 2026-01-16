# Quickstart: 保戶基本資料管理系統

**Feature**: 001-policyholder-management
**Date**: 2026-01-16

## 前置需求

- Java 17+
- Gradle 8.x 或 Maven 3.9+
- IDE (IntelliJ IDEA recommended)

## 專案初始化

### 1. 建立 Spring Boot 專案

使用 Spring Initializr 或手動建立，包含以下依賴：

```gradle
dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Database
    runtimeOnly 'com.h2database:h2'

    // OpenAPI
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'com.tngtech.archunit:archunit-junit5:1.2.1'
    testImplementation 'io.cucumber:cucumber-java:7.15.0'
    testImplementation 'io.cucumber:cucumber-spring:7.15.0'
}
```

### 2. 建立套件結構

```bash
mkdir -p src/main/java/com/insurance/policyholder/{domain,application,infrastructure}
mkdir -p src/main/java/com/insurance/policyholder/domain/{model,event,service,exception}
mkdir -p src/main/java/com/insurance/policyholder/domain/model/{aggregate,entity,valueobject,enums}
mkdir -p src/main/java/com/insurance/policyholder/application/{command,commandhandler,query,queryhandler,readmodel,port}
mkdir -p src/main/java/com/insurance/policyholder/application/port/{input,output}
mkdir -p src/main/java/com/insurance/policyholder/infrastructure/{adapter,config,exception}
mkdir -p src/main/java/com/insurance/policyholder/infrastructure/adapter/{input,output}
mkdir -p src/main/java/com/insurance/policyholder/infrastructure/adapter/input/rest/{request,response,mapper}
mkdir -p src/main/java/com/insurance/policyholder/infrastructure/adapter/output/{persistence,event}
mkdir -p src/main/java/com/insurance/policyholder/infrastructure/adapter/output/persistence/{entity,repository,adapter,mapper}
```

### 3. 設定 application.yml

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

## 啟動與驗證

### 1. 啟動應用程式

```bash
./gradlew bootRun
```

### 2. 驗證端點

| 驗證項目 | URL |
|----------|-----|
| H2 Console | http://localhost:8080/h2-console |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |
| Health | http://localhost:8080/actuator/health |

### 3. 測試 API

**新增保戶：**
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
      "street": "忠孝東路一段1號"
    }
  }'
```

**查詢保戶：**
```bash
curl http://localhost:8080/api/v1/policyholders/PH0000000001
```

**查詢保戶列表：**
```bash
curl "http://localhost:8080/api/v1/policyholders?page=0&size=20"
```

## 測試執行

### 執行單元測試
```bash
./gradlew test
```

### 執行架構測試
```bash
./gradlew test --tests "*ArchitectureTest*"
```

### 執行 BDD 測試
```bash
./gradlew cucumberTest
```

### 產生測試報告
```bash
./gradlew jacocoTestReport
# 報告位置: build/reports/jacoco/test/html/index.html
```

## 開發流程

### TDD 循環

1. **Red**: 撰寫失敗的測試
   ```java
   @Test
   void shouldCreatePolicyHolder_whenValidData() {
       // Given
       CreatePolicyHolderCommand command = ...;

       // When
       var result = handler.handle(command);

       // Then
       assertThat(result.policyHolderId()).startsWith("PH");
   }
   ```

2. **Green**: 實作最少程式碼使測試通過

3. **Refactor**: 重構並保持測試通過

### 分層開發順序

1. **Domain Layer** (先寫測試)
   - Value Objects (PolicyHolderId, NationalId, Address...)
   - Entity (Policy)
   - Aggregate Root (PolicyHolder)
   - Domain Events

2. **Application Layer** (先寫測試)
   - Commands/Queries
   - Port Interfaces
   - Command/Query Handlers

3. **Infrastructure Layer**
   - JPA Entities
   - Repository Adapters
   - REST Controller

## 常見問題

### Q: Domain Layer 可以使用 Lombok 嗎？
A: 可以，但建議使用 Java Record 取代 Lombok @Value。

### Q: 如何驗證六角形架構依賴方向？
A: 執行 ArchUnit 測試：
```bash
./gradlew test --tests "*ArchitectureTest*"
```

### Q: H2 資料如何持久化？
A: 開發環境使用 in-memory，生產環境建議更換為 PostgreSQL。

## 下一步

完成 quickstart 驗證後，執行：
```
/speckit.tasks
```
生成詳細的實作任務清單。
