# language: zh-TW
@US5 @AddPolicy
功能: US5 - 新增保單
  作為一個保險業務員
  我想要為現有保戶新增保單
  以便管理保戶的保險商品

  背景:
    假設 系統中已有一位 ACTIVE 狀態的保戶 "PH0000000001"

  @Happy
  場景: 成功新增壽險保單
    當 我為保戶 "PH0000000001" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | LIFE       | 10000   | 1000000    | 2024-01-01 | 2024-12-31 |
    那麼 系統應回傳狀態碼 201
    而且 回應應包含新建立的保單資訊
    而且 保單編號應為 "PO" 開頭
    而且 保單狀態應為 "ACTIVE"

  @Happy
  場景: 成功新增健康險保單
    當 我為保戶 "PH0000000001" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | HEALTH     | 5000    | 500000     | 2024-01-01 | 2024-12-31 |
    那麼 系統應回傳狀態碼 201
    而且 回應應包含新建立的保單資訊
    而且 保單類型應為 "HEALTH"

  @Happy
  場景: 成功新增旅遊險保單
    當 我為保戶 "PH0000000001" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | TRAVEL     | 1000    | 100000     | 2024-06-01 | 2024-06-15 |
    那麼 系統應回傳狀態碼 201
    而且 保單類型應為 "TRAVEL"

  @Error
  場景: 保戶不存在時無法新增保單
    當 我為保戶 "PH9999999999" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | LIFE       | 10000   | 1000000    | 2024-01-01 | 2024-12-31 |
    那麼 系統應回傳狀態碼 404
    而且 錯誤代碼應為 "POLICY_HOLDER_NOT_FOUND"

  @Error
  場景: INACTIVE 保戶無法新增保單
    假設 系統中已有一位 INACTIVE 狀態的保戶 "PH0000000002"
    當 我為保戶 "PH0000000002" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | LIFE       | 10000   | 1000000    | 2024-01-01 | 2024-12-31 |
    那麼 系統應回傳狀態碼 400
    而且 錯誤代碼應為 "POLICY_HOLDER_NOT_ACTIVE"

  @Validation
  場景: 缺少保單類型時應回傳錯誤
    當 我為保戶 "PH0000000001" 新增缺少必填欄位的保單
    那麼 系統應回傳狀態碼 400

  @Validation
  場景: 結束日期早於開始日期時應回傳錯誤
    當 我為保戶 "PH0000000001" 新增以下保單:
      | policyType | premium | sumInsured | startDate  | endDate    |
      | LIFE       | 10000   | 1000000    | 2024-12-31 | 2024-01-01 |
    那麼 系統應回傳狀態碼 400
