package com.insurance.policyholder.infrastructure.adapter.output.persistence.adapter;

import com.insurance.policyholder.application.port.output.PolicyHolderQueryRepository;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.repository.PolicyHolderJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 保戶查詢儲存庫適配器
 * 實作 Application Layer 的 PolicyHolderQueryRepository Port
 * 用於 CQRS 的 Query 端
 */
@Repository
@Transactional(readOnly = true)
public class PolicyHolderQueryRepositoryAdapter implements PolicyHolderQueryRepository<PolicyHolderReadModel> {

    private final PolicyHolderJpaRepository jpaRepository;

    public PolicyHolderQueryRepositoryAdapter(PolicyHolderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<PolicyHolderReadModel> findById(PolicyHolderId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::toReadModel);
    }

    @Override
    public Optional<PolicyHolderReadModel> findByNationalId(NationalId nationalId) {
        return jpaRepository.findByNationalId(nationalId.getValue())
                .map(this::toReadModel);
    }

    @Override
    public List<PolicyHolderReadModel> searchByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.findByNameContaining(name, pageable)
                .stream()
                .map(this::toReadModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<PolicyHolderReadModel> findByStatus(PolicyHolderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        PolicyHolderJpaEntity.Status jpaStatus = mapStatusToJpa(status);
        return jpaRepository.findByStatus(jpaStatus, pageable)
                .stream()
                .map(this::toReadModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countByName(String name) {
        return jpaRepository.countByNameContaining(name);
    }

    @Override
    public long countByStatus(PolicyHolderStatus status) {
        PolicyHolderJpaEntity.Status jpaStatus = mapStatusToJpa(status);
        return jpaRepository.countByStatus(jpaStatus);
    }

    private PolicyHolderReadModel toReadModel(PolicyHolderJpaEntity entity) {
        return new PolicyHolderReadModel(
                entity.getId(),
                entity.getNationalId(),
                entity.getName(),
                entity.getGender().name(),
                entity.getBirthDate(),
                entity.getMobilePhone(),
                entity.getEmail(),
                entity.getZipCode(),
                entity.getCity(),
                entity.getDistrict(),
                entity.getStreet(),
                entity.getStatus().name(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private PolicyHolderJpaEntity.Status mapStatusToJpa(PolicyHolderStatus status) {
        return switch (status) {
            case ACTIVE -> PolicyHolderJpaEntity.Status.ACTIVE;
            case INACTIVE -> PolicyHolderJpaEntity.Status.INACTIVE;
            case SUSPENDED -> PolicyHolderJpaEntity.Status.SUSPENDED;
        };
    }
}
