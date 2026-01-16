package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.query.GetPolicyQuery;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.exception.PolicyNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPolicyQueryHandler Tests")
class GetPolicyQueryHandlerTest {

    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String POLICY_ID = "PO0000000001";
    private static final String NATIONAL_ID = "A123456789";

    @Mock
    private PolicyHolderRepository policyHolderRepository;

    private GetPolicyQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPolicyQueryHandler(policyHolderRepository);
    }

    private PolicyHolder createPolicyHolderWithPolicy() {
        PolicyHolder policyHolder = PolicyHolder.reconstitute(
                PolicyHolderId.of(POLICY_HOLDER_ID),
                NationalId.of(NATIONAL_ID),
                PersonalInfo.of("Test User", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                PolicyHolderStatus.ACTIVE,
                1L
        );

        Policy policy = Policy.reconstitute(
                PolicyId.of(POLICY_ID),
                PolicyType.LIFE,
                Money.twd(10000),
                Money.twd(1000000),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                com.insurance.policyholder.domain.model.enums.PolicyStatus.ACTIVE,
                1L
        );

        policyHolder.addReconstitutedPolicy(policy);
        return policyHolder;
    }

    private PolicyHolder createPolicyHolderWithoutPolicy() {
        return PolicyHolder.reconstitute(
                PolicyHolderId.of(POLICY_HOLDER_ID),
                NationalId.of(NATIONAL_ID),
                PersonalInfo.of("Test User", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                PolicyHolderStatus.ACTIVE,
                1L
        );
    }

    @Nested
    @DisplayName("成功查詢保單")
    class SuccessfulQueryTests {

        @Test
        @DisplayName("should return policy when exists")
        void shouldReturnPolicyWhenExists() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, POLICY_ID);
            PolicyHolder policyHolder = createPolicyHolderWithPolicy();

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When
            PolicyReadModel result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertEquals(POLICY_ID, result.getId());
            assertEquals(POLICY_HOLDER_ID, result.getPolicyHolderId());
            assertEquals("LIFE", result.getPolicyType());
            assertEquals(new BigDecimal("10000"), result.getPremium());
            assertEquals(new BigDecimal("1000000"), result.getSumInsured());
            assertEquals("ACTIVE", result.getStatus());
        }

        @Test
        @DisplayName("should return correct policy dates")
        void shouldReturnCorrectPolicyDates() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, POLICY_ID);
            PolicyHolder policyHolder = createPolicyHolderWithPolicy();

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When
            PolicyReadModel result = handler.handle(query);

            // Then
            assertEquals(LocalDate.now(), result.getStartDate());
            assertEquals(LocalDate.now().plusYears(1), result.getEndDate());
        }
    }

    @Nested
    @DisplayName("保戶不存在")
    class PolicyHolderNotFoundTests {

        @Test
        @DisplayName("should throw PolicyHolderNotFoundException when policy holder not found")
        void shouldThrowExceptionWhenPolicyHolderNotFound() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery("PH9999999999", POLICY_ID);

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.empty());

            // When & Then
            PolicyHolderNotFoundException ex = assertThrows(
                    PolicyHolderNotFoundException.class,
                    () -> handler.handle(query)
            );

            assertTrue(ex.getMessage().contains("PH9999999999"));
        }
    }

    @Nested
    @DisplayName("保單不存在")
    class PolicyNotFoundTests {

        @Test
        @DisplayName("should throw PolicyNotFoundException when policy not found")
        void shouldThrowExceptionWhenPolicyNotFound() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, "PO9999999999");
            PolicyHolder policyHolder = createPolicyHolderWithoutPolicy();

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When & Then
            PolicyNotFoundException ex = assertThrows(
                    PolicyNotFoundException.class,
                    () -> handler.handle(query)
            );

            assertTrue(ex.getMessage().contains("PO9999999999"));
        }

        @Test
        @DisplayName("should throw PolicyNotFoundException when wrong policy ID")
        void shouldThrowExceptionWhenWrongPolicyId() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, "PO0000000002");
            PolicyHolder policyHolder = createPolicyHolderWithPolicy();

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When & Then
            assertThrows(PolicyNotFoundException.class, () -> handler.handle(query));
        }
    }

    @Nested
    @DisplayName("Repository 互動測試")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("should call repository with correct policy holder ID")
        void shouldCallRepositoryWithCorrectId() {
            // Given
            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, POLICY_ID);
            PolicyHolder policyHolder = createPolicyHolderWithPolicy();

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When
            handler.handle(query);

            // Then
            verify(policyHolderRepository, times(1)).findById(PolicyHolderId.of(POLICY_HOLDER_ID));
        }
    }

    @Nested
    @DisplayName("不同保單類型測試")
    class DifferentPolicyTypeTests {

        @Test
        @DisplayName("should return correct policy type for HEALTH policy")
        void shouldReturnCorrectPolicyTypeForHealth() {
            // Given
            PolicyHolder policyHolder = PolicyHolder.reconstitute(
                    PolicyHolderId.of(POLICY_HOLDER_ID),
                    NationalId.of(NATIONAL_ID),
                    PersonalInfo.of("Test User", Gender.MALE, LocalDate.of(1990, 1, 15)),
                    ContactInfo.of("0912345678", "test@example.com"),
                    Address.of("10001", "Taipei", "Xinyi", "Test Street"),
                    PolicyHolderStatus.ACTIVE,
                    1L
            );

            Policy healthPolicy = Policy.reconstitute(
                    PolicyId.of(POLICY_ID),
                    PolicyType.HEALTH,
                    Money.twd(5000),
                    Money.twd(500000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1),
                    com.insurance.policyholder.domain.model.enums.PolicyStatus.ACTIVE,
                    1L
            );
            policyHolder.addReconstitutedPolicy(healthPolicy);

            GetPolicyQuery query = new GetPolicyQuery(POLICY_HOLDER_ID, POLICY_ID);

            when(policyHolderRepository.findById(any(PolicyHolderId.class)))
                    .thenReturn(Optional.of(policyHolder));

            // When
            PolicyReadModel result = handler.handle(query);

            // Then
            assertEquals("HEALTH", result.getPolicyType());
            assertEquals(new BigDecimal("5000"), result.getPremium());
            assertEquals(new BigDecimal("500000"), result.getSumInsured());
        }
    }
}
