package com.insurance.policyholder.domain.exception;

/**
 * 保單找不到例外
 */
public class PolicyNotFoundException extends DomainException {

    private static final String ERROR_CODE = "POLICY_NOT_FOUND";

    public PolicyNotFoundException(String policyId) {
        super(ERROR_CODE, "Policy not found: " + policyId);
    }

    public PolicyNotFoundException(String policyId, String message) {
        super(ERROR_CODE, message);
    }
}
