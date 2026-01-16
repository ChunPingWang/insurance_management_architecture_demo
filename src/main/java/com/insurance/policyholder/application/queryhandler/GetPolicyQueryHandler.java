package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.input.QueryHandler;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.query.GetPolicyQuery;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.exception.PolicyNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 查詢單一保單查詢處理器
 * 實作查詢單一保單詳細資訊
 */
@Service
@Transactional(readOnly = true)
public class GetPolicyQueryHandler implements QueryHandler<GetPolicyQuery, PolicyReadModel> {

    private final PolicyHolderRepository policyHolderRepository;

    public GetPolicyQueryHandler(PolicyHolderRepository policyHolderRepository) {
        this.policyHolderRepository = policyHolderRepository;
    }

    @Override
    public PolicyReadModel handle(GetPolicyQuery query) {
        // 1. 查詢保戶
        PolicyHolderId policyHolderId = PolicyHolderId.of(query.getPolicyHolderId());
        PolicyHolder policyHolder = policyHolderRepository.findById(policyHolderId)
                .orElseThrow(() -> new PolicyHolderNotFoundException(query.getPolicyHolderId()));

        // 2. 查詢保單
        Policy policy = policyHolder.getPolicies().stream()
                .filter(p -> p.getId().getValue().equals(query.getPolicyId()))
                .findFirst()
                .orElseThrow(() -> new PolicyNotFoundException(query.getPolicyId()));

        // 3. 轉換為 ReadModel
        return toPolicyReadModel(policyHolder.getId().getValue(), policy);
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
