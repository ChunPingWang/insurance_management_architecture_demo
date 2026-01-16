package com.insurance.policyholder.application.command;

/**
 * 刪除保戶命令
 * CQRS Command - 軟刪除保戶（狀態改為 INACTIVE）
 */
public class DeletePolicyHolderCommand {

    private final String policyHolderId;

    public DeletePolicyHolderCommand(String policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    public String getPolicyHolderId() {
        return policyHolderId;
    }
}
