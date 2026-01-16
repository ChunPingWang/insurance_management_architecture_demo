package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.DeletePolicyHolderCommand;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 刪除保戶命令處理器
 * 處理保戶的軟刪除（狀態改為 INACTIVE）
 */
@Service
@Transactional
public class DeletePolicyHolderCommandHandler {

    private final PolicyHolderRepository repository;

    public DeletePolicyHolderCommandHandler(PolicyHolderRepository repository) {
        this.repository = repository;
    }

    public void handle(DeletePolicyHolderCommand command) {
        // 1. 查詢現有保戶
        PolicyHolderId id = PolicyHolderId.of(command.getPolicyHolderId());
        PolicyHolder policyHolder = repository.findById(id)
                .orElseThrow(() -> new PolicyHolderNotFoundException(command.getPolicyHolderId()));

        // 2. 執行軟刪除（deactivate 會檢查是否已經 INACTIVE）
        policyHolder.deactivate();

        // 3. 儲存更新後的保戶
        repository.save(policyHolder);
    }
}
