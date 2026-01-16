package com.insurance.policyholder.infrastructure.adapter.input.rest;

import com.insurance.policyholder.application.commandhandler.AddPolicyCommandHandler;
import com.insurance.policyholder.application.commandhandler.CreatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.DeletePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.UpdatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderPoliciesQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyQueryHandler;
import com.insurance.policyholder.application.queryhandler.SearchPolicyHoldersQueryHandler;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.infrastructure.adapter.input.rest.mapper.PolicyHolderRestMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyController Query Tests")
class PolicyControllerQueryTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreatePolicyHolderCommandHandler createPolicyHolderCommandHandler;

    @MockBean
    private UpdatePolicyHolderCommandHandler updatePolicyHolderCommandHandler;

    @MockBean
    private DeletePolicyHolderCommandHandler deletePolicyHolderCommandHandler;

    @MockBean
    private AddPolicyCommandHandler addPolicyCommandHandler;

    @MockBean
    private GetPolicyHolderQueryHandler getPolicyHolderQueryHandler;

    @MockBean
    private SearchPolicyHoldersQueryHandler searchPolicyHoldersQueryHandler;

    @MockBean
    private GetPolicyHolderPoliciesQueryHandler getPolicyHolderPoliciesQueryHandler;

    @MockBean
    private GetPolicyQueryHandler getPolicyQueryHandler;

    private List<PolicyReadModel> createMockPolicyList() {
        PolicyReadModel policy1 = new PolicyReadModel(
                "PO0000000001",
                "PH0000000001",
                "LIFE",
                new BigDecimal("10000"),
                new BigDecimal("1000000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "ACTIVE"
        );

        PolicyReadModel policy2 = new PolicyReadModel(
                "PO0000000002",
                "PH0000000001",
                "HEALTH",
                new BigDecimal("5000"),
                new BigDecimal("500000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "ACTIVE"
        );

        return Arrays.asList(policy1, policy2);
    }

    private PolicyReadModel createMockPolicy() {
        return new PolicyReadModel(
                "PO0000000001",
                "PH0000000001",
                "LIFE",
                new BigDecimal("10000"),
                new BigDecimal("1000000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "ACTIVE"
        );
    }

    @Nested
    @DisplayName("GET /api/v1/policyholders/{id}/policies - 查詢保戶保單")
    class GetPolicyHolderPoliciesTests {

        @Test
        @DisplayName("應成功查詢保戶的所有保單")
        void shouldReturnPoliciesSuccessfully() throws Exception {
            // Given
            List<PolicyReadModel> mockPolicies = createMockPolicyList();
            when(getPolicyHolderPoliciesQueryHandler.handle(any())).thenReturn(mockPolicies);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value("PO0000000001"))
                    .andExpect(jsonPath("$.data[0].policyType").value("LIFE"))
                    .andExpect(jsonPath("$.data[1].id").value("PO0000000002"))
                    .andExpect(jsonPath("$.data[1].policyType").value("HEALTH"));
        }

        @Test
        @DisplayName("保戶無保單時應回傳空陣列")
        void shouldReturnEmptyArrayWhenNoPolicies() throws Exception {
            // Given
            when(getPolicyHolderPoliciesQueryHandler.handle(any())).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("保戶不存在應回傳 404")
        void shouldReturn404WhenPolicyHolderNotFound() throws Exception {
            // Given
            when(getPolicyHolderPoliciesQueryHandler.handle(any()))
                    .thenThrow(new PolicyHolderNotFoundException("PH9999999999"));

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH9999999999/policies"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("POLICY_HOLDER_NOT_FOUND"));
        }

        @Test
        @DisplayName("應支援依類型篩選")
        void shouldSupportFilterByType() throws Exception {
            // Given
            PolicyReadModel lifePolicy = createMockPolicy();
            when(getPolicyHolderPoliciesQueryHandler.handle(any())).thenReturn(List.of(lifePolicy));

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies")
                            .param("type", "LIFE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].policyType").value("LIFE"));
        }

        @Test
        @DisplayName("應支援依狀態篩選")
        void shouldSupportFilterByStatus() throws Exception {
            // Given
            List<PolicyReadModel> mockPolicies = createMockPolicyList();
            when(getPolicyHolderPoliciesQueryHandler.handle(any())).thenReturn(mockPolicies);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies")
                            .param("status", "ACTIVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/policyholders/{phId}/policies/{policyId} - 查詢單一保單")
    class GetSinglePolicyTests {

        @Test
        @DisplayName("應成功查詢單一保單")
        void shouldReturnSinglePolicy() throws Exception {
            // Given
            PolicyReadModel mockPolicy = createMockPolicy();
            when(getPolicyQueryHandler.handle(any())).thenReturn(mockPolicy);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies/PO0000000001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value("PO0000000001"))
                    .andExpect(jsonPath("$.data.policyType").value("LIFE"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("保單不存在應回傳 404")
        void shouldReturn404WhenPolicyNotFound() throws Exception {
            // Given
            when(getPolicyQueryHandler.handle(any()))
                    .thenThrow(new PolicyHolderNotFoundException("Policy not found: PO9999999999"));

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001/policies/PO9999999999"))
                    .andExpect(status().isNotFound());
        }
    }
}
