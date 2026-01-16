package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.input.QueryHandler;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.query.GetPolicyHolderPoliciesQuery;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查詢保戶保單查詢處理器
 * 實作查詢保戶的所有保單
 */
@Service
@Transactional(readOnly = true)
public class GetPolicyHolderPoliciesQueryHandler implements QueryHandler<GetPolicyHolderPoliciesQuery, List<PolicyReadModel>> {

    private final PolicyHolderRepository policyHolderRepository;

    public GetPolicyHolderPoliciesQueryHandler(PolicyHolderRepository policyHolderRepository) {
        this.policyHolderRepository = policyHolderRepository;
    }

    @Override
    public List<PolicyReadModel> handle(GetPolicyHolderPoliciesQuery query) {
        // 1. 查詢保戶
        PolicyHolderId policyHolderId = PolicyHolderId.of(query.getPolicyHolderId());
        PolicyHolder policyHolder = policyHolderRepository.findById(policyHolderId)
                .orElseThrow(() -> new PolicyHolderNotFoundException(query.getPolicyHolderId()));

        // 2. 取得保單列表並套用篩選條件
        return policyHolder.getPolicies().stream()
                .filter(policy -> matchesTypeFilter(policy, query))
                .filter(policy -> matchesStatusFilter(policy, query))
                .map(policy -> toPolicyReadModel(policyHolder.getId().getValue(), policy))
                .collect(Collectors.toList());
    }

    private boolean matchesTypeFilter(Policy policy, GetPolicyHolderPoliciesQuery query) {
        if (!query.hasTypeFilter()) {
            return true;
        }
        return policy.getPolicyType().name().equals(query.getPolicyType());
    }

    private boolean matchesStatusFilter(Policy policy, GetPolicyHolderPoliciesQuery query) {
        if (!query.hasStatusFilter()) {
            return true;
        }
        return policy.getStatus().name().equals(query.getStatus());
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
