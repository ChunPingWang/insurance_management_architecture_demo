package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.AddPolicyCommand;
import com.insurance.policyholder.application.port.input.CommandHandler;
import com.insurance.policyholder.application.port.output.DomainEventPublisher;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.event.PolicyAdded;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.Money;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 新增保單命令處理器
 * 實作 AddPolicy 用例
 */
@Service
@Transactional
public class AddPolicyCommandHandler implements CommandHandler<AddPolicyCommand, PolicyReadModel> {

    private final PolicyHolderRepository policyHolderRepository;
    private final DomainEventPublisher domainEventPublisher;

    public AddPolicyCommandHandler(
            PolicyHolderRepository policyHolderRepository,
            DomainEventPublisher domainEventPublisher) {
        this.policyHolderRepository = policyHolderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public PolicyReadModel handle(AddPolicyCommand command) {
        // 1. 查詢保戶
        PolicyHolderId policyHolderId = PolicyHolderId.of(command.getPolicyHolderId());
        PolicyHolder policyHolder = policyHolderRepository.findById(policyHolderId)
                .orElseThrow(() -> new PolicyHolderNotFoundException(command.getPolicyHolderId()));

        // 2. 建立保單實體
        Policy policy = Policy.create(
                PolicyType.valueOf(command.getPolicyType()),
                Money.of(command.getPremium()),
                Money.of(command.getSumInsured()),
                command.getStartDate(),
                command.getEndDate()
        );

        // 3. 新增保單到保戶（如果保戶非 ACTIVE 狀態會拋出 IllegalStateException）
        policyHolder.addPolicy(policy);

        // 4. 儲存保戶
        PolicyHolder savedPolicyHolder = policyHolderRepository.save(policyHolder);

        // 5. 發布領域事件
        PolicyAdded event = new PolicyAdded(savedPolicyHolder.getId().getValue(), policy);
        domainEventPublisher.publish(event);

        // 6. 轉換為 ReadModel 並回傳
        return toPolicyReadModel(savedPolicyHolder.getId().getValue(), policy);
    }

    private PolicyReadModel toPolicyReadModel(String policyHolderId, Policy policy) {
        return new PolicyReadModel(
                policy.getId().getValue(),
                policyHolderId,
                policy.getPolicyType().name(),
                policy.getPremium().getAmount(),
                policy.getSumInsured().getAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getStatus().name()
        );
    }
}
