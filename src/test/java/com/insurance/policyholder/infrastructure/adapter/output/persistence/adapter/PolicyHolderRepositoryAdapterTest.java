package com.insurance.policyholder.infrastructure.adapter.output.persistence.adapter;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyHolderJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.entity.PolicyJpaEntity;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.mapper.PolicyHolderMapper;
import com.insurance.policyholder.infrastructure.adapter.output.persistence.repository.PolicyHolderJpaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyHolderRepositoryAdapter Tests")
class PolicyHolderRepositoryAdapterTest {

    private static final String VALID_ID_A = "A123456789";
    private static final String POLICY_HOLDER_ID = "PH0000000001";

    @Mock
    private PolicyHolderJpaRepository jpaRepository;

    @Mock
    private PolicyHolderMapper mapper;

    @InjectMocks
    private PolicyHolderRepositoryAdapter repositoryAdapter;

    private PolicyHolder createTestPolicyHolder() {
        return PolicyHolder.reconstitute(
                PolicyHolderId.of(POLICY_HOLDER_ID),
                NationalId.of(VALID_ID_A),
                PersonalInfo.of("Test User", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                PolicyHolderStatus.ACTIVE,
                0L
        );
    }

    private PolicyHolderJpaEntity createTestJpaEntity() {
        PolicyHolderJpaEntity entity = new PolicyHolderJpaEntity();
        entity.setId(POLICY_HOLDER_ID);
        entity.setNationalId(VALID_ID_A);
        entity.setName("Test User");
        entity.setGender(PolicyHolderJpaEntity.Gender.MALE);
        entity.setBirthDate(LocalDate.of(1990, 1, 15));
        entity.setMobilePhone("0912345678");
        entity.setEmail("test@example.com");
        entity.setZipCode("10001");
        entity.setCity("Taipei");
        entity.setDistrict("Xinyi");
        entity.setStreet("Test Street");
        entity.setStatus(PolicyHolderJpaEntity.Status.ACTIVE);
        entity.setVersion(0L);
        entity.setPolicies(new ArrayList<>());
        return entity;
    }

    @Nested
    @DisplayName("Save Tests")
    class SaveTests {

        @Test
        @DisplayName("should save policy holder")
        void shouldSavePolicyHolder() {
            // Given
            PolicyHolder policyHolder = createTestPolicyHolder();
            PolicyHolderJpaEntity entity = createTestJpaEntity();

            when(mapper.toEntity(any(PolicyHolder.class))).thenReturn(entity);
            when(jpaRepository.save(any(PolicyHolderJpaEntity.class))).thenReturn(entity);
            when(mapper.toDomain(any(PolicyHolderJpaEntity.class))).thenReturn(policyHolder);

            // When
            PolicyHolder saved = repositoryAdapter.save(policyHolder);

            // Then
            assertNotNull(saved);
            assertEquals(POLICY_HOLDER_ID, saved.getId().getValue());
            verify(mapper).toEntity(policyHolder);
            verify(jpaRepository).save(entity);
            verify(mapper).toDomain(entity);
        }
    }

    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("should find existing policy holder by ID")
        void shouldFindExistingPolicyHolderById() {
            // Given
            PolicyHolder policyHolder = createTestPolicyHolder();
            PolicyHolderJpaEntity entity = createTestJpaEntity();

            when(jpaRepository.findById(POLICY_HOLDER_ID)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(policyHolder);

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findById(PolicyHolderId.of(POLICY_HOLDER_ID));

            // Then
            assertTrue(found.isPresent());
            assertEquals(POLICY_HOLDER_ID, found.get().getId().getValue());
            verify(jpaRepository).findById(POLICY_HOLDER_ID);
            verify(mapper).toDomain(entity);
        }

        @Test
        @DisplayName("should return empty for non-existing ID")
        void shouldReturnEmptyForNonExistingId() {
            // Given
            when(jpaRepository.findById("PH9999999999")).thenReturn(Optional.empty());

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findById(PolicyHolderId.of("PH9999999999"));

            // Then
            assertTrue(found.isEmpty());
            verify(jpaRepository).findById("PH9999999999");
            verify(mapper, never()).toDomain(any());
        }
    }

    @Nested
    @DisplayName("FindByNationalId Tests")
    class FindByNationalIdTests {

        @Test
        @DisplayName("should find policy holder by national ID")
        void shouldFindPolicyHolderByNationalId() {
            // Given
            PolicyHolder policyHolder = createTestPolicyHolder();
            PolicyHolderJpaEntity entity = createTestJpaEntity();

            when(jpaRepository.findByNationalId(VALID_ID_A)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(policyHolder);

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findByNationalId(NationalId.of(VALID_ID_A));

            // Then
            assertTrue(found.isPresent());
            assertEquals(VALID_ID_A, found.get().getNationalId().getValue());
            verify(jpaRepository).findByNationalId(VALID_ID_A);
        }

        @Test
        @DisplayName("should return empty for non-existing national ID")
        void shouldReturnEmptyForNonExistingNationalId() {
            // Given
            when(jpaRepository.findByNationalId("F131104093")).thenReturn(Optional.empty());

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findByNationalId(NationalId.of("F131104093"));

            // Then
            assertTrue(found.isEmpty());
        }
    }

    @Nested
    @DisplayName("ExistsByNationalId Tests")
    class ExistsByNationalIdTests {

        @Test
        @DisplayName("should return true for existing national ID")
        void shouldReturnTrueForExistingNationalId() {
            // Given
            when(jpaRepository.existsByNationalId(VALID_ID_A)).thenReturn(true);

            // When
            boolean exists = repositoryAdapter.existsByNationalId(NationalId.of(VALID_ID_A));

            // Then
            assertTrue(exists);
            verify(jpaRepository).existsByNationalId(VALID_ID_A);
        }

        @Test
        @DisplayName("should return false for non-existing national ID")
        void shouldReturnFalseForNonExistingNationalId() {
            // Given
            when(jpaRepository.existsByNationalId("F131104093")).thenReturn(false);

            // When
            boolean exists = repositoryAdapter.existsByNationalId(NationalId.of("F131104093"));

            // Then
            assertFalse(exists);
        }
    }

    @Nested
    @DisplayName("DeleteById Tests")
    class DeleteByIdTests {

        @Test
        @DisplayName("should delete existing policy holder")
        void shouldDeleteExistingPolicyHolder() {
            // Given
            doNothing().when(jpaRepository).deleteById(POLICY_HOLDER_ID);

            // When
            repositoryAdapter.deleteById(PolicyHolderId.of(POLICY_HOLDER_ID));

            // Then
            verify(jpaRepository).deleteById(POLICY_HOLDER_ID);
        }
    }

    @Nested
    @DisplayName("Policy Holder with Policies Tests")
    class PolicyHolderWithPoliciesTests {

        @Test
        @DisplayName("should save policy holder with policies")
        void shouldSavePolicyHolderWithPolicies() {
            // Given
            PolicyHolder policyHolder = createTestPolicyHolder();
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );
            policyHolder.addPolicy(policy);

            PolicyHolderJpaEntity entity = createTestJpaEntity();
            PolicyJpaEntity policyEntity = new PolicyJpaEntity();
            policyEntity.setId("PO0000000001");
            entity.getPolicies().add(policyEntity);

            when(mapper.toEntity(any(PolicyHolder.class))).thenReturn(entity);
            when(jpaRepository.save(any(PolicyHolderJpaEntity.class))).thenReturn(entity);
            when(mapper.toDomain(any(PolicyHolderJpaEntity.class))).thenReturn(policyHolder);

            // When
            PolicyHolder saved = repositoryAdapter.save(policyHolder);

            // Then
            assertNotNull(saved);
            assertEquals(1, saved.getPolicies().size());
        }
    }

    @Nested
    @DisplayName("Status Handling Tests")
    class StatusHandlingTests {

        @Test
        @DisplayName("should handle inactive status")
        void shouldHandleInactiveStatus() {
            // Given
            PolicyHolder inactivePolicyHolder = PolicyHolder.reconstitute(
                    PolicyHolderId.of(POLICY_HOLDER_ID),
                    NationalId.of(VALID_ID_A),
                    PersonalInfo.of("Test", Gender.MALE, LocalDate.of(1990, 1, 1)),
                    ContactInfo.of("0912345678", "test@test.com"),
                    Address.of("10001", "City", "District", "Street"),
                    PolicyHolderStatus.INACTIVE,
                    0L
            );

            PolicyHolderJpaEntity entity = createTestJpaEntity();
            entity.setStatus(PolicyHolderJpaEntity.Status.INACTIVE);

            when(jpaRepository.findById(POLICY_HOLDER_ID)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(inactivePolicyHolder);

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findById(PolicyHolderId.of(POLICY_HOLDER_ID));

            // Then
            assertTrue(found.isPresent());
            assertEquals(PolicyHolderStatus.INACTIVE, found.get().getStatus());
        }

        @Test
        @DisplayName("should handle suspended status")
        void shouldHandleSuspendedStatus() {
            // Given
            PolicyHolder suspendedPolicyHolder = PolicyHolder.reconstitute(
                    PolicyHolderId.of(POLICY_HOLDER_ID),
                    NationalId.of(VALID_ID_A),
                    PersonalInfo.of("Test", Gender.FEMALE, LocalDate.of(1990, 1, 1)),
                    ContactInfo.of("0912345678", "test@test.com"),
                    Address.of("10001", "City", "District", "Street"),
                    PolicyHolderStatus.SUSPENDED,
                    0L
            );

            PolicyHolderJpaEntity entity = createTestJpaEntity();
            entity.setStatus(PolicyHolderJpaEntity.Status.SUSPENDED);

            when(jpaRepository.findById(POLICY_HOLDER_ID)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(suspendedPolicyHolder);

            // When
            Optional<PolicyHolder> found = repositoryAdapter.findById(PolicyHolderId.of(POLICY_HOLDER_ID));

            // Then
            assertTrue(found.isPresent());
            assertEquals(PolicyHolderStatus.SUSPENDED, found.get().getStatus());
        }
    }
}
