package com.insurance.policyholder.domain.model.enums;

/**
 * 保戶狀態列舉
 */
public enum PolicyHolderStatus {
    /** 有效 */
    ACTIVE,
    /** 停用（軟刪除） */
    INACTIVE,
    /** 停權 */
    SUSPENDED
}
