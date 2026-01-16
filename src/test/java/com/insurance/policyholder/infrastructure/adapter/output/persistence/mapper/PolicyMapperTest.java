package com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper;

import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.Money;
import com.insurance.policyholder.domain.model.valueobject.PolicyId;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyMapper Tests")
class PolicyMapperTest {

    private PolicyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PolicyMapper();
    }

    private PolicyJpaEntity createJpaEntity() {
        PolicyJpaEntity entity = new PolicyJpaEntity();
        entity.setId("PO0000000001");
        entity.setPolicyType(PolicyJpaEntity.PolicyType.LIFE);
        entity.setPremiumAmount(new BigDecimal("10000"));
        entity.setPremiumCurrency("TWD");
        entity.setSumInsured(new BigDecimal("1000000"));
        entity.setSumInsuredCurrency("TWD");
        entity.setStartDate(LocalDate.of(2024, 1, 1));
        entity.setEndDate(LocalDate.of(2025, 1, 1));
        entity.setStatus(PolicyJpaEntity.PolicyStatus.ACTIVE);
        entity.setVersion(1L);
        return entity;
    }

    private Policy createDomainPolicy() {
        return Policy.reconstitute(
                PolicyId.of("PO0000000001"),
                PolicyType.LIFE,
                Money.twd(10000),
                Money.twd(1000000),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2025, 1, 1),
                PolicyStatus.ACTIVE,
                1L
        );
    }

    @Nested
    @DisplayName("JPA Entity to Domain Model Tests")
    class ToDomainTests {

        @Test
        @DisplayName("should convert JPA entity to domain model")
        void shouldConvertToDomain() {
            // Given
            PolicyJpaEntity entity = createJpaEntity();

            // When
            Policy domain = mapper.toDomain(entity);

            // Then
            assertNotNull(domain);
            assertEquals("PO0000000001", domain.getId().getValue());
            assertEquals(PolicyType.LIFE, domain.getPolicyType());
            assertEquals(new BigDecimal("10000"), domain.getPremium().getAmount());
            assertEquals(new BigDecimal("1000000"), domain.getSumInsured().getAmount());
            assertEquals(LocalDate.of(2024, 1, 1), domain.getStartDate());
            assertEquals(LocalDate.of(2025, 1, 1), domain.getEndDate());
            assertEquals(PolicyStatus.ACTIVE, domain.getStatus());
            assertEquals(1L, domain.getVersion());
        }

        @Test
        @DisplayName("should return null for null entity")
        void shouldReturnNullForNullEntity() {
            Policy domain = mapper.toDomain(null);
            assertNull(domain);
        }

        @Test
        @DisplayName("should convert all policy types")
        void shouldConvertAllPolicyTypes() {
            for (PolicyJpaEntity.PolicyType jpaType : PolicyJpaEntity.PolicyType.values()) {
                PolicyJpaEntity entity = createJpaEntity();
                entity.setPolicyType(jpaType);

                Policy domain = mapper.toDomain(entity);

                assertNotNull(domain.getPolicyType());
                assertEquals(jpaType.name(), domain.getPolicyType().name());
            }
        }

        @Test
        @DisplayName("should convert all policy statuses")
        void shouldConvertAllPolicyStatuses() {
            for (PolicyJpaEntity.PolicyStatus jpaStatus : PolicyJpaEntity.PolicyStatus.values()) {
                PolicyJpaEntity entity = createJpaEntity();
                entity.setStatus(jpaStatus);

                Policy domain = mapper.toDomain(entity);

                assertNotNull(domain.getStatus());
                assertEquals(jpaStatus.name(), domain.getStatus().name());
            }
        }
    }

    @Nested
    @DisplayName("Domain Model to JPA Entity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("should convert domain model to JPA entity")
        void shouldConvertToEntity() {
            // Given
            Policy domain = createDomainPolicy();
            PolicyHolderJpaEntity policyHolder = new PolicyHolderJpaEntity();
            policyHolder.setId("PH0000000001");

            // When
            PolicyJpaEntity entity = mapper.toEntity(domain, policyHolder);

            // Then
            assertNotNull(entity);
            assertEquals("PO0000000001", entity.getId());
            assertEquals(policyHolder, entity.getPolicyHolder());
            assertEquals(PolicyJpaEntity.PolicyType.LIFE, entity.getPolicyType());
            assertEquals(new BigDecimal("10000"), entity.getPremiumAmount());
            assertEquals("TWD", entity.getPremiumCurrency());
            assertEquals(new BigDecimal("1000000"), entity.getSumInsured());
            assertEquals("TWD", entity.getSumInsuredCurrency());
            assertEquals(LocalDate.of(2024, 1, 1), entity.getStartDate());
            assertEquals(LocalDate.of(2025, 1, 1), entity.getEndDate());
            assertEquals(PolicyJpaEntity.PolicyStatus.ACTIVE, entity.getStatus());
            assertEquals(1L, entity.getVersion());
        }

        @Test
        @DisplayName("should return null for null domain")
        void shouldReturnNullForNullDomain() {
            PolicyJpaEntity entity = mapper.toEntity(null, new PolicyHolderJpaEntity());
            assertNull(entity);
        }

        @Test
        @DisplayName("should convert all domain policy types to JPA")
        void shouldConvertAllDomainPolicyTypes() {
            PolicyHolderJpaEntity policyHolder = new PolicyHolderJpaEntity();

            for (PolicyType domainType : PolicyType.values()) {
                Policy domain = Policy.reconstitute(
                        PolicyId.of("PO0000000001"),
                        domainType,
                        Money.twd(10000),
                        Money.twd(1000000),
                        LocalDate.now(),
                        LocalDate.now().plusYears(1),
                        PolicyStatus.ACTIVE,
                        1L
                );

                PolicyJpaEntity entity = mapper.toEntity(domain, policyHolder);

                assertNotNull(entity.getPolicyType());
                assertEquals(domainType.name(), entity.getPolicyType().name());
            }
        }
    }

    @Nested
    @DisplayName("Update Entity Tests")
    class UpdateEntityTests {

        @Test
        @DisplayName("should update existing entity")
        void shouldUpdateExistingEntity() {
            // Given
            PolicyJpaEntity entity = createJpaEntity();
            Policy domain = Policy.reconstitute(
                    PolicyId.of("PO0000000001"),
                    PolicyType.HEALTH,
                    Money.twd(20000),
                    Money.twd(2000000),
                    LocalDate.of(2024, 6, 1),
                    LocalDate.of(2026, 6, 1),
                    PolicyStatus.LAPSED,
                    2L
            );

            // When
            mapper.updateEntity(entity, domain);

            // Then
            assertEquals(PolicyJpaEntity.PolicyType.HEALTH, entity.getPolicyType());
            assertEquals(new BigDecimal("20000"), entity.getPremiumAmount());
            assertEquals(new BigDecimal("2000000"), entity.getSumInsured());
            assertEquals(LocalDate.of(2024, 6, 1), entity.getStartDate());
            assertEquals(LocalDate.of(2026, 6, 1), entity.getEndDate());
            assertEquals(PolicyJpaEntity.PolicyStatus.LAPSED, entity.getStatus());
        }

        @Test
        @DisplayName("should handle null entity gracefully")
        void shouldHandleNullEntityGracefully() {
            Policy domain = createDomainPolicy();
            assertDoesNotThrow(() -> mapper.updateEntity(null, domain));
        }

        @Test
        @DisplayName("should handle null domain gracefully")
        void shouldHandleNullDomainGracefully() {
            PolicyJpaEntity entity = createJpaEntity();
            assertDoesNotThrow(() -> mapper.updateEntity(entity, null));
        }
    }
}
