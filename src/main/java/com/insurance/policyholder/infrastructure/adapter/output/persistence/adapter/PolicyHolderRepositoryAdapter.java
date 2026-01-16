package com.insurance.policyholder.infrastructure.adapter.output.persistence.adapter;

import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper.PolicyHolderMapper;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.repository.PolicyHolderJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 保戶儲存庫適配器
 * 實作 Application Layer 的 PolicyHolderRepository Port
 */
@Repository
@Transactional
public class PolicyHolderRepositoryAdapter implements PolicyHolderRepository {

    private final PolicyHolderJpaRepository jpaRepository;
    private final PolicyHolderMapper mapper;

    public PolicyHolderRepositoryAdapter(PolicyHolderJpaRepository jpaRepository, PolicyHolderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public PolicyHolder save(PolicyHolder policyHolder) {
        PolicyHolderJpaEntity entity = mapper.toEntity(policyHolder);
        PolicyHolderJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyHolder> findById(PolicyHolderId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyHolder> findByNationalId(NationalId nationalId) {
        return jpaRepository.findByNationalId(nationalId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNationalId(NationalId nationalId) {
        return jpaRepository.existsByNationalId(nationalId.getValue());
    }

    @Override
    public void deleteById(PolicyHolderId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
