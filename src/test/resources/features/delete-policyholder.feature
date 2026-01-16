# language: zh-TW
@US4
Feature: 刪除保戶資料
  作為內勤人員
  我想要刪除保戶資料
  以便維護保戶資料庫的正確性

  Background:
    Given 系統中已存在保戶
      | id           | nationalId  | name   | status |
      | PH0000000001 | A123456789  | 王小明  | ACTIVE |

  @happy-path
  Scenario: 成功軟刪除保戶
    When 我刪除保戶 "PH0000000001"
    Then 應回傳狀態碼 204
    And 保戶 "PH0000000001" 的狀態應為 "INACTIVE"

  @happy-path
  Scenario: 刪除後保戶資料應保留
    When 我刪除保戶 "PH0000000001"
    Then 應回傳狀態碼 204
    When 我查詢保戶 "PH0000000001"
    Then 應回傳狀態碼 200
    And 保戶資料應包含
      | nationalId  | A123456789 |
      | name        | 王小明      |
      | status      | INACTIVE   |

  @error-handling
  Scenario: 保戶不存在
    When 我刪除不存在的保戶 "PH9999999999"
    Then 應回傳狀態碼 404
    And 錯誤碼應為 "POLICY_HOLDER_NOT_FOUND"

  @error-handling
  Scenario: 重複刪除已刪除的保戶
    Given 保戶 "PH0000000001" 已被刪除
    When 我再次刪除保戶 "PH0000000001"
    Then 應回傳狀態碼 400
    And 錯誤訊息應包含 "already inactive"

  @business-rule
  Scenario: 軟刪除不會實際移除資料
    When 我刪除保戶 "PH0000000001"
    Then 資料庫中應仍存在保戶 "PH0000000001"
    And 保戶狀態應為 "INACTIVE"
