package com.insurance.policyholder.domain.exception;

/**
 * 保戶找不到例外
 */
public class PolicyHolderNotFoundException extends DomainException {

    private static final String ERROR_CODE = "POLICY_HOLDER_NOT_FOUND";

    public PolicyHolderNotFoundException(String identifier) {
        super(ERROR_CODE, "PolicyHolder not found: " + identifier);
    }

    public PolicyHolderNotFoundException(String identifier, String message) {
        super(ERROR_CODE, message);
    }
}
