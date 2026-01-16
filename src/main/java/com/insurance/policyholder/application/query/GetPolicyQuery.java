package com.insurance.policyholder.application.query;

/**
 * 查詢單一保單查詢
 * CQRS Query - 用於查詢單一保單詳細資訊
 */
public class GetPolicyQuery {

    private final String policyHolderId;
    private final String policyId;

    public GetPolicyQuery(String policyHolderId, String policyId) {
        this.policyHolderId = policyHolderId;
        this.policyId = policyId;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
    }

    public String getPolicyId() {
        return policyId;
    }
}
