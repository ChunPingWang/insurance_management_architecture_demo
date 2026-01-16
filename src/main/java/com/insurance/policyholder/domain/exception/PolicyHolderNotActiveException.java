package com.insurance.policyholder.domain.exception;

/**
 * 保戶非活動狀態例外
 * 當嘗試對非 ACTIVE 狀態的保戶執行需要活動狀態的操作時拋出
 */
public class PolicyHolderNotActiveException extends DomainException {

    private static final String ERROR_CODE = "POLICY_HOLDER_NOT_ACTIVE";

    public PolicyHolderNotActiveException(String policyHolderId) {
        super(ERROR_CODE, "PolicyHolder is not active: " + policyHolderId);
    }

    public PolicyHolderNotActiveException(String policyHolderId, String message) {
        super(ERROR_CODE, message);
    }
}
