package com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper;

import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.Money;
import com.insurance.policyholder.domain.model.valueobject.PolicyId;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;

/**
 * 保單領域模型與 JPA 實體的轉換器
 */
@Component
public class PolicyMapper {

    /**
     * JPA 實體轉換為領域模型
     */
    public Policy toDomain(PolicyJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        PolicyId id = PolicyId.of(entity.getId());
        PolicyType policyType = mapPolicyTypeToDomain(entity.getPolicyType());

        Money premium = Money.of(
                entity.getPremiumAmount(),
                Currency.getInstance(entity.getPremiumCurrency())
        );

        Money sumInsured = Money.of(
                entity.getSumInsured(),
                Currency.getInstance(entity.getSumInsuredCurrency())
        );

        PolicyStatus status = mapPolicyStatusToDomain(entity.getStatus());

        return Policy.reconstitute(
                id,
                policyType,
                premium,
                sumInsured,
                entity.getStartDate(),
                entity.getEndDate(),
                status,
                entity.getVersion()
        );
    }

    /**
     * 領域模型轉換為 JPA 實體
     */
    public PolicyJpaEntity toEntity(Policy domain, PolicyHolderJpaEntity policyHolder) {
        if (domain == null) {
            return null;
        }

        PolicyJpaEntity entity = new PolicyJpaEntity();
        entity.setId(domain.getId().getValue());
        entity.setPolicyHolder(policyHolder);
        entity.setPolicyType(mapPolicyTypeToEntity(domain.getPolicyType()));
        entity.setPremiumAmount(domain.getPremium().getAmount());
        entity.setPremiumCurrency(domain.getPremium().getCurrency().getCurrencyCode());
        entity.setSumInsured(domain.getSumInsured().getAmount());
        entity.setSumInsuredCurrency(domain.getSumInsured().getCurrency().getCurrencyCode());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(mapPolicyStatusToEntity(domain.getStatus()));
        entity.setVersion(domain.getVersion());

        return entity;
    }

    /**
     * 更新現有 JPA 實體
     */
    public void updateEntity(PolicyJpaEntity entity, Policy domain) {
        if (entity == null || domain == null) {
            return;
        }

        entity.setPolicyType(mapPolicyTypeToEntity(domain.getPolicyType()));
        entity.setPremiumAmount(domain.getPremium().getAmount());
        entity.setPremiumCurrency(domain.getPremium().getCurrency().getCurrencyCode());
        entity.setSumInsured(domain.getSumInsured().getAmount());
        entity.setSumInsuredCurrency(domain.getSumInsured().getCurrency().getCurrencyCode());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(mapPolicyStatusToEntity(domain.getStatus()));
    }

    private PolicyType mapPolicyTypeToDomain(PolicyJpaEntity.PolicyType type) {
        return switch (type) {
            case LIFE -> PolicyType.LIFE;
            case HEALTH -> PolicyType.HEALTH;
            case ACCIDENT -> PolicyType.ACCIDENT;
            case TRAVEL -> PolicyType.TRAVEL;
            case PROPERTY -> PolicyType.PROPERTY;
            case AUTO -> PolicyType.AUTO;
            case SAFETY -> PolicyType.SAFETY;
        };
    }

    private PolicyJpaEntity.PolicyType mapPolicyTypeToEntity(PolicyType type) {
        return switch (type) {
            case LIFE -> PolicyJpaEntity.PolicyType.LIFE;
            case HEALTH -> PolicyJpaEntity.PolicyType.HEALTH;
            case ACCIDENT -> PolicyJpaEntity.PolicyType.ACCIDENT;
            case TRAVEL -> PolicyJpaEntity.PolicyType.TRAVEL;
            case PROPERTY -> PolicyJpaEntity.PolicyType.PROPERTY;
            case AUTO -> PolicyJpaEntity.PolicyType.AUTO;
            case SAFETY -> PolicyJpaEntity.PolicyType.SAFETY;
        };
    }

    private PolicyStatus mapPolicyStatusToDomain(PolicyJpaEntity.PolicyStatus status) {
        return switch (status) {
            case ACTIVE -> PolicyStatus.ACTIVE;
            case LAPSED -> PolicyStatus.LAPSED;
            case TERMINATED -> PolicyStatus.TERMINATED;
        };
    }

    private PolicyJpaEntity.PolicyStatus mapPolicyStatusToEntity(PolicyStatus status) {
        return switch (status) {
            case ACTIVE -> PolicyJpaEntity.PolicyStatus.ACTIVE;
            case LAPSED -> PolicyJpaEntity.PolicyStatus.LAPSED;
            case TERMINATED -> PolicyJpaEntity.PolicyStatus.TERMINATED;
        };
    }
}
