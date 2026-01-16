package com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyHolderMapper Tests")
class PolicyHolderMapperTest {

    @Mock
    private PolicyMapper policyMapper;

    private PolicyHolderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PolicyHolderMapper(policyMapper);
    }

    private PolicyHolderJpaEntity createJpaEntity() {
        PolicyHolderJpaEntity entity = new PolicyHolderJpaEntity();
        entity.setId("PH0000000001");
        entity.setNationalId("A123456789");
        entity.setName("John Doe");
        entity.setGender(PolicyHolderJpaEntity.Gender.MALE);
        entity.setBirthDate(LocalDate.of(1990, 1, 15));
        entity.setMobilePhone("0912345678");
        entity.setEmail("john@example.com");
        entity.setZipCode("10001");
        entity.setCity("Taipei");
        entity.setDistrict("Xinyi");
        entity.setStreet("Test Street");
        entity.setStatus(PolicyHolderJpaEntity.Status.ACTIVE);
        entity.setVersion(1L);
        entity.setPolicies(new ArrayList<>());
        return entity;
    }

    private PolicyHolder createDomainPolicyHolder() {
        return PolicyHolder.reconstitute(
                PolicyHolderId.of("PH0000000001"),
                NationalId.of("A123456789"),
                PersonalInfo.of("John Doe", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "john@example.com"),
                Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                PolicyHolderStatus.ACTIVE,
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
            PolicyHolderJpaEntity entity = createJpaEntity();

            // When
            PolicyHolder domain = mapper.toDomain(entity);

            // Then
            assertNotNull(domain);
            assertEquals("PH0000000001", domain.getId().getValue());
            assertEquals("A123456789", domain.getNationalId().getValue());
            assertEquals("John Doe", domain.getPersonalInfo().getName());
            assertEquals(Gender.MALE, domain.getPersonalInfo().getGender());
            assertEquals(LocalDate.of(1990, 1, 15), domain.getPersonalInfo().getBirthDate());
            assertEquals("0912345678", domain.getContactInfo().getMobilePhone());
            assertEquals("john@example.com", domain.getContactInfo().getEmail());
            assertEquals("10001", domain.getAddress().getZipCode());
            assertEquals("Taipei", domain.getAddress().getCity());
            assertEquals("Xinyi", domain.getAddress().getDistrict());
            assertEquals("Test Street", domain.getAddress().getStreet());
            assertEquals(PolicyHolderStatus.ACTIVE, domain.getStatus());
            assertEquals(1L, domain.getVersion());
        }

        @Test
        @DisplayName("should return null for null entity")
        void shouldReturnNullForNullEntity() {
            PolicyHolder domain = mapper.toDomain(null);
            assertNull(domain);
        }

        @Test
        @DisplayName("should convert female gender")
        void shouldConvertFemaleGender() {
            PolicyHolderJpaEntity entity = createJpaEntity();
            entity.setGender(PolicyHolderJpaEntity.Gender.FEMALE);

            PolicyHolder domain = mapper.toDomain(entity);

            assertEquals(Gender.FEMALE, domain.getPersonalInfo().getGender());
        }

        @Test
        @DisplayName("should convert inactive status")
        void shouldConvertInactiveStatus() {
            PolicyHolderJpaEntity entity = createJpaEntity();
            entity.setStatus(PolicyHolderJpaEntity.Status.INACTIVE);

            PolicyHolder domain = mapper.toDomain(entity);

            assertEquals(PolicyHolderStatus.INACTIVE, domain.getStatus());
        }

        @Test
        @DisplayName("should convert suspended status")
        void shouldConvertSuspendedStatus() {
            PolicyHolderJpaEntity entity = createJpaEntity();
            entity.setStatus(PolicyHolderJpaEntity.Status.SUSPENDED);

            PolicyHolder domain = mapper.toDomain(entity);

            assertEquals(PolicyHolderStatus.SUSPENDED, domain.getStatus());
        }

        @Test
        @DisplayName("should convert entity with policies")
        void shouldConvertEntityWithPolicies() {
            // Given
            PolicyHolderJpaEntity entity = createJpaEntity();
            PolicyJpaEntity policyEntity = new PolicyJpaEntity();
            policyEntity.setId("PO0000000001");
            entity.getPolicies().add(policyEntity);

            Policy domainPolicy = Policy.reconstitute(
                    PolicyId.of("PO0000000001"),
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    PolicyStatus.ACTIVE,
                    1L
            );

            when(policyMapper.toDomain(any(PolicyJpaEntity.class))).thenReturn(domainPolicy);

            // When
            PolicyHolder domain = mapper.toDomain(entity);

            // Then
            assertNotNull(domain);
            assertEquals(1, domain.getPolicies().size());
            verify(policyMapper, times(1)).toDomain(policyEntity);
        }
    }

    @Nested
    @DisplayName("Domain Model to JPA Entity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("should convert domain model to JPA entity")
        void shouldConvertToEntity() {
            // Given
            PolicyHolder domain = createDomainPolicyHolder();

            // When
            PolicyHolderJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertNotNull(entity);
            assertEquals("PH0000000001", entity.getId());
            assertEquals("A123456789", entity.getNationalId());
            assertEquals("John Doe", entity.getName());
            assertEquals(PolicyHolderJpaEntity.Gender.MALE, entity.getGender());
            assertEquals(LocalDate.of(1990, 1, 15), entity.getBirthDate());
            assertEquals("0912345678", entity.getMobilePhone());
            assertEquals("john@example.com", entity.getEmail());
            assertEquals("10001", entity.getZipCode());
            assertEquals("Taipei", entity.getCity());
            assertEquals("Xinyi", entity.getDistrict());
            assertEquals("Test Street", entity.getStreet());
            assertEquals(PolicyHolderJpaEntity.Status.ACTIVE, entity.getStatus());
            assertEquals(1L, entity.getVersion());
        }

        @Test
        @DisplayName("should return null for null domain")
        void shouldReturnNullForNullDomain() {
            PolicyHolderJpaEntity entity = mapper.toEntity(null);
            assertNull(entity);
        }

        @Test
        @DisplayName("should convert female gender to entity")
        void shouldConvertFemaleGenderToEntity() {
            PolicyHolder domain = PolicyHolder.reconstitute(
                    PolicyHolderId.of("PH0000000001"),
                    NationalId.of("A123456789"),
                    PersonalInfo.of("Jane Doe", Gender.FEMALE, LocalDate.of(1990, 1, 15)),
                    ContactInfo.of("0912345678", "jane@example.com"),
                    Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                    PolicyHolderStatus.ACTIVE,
                    1L
            );

            PolicyHolderJpaEntity entity = mapper.toEntity(domain);

            assertEquals(PolicyHolderJpaEntity.Gender.FEMALE, entity.getGender());
        }

        @Test
        @DisplayName("should convert inactive status to entity")
        void shouldConvertInactiveStatusToEntity() {
            PolicyHolder domain = PolicyHolder.reconstitute(
                    PolicyHolderId.of("PH0000000001"),
                    NationalId.of("A123456789"),
                    PersonalInfo.of("John Doe", Gender.MALE, LocalDate.of(1990, 1, 15)),
                    ContactInfo.of("0912345678", "john@example.com"),
                    Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                    PolicyHolderStatus.INACTIVE,
                    1L
            );

            PolicyHolderJpaEntity entity = mapper.toEntity(domain);

            assertEquals(PolicyHolderJpaEntity.Status.INACTIVE, entity.getStatus());
        }

        @Test
        @DisplayName("should convert domain with policies")
        void shouldConvertDomainWithPolicies() {
            // Given
            PolicyHolder domain = createDomainPolicyHolder();
            Policy policy = Policy.reconstitute(
                    PolicyId.of("PO0000000001"),
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    PolicyStatus.ACTIVE,
                    1L
            );
            domain.addReconstitutedPolicy(policy);

            PolicyJpaEntity policyEntity = new PolicyJpaEntity();
            policyEntity.setId("PO0000000001");

            when(policyMapper.toEntity(any(Policy.class), any(PolicyHolderJpaEntity.class)))
                    .thenReturn(policyEntity);

            // When
            PolicyHolderJpaEntity entity = mapper.toEntity(domain);

            // Then
            assertNotNull(entity);
            assertEquals(1, entity.getPolicies().size());
            verify(policyMapper, times(1)).toEntity(eq(policy), any(PolicyHolderJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("Update Entity Tests")
    class UpdateEntityTests {

        @Test
        @DisplayName("should update existing entity")
        void shouldUpdateExistingEntity() {
            // Given
            PolicyHolderJpaEntity entity = createJpaEntity();
            PolicyHolder domain = PolicyHolder.reconstitute(
                    PolicyHolderId.of("PH0000000001"),
                    NationalId.of("A123456789"),
                    PersonalInfo.of("Jane Doe Updated", Gender.FEMALE, LocalDate.of(1985, 6, 15)),
                    ContactInfo.of("0987654321", "updated@example.com"),
                    Address.of("80000", "Kaohsiung", "Sanmin", "New Street"),
                    PolicyHolderStatus.SUSPENDED,
                    2L
            );

            // When
            mapper.updateEntity(entity, domain);

            // Then
            assertEquals("Jane Doe Updated", entity.getName());
            assertEquals(PolicyHolderJpaEntity.Gender.FEMALE, entity.getGender());
            assertEquals(LocalDate.of(1985, 6, 15), entity.getBirthDate());
            assertEquals("0987654321", entity.getMobilePhone());
            assertEquals("updated@example.com", entity.getEmail());
            assertEquals("80000", entity.getZipCode());
            assertEquals("Kaohsiung", entity.getCity());
            assertEquals("Sanmin", entity.getDistrict());
            assertEquals("New Street", entity.getStreet());
            assertEquals(PolicyHolderJpaEntity.Status.SUSPENDED, entity.getStatus());
        }

        @Test
        @DisplayName("should handle null entity gracefully")
        void shouldHandleNullEntityGracefully() {
            PolicyHolder domain = createDomainPolicyHolder();
            assertDoesNotThrow(() -> mapper.updateEntity(null, domain));
        }

        @Test
        @DisplayName("should handle null domain gracefully")
        void shouldHandleNullDomainGracefully() {
            PolicyHolderJpaEntity entity = createJpaEntity();
            assertDoesNotThrow(() -> mapper.updateEntity(entity, null));
        }
    }
}
