package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.query.GetPolicyHolderPoliciesQuery;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPolicyHolderPoliciesQueryHandler Tests")
class GetPolicyHolderPoliciesQueryHandlerTest {

    @Mock
    private PolicyHolderRepository repository;

    private GetPolicyHolderPoliciesQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPolicyHolderPoliciesQueryHandler(repository);
    }

    private PolicyHolder createPolicyHolderWithPolicies() {
        PolicyHolder policyHolder = PolicyHolder.create(
                PolicyHolderId.of("PH0000000001"),
                NationalId.of("A123456789"),
                PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("100", "台北市", "中正區", "忠孝東路100號")
        );

        Policy lifePolicy = Policy.create(
                PolicyType.LIFE,
                Money.twd(10000),
                Money.twd(1000000),
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );

        Policy healthPolicy = Policy.create(
                PolicyType.HEALTH,
                Money.twd(5000),
                Money.twd(500000),
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );

        policyHolder.addPolicy(lifePolicy);
        policyHolder.addPolicy(healthPolicy);

        return policyHolder;
    }

    private PolicyHolder createPolicyHolderWithoutPolicies() {
        return PolicyHolder.create(
                PolicyHolderId.of("PH0000000002"),
                NationalId.of("B123456780"),
                PersonalInfo.of("李小華", Gender.FEMALE, LocalDate.of(1985, 5, 20)),
                ContactInfo.of("0923456789", "test2@example.com"),
                Address.of("200", "新北市", "板橋區", "中山路200號")
        );
    }

    @Nested
    @DisplayName("查詢保戶保單")
    class GetPoliciesTests {

        @Test
        @DisplayName("應成功查詢保戶的所有保單")
        void shouldReturnAllPoliciesForPolicyHolder() {
            // Given
            PolicyHolder policyHolder = createPolicyHolderWithPolicies();
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH0000000001");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));

            // When
            List<PolicyReadModel> result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("保單列表應包含正確的保單資訊")
        void shouldContainCorrectPolicyInfo() {
            // Given
            PolicyHolder policyHolder = createPolicyHolderWithPolicies();
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH0000000001");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));

            // When
            List<PolicyReadModel> result = handler.handle(query);

            // Then
            assertTrue(result.stream().anyMatch(p -> p.getPolicyType().equals("LIFE")));
            assertTrue(result.stream().anyMatch(p -> p.getPolicyType().equals("HEALTH")));
            assertTrue(result.stream().allMatch(p -> p.getPolicyHolderId().equals("PH0000000001")));
            assertTrue(result.stream().allMatch(p -> p.getId().startsWith("PO")));
        }

        @Test
        @DisplayName("保戶無保單時應回傳空列表")
        void shouldReturnEmptyListWhenNoPolicies() {
            // Given
            PolicyHolder policyHolder = createPolicyHolderWithoutPolicies();
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH0000000002");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));

            // When
            List<PolicyReadModel> result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("依類型篩選")
    class FilterByTypeTests {

        @Test
        @DisplayName("應可依保單類型篩選")
        void shouldFilterByPolicyType() {
            // Given
            PolicyHolder policyHolder = createPolicyHolderWithPolicies();
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH0000000001", "LIFE", null);

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));

            // When
            List<PolicyReadModel> result = handler.handle(query);

            // Then
            assertEquals(1, result.size());
            assertEquals("LIFE", result.get(0).getPolicyType());
        }
    }

    @Nested
    @DisplayName("依狀態篩選")
    class FilterByStatusTests {

        @Test
        @DisplayName("應可依保單狀態篩選")
        void shouldFilterByPolicyStatus() {
            // Given
            PolicyHolder policyHolder = createPolicyHolderWithPolicies();
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH0000000001", null, "ACTIVE");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));

            // When
            List<PolicyReadModel> result = handler.handle(query);

            // Then
            assertFalse(result.isEmpty());
            assertTrue(result.stream().allMatch(p -> p.getStatus().equals("ACTIVE")));
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在時應拋出例外")
        void shouldThrowExceptionWhenPolicyHolderNotFound() {
            // Given
            GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery("PH9999999999");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handle(query));
        }
    }
}
