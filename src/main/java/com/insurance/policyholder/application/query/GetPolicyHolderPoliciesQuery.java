package com.insurance.policyholder.application.query;

/**
 * 查詢保戶保單查詢
 * CQRS Query - 用於查詢保戶的所有保單
 */
public class GetPolicyHolderPoliciesQuery {

    private final String policyHolderId;
    private final String policyType;
    private final String status;

    public GetPolicyHolderPoliciesQuery(String policyHolderId) {
        this(policyHolderId, null, null);
    }

    public GetPolicyHolderPoliciesQuery(String policyHolderId, String policyType, String status) {
        this.policyHolderId = policyHolderId;
        this.policyType = policyType;
        this.status = status;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
    }

    public String getPolicyType() {
        return policyType;
    }

    public String getStatus() {
        return status;
    }

    public boolean hasTypeFilter() {
        return policyType != null && !policyType.isBlank();
    }

    public boolean hasStatusFilter() {
        return status != null && !status.isBlank();
    }
}
