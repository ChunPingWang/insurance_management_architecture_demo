package com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.*;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 保戶領域模型與 JPA 實體的轉換器
 */
@Component
public class PolicyHolderMapper {

    private final PolicyMapper policyMapper;

    public PolicyHolderMapper(PolicyMapper policyMapper) {
        this.policyMapper = policyMapper;
    }

    /**
     * JPA 實體轉換為領域模型
     */
    public PolicyHolder toDomain(PolicyHolderJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PolicyHolderId id = PolicyHolderId.of(entity.getId());
        NationalId nationalId = NationalId.of(entity.getNationalId());

        PersonalInfo personalInfo = PersonalInfo.of(
                entity.getName(),
                mapGenderToDomain(entity.getGender()),
                entity.getBirthDate()
        );

        ContactInfo contactInfo = ContactInfo.of(
                entity.getMobilePhone(),
                entity.getEmail()
        );

        Address address = Address.of(
                entity.getZipCode(),
                entity.getCity(),
                entity.getDistrict(),
                entity.getStreet()
        );

        PolicyHolderStatus status = mapStatusToDomain(entity.getStatus());

        // 使用重建方法（不會產生新事件）
        PolicyHolder policyHolder = PolicyHolder.reconstitute(
                id,
                nationalId,
                personalInfo,
                contactInfo,
                address,
                status,
                entity.getVersion()
        );

        // 轉換保單
        if (entity.getPolicies() != null && !entity.getPolicies().isEmpty()) {
            entity.getPolicies().forEach(policyEntity -> {
                policyHolder.addReconstitutedPolicy(policyMapper.toDomain(policyEntity));
            });
        }

        return policyHolder;
    }

    /**
     * 領域模型轉換為 JPA 實體
     */
    public PolicyHolderJpaEntity toEntity(PolicyHolder domain) {
        if (domain == null) {
            return null;
        }

        PolicyHolderJpaEntity entity = new PolicyHolderJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setNationalId(domain.getNationalId().getValue());
        entity.setName(domain.getPersonalInfo().getName());
        entity.setGender(mapGenderToEntity(domain.getPersonalInfo().getGender()));
        entity.setBirthDate(domain.getPersonalInfo().getBirthDate());
        entity.setMobilePhone(domain.getContactInfo().getMobilePhone());
        entity.setEmail(domain.getContactInfo().getEmail());
        entity.setZipCode(domain.getAddress().getZipCode());
        entity.setCity(domain.getAddress().getCity());
        entity.setDistrict(domain.getAddress().getDistrict());
        entity.setStreet(domain.getAddress().getStreet());
        entity.setStatus(mapStatusToEntity(domain.getStatus()));
        entity.setVersion(domain.getVersion());

        // 轉換保單
        if (domain.getPolicies() != null && !domain.getPolicies().isEmpty()) {
            domain.getPolicies().forEach(policy -> {
                entity.addPolicy(policyMapper.toEntity(policy, entity));
            });
        }

        return entity;
    }

    /**
     * 更新現有 JPA 實體
     */
    public void updateEntity(PolicyHolderJpaEntity entity, PolicyHolder domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setName(domain.getPersonalInfo().getName());
        entity.setGender(mapGenderToEntity(domain.getPersonalInfo().getGender()));
        entity.setBirthDate(domain.getPersonalInfo().getBirthDate());
        entity.setMobilePhone(domain.getContactInfo().getMobilePhone());
        entity.setEmail(domain.getContactInfo().getEmail());
        entity.setZipCode(domain.getAddress().getZipCode());
        entity.setCity(domain.getAddress().getCity());
        entity.setDistrict(domain.getAddress().getDistrict());
        entity.setStreet(domain.getAddress().getStreet());
        entity.setStatus(mapStatusToEntity(domain.getStatus()));
    }

    private Gender mapGenderToDomain(PolicyHolderJpaEntity.Gender gender) {
        return switch (gender) {
            case MALE -> Gender.MALE;
            case FEMALE -> Gender.FEMALE;
        };
    }

    private PolicyHolderJpaEntity.Gender mapGenderToEntity(Gender gender) {
        return switch (gender) {
            case MALE -> PolicyHolderJpaEntity.Gender.MALE;
            case FEMALE -> PolicyHolderJpaEntity.Gender.FEMALE;
        };
    }

    private PolicyHolderStatus mapStatusToDomain(PolicyHolderJpaEntity.Status status) {
        return switch (status) {
            case ACTIVE -> PolicyHolderStatus.ACTIVE;
            case INACTIVE -> PolicyHolderStatus.INACTIVE;
            case SUSPENDED -> PolicyHolderStatus.SUSPENDED;
        };
    }

    private PolicyHolderJpaEntity.Status mapStatusToEntity(PolicyHolderStatus status) {
        return switch (status) {
            case ACTIVE -> PolicyHolderJpaEntity.Status.ACTIVE;
            case INACTIVE -> PolicyHolderJpaEntity.Status.INACTIVE;
            case SUSPENDED -> PolicyHolderJpaEntity.Status.SUSPENDED;
        };
    }
}
