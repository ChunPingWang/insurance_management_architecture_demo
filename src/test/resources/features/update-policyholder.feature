# language: zh-TW
@US3
Feature: 修改保戶資料
  作為內勤人員
  我想要修改保戶的聯絡資訊
  以便維護最新的保戶資料

  Background:
    Given 系統中已存在保戶
      | id           | nationalId  | name   | mobilePhone | email            |
      | PH0000000001 | A123456789  | 王小明  | 0912345678  | old@example.com  |

  @happy-path
  Scenario: 成功更新保戶聯絡資訊
    When 我更新保戶 "PH0000000001" 的資料
      | mobilePhone  | 0987654321        |
      | email        | new@example.com   |
      | zipCode      | 200               |
      | city         | 新北市             |
      | district     | 板橋區             |
      | street       | 新地址200號        |
    Then 應回傳狀態碼 200
    And 回傳的保戶資料應包含
      | mobilePhone  | 0987654321        |
      | email        | new@example.com   |
    And 身分證字號應保持為 "A123456789"
    And 版本號應大於 0

  @happy-path
  Scenario: 只更新手機號碼
    When 我更新保戶 "PH0000000001" 的手機為 "0911222333"
    Then 應回傳狀態碼 200
    And 手機號碼應為 "0911222333"
    And email 應保持為 "old@example.com"

  @happy-path
  Scenario: 只更新地址
    When 我更新保戶 "PH0000000001" 的地址
      | zipCode   | 300      |
      | city      | 新竹市    |
      | district  | 東區      |
      | street    | 科學路1號 |
    Then 應回傳狀態碼 200
    And 地址應為 "新竹市東區科學路1號"

  @error-handling
  Scenario: 保戶不存在
    When 我更新不存在的保戶 "PH9999999999"
    Then 應回傳狀態碼 404
    And 錯誤碼應為 "POLICY_HOLDER_NOT_FOUND"

  @validation
  Scenario: 手機號碼格式錯誤
    When 我更新保戶 "PH0000000001" 的手機為 "invalid-phone"
    Then 應回傳狀態碼 400
    And 應包含手機號碼格式錯誤訊息

  @validation
  Scenario: Email 格式錯誤
    When 我更新保戶 "PH0000000001" 的 email 為 "invalid-email"
    Then 應回傳狀態碼 400
    And 應包含 email 格式錯誤訊息

  @business-rule
  Scenario: 身分證字號不可修改
    Given 更新請求中包含身分證字號
    When 我嘗試更新保戶 "PH0000000001" 的身分證
    Then 身分證字號應保持不變
    And 系統應忽略身分證字號的修改請求
