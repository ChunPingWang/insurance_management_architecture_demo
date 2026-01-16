package com.insurance.policyholder.application.query;

/**
 * 根據保戶編號查詢保戶
 * CQRS Query
 */
public class GetPolicyHolderQuery {

    private final String policyHolderId;

    public GetPolicyHolderQuery(String policyHolderId) {
        if (policyHolderId == null || policyHolderId.isBlank()) {
            throw new IllegalArgumentException("PolicyHolder ID cannot be null or empty");
        }
        this.policyHolderId = policyHolderId;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
    }
}
