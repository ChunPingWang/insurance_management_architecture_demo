package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.input.QueryHandler;
import com.insurance.policyholder.application.port.output.PolicyHolderQueryRepository;
import com.insurance.policyholder.application.query.GetPolicyHolderByNationalIdQuery;
import com.insurance.policyholder.application.query.GetPolicyHolderQuery;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 查詢單一保戶的處理器
 */
@Service
@Transactional(readOnly = true)
public class GetPolicyHolderQueryHandler implements QueryHandler<GetPolicyHolderQuery, PolicyHolderReadModel> {

    private final PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository;

    public GetPolicyHolderQueryHandler(PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public PolicyHolderReadModel handle(GetPolicyHolderQuery query) {
        PolicyHolderId id = PolicyHolderId.of(query.getPolicyHolderId());
        return queryRepository.findById(id)
                .orElseThrow(() -> new PolicyHolderNotFoundException(query.getPolicyHolderId()));
    }

    /**
     * 根據身分證字號查詢
     */
    public PolicyHolderReadModel handleByNationalId(GetPolicyHolderByNationalIdQuery query) {
        NationalId nationalId = NationalId.of(query.getNationalId());
        return queryRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new PolicyHolderNotFoundException(
                        "National ID not found: " + query.getNationalId()));
    }
}
