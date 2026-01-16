package com.insurance.policyholder.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddPolicyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyController Add Policy Tests")
class PolicyControllerAddTest {

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

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private AddPolicyRequest createValidRequest() {
        AddPolicyRequest request = new AddPolicyRequest();
        request.setPolicyType("LIFE");
        request.setPremium(new BigDecimal("10000"));
        request.setSumInsured(new BigDecimal("1000000"));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusYears(1));
        return request;
    }

    private PolicyReadModel createMockPolicyReadModel() {
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
    @DisplayName("POST /api/v1/policyholders/{id}/policies - 新增保單")
    class AddPolicyTests {

        @Test
        @DisplayName("應成功新增保單並回傳 201")
        void shouldAddPolicySuccessfully() throws Exception {
            // Given
            AddPolicyRequest request = createValidRequest();
            PolicyReadModel mockResult = createMockPolicyReadModel();

            when(addPolicyCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders/PH0000000001/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value("PO0000000001"))
                    .andExpect(jsonPath("$.data.policyType").value("LIFE"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("保單編號應為 PO 開頭")
        void shouldReturnPolicyIdWithCorrectFormat() throws Exception {
            // Given
            AddPolicyRequest request = createValidRequest();
            PolicyReadModel mockResult = createMockPolicyReadModel();

            when(addPolicyCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders/PH0000000001/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(org.hamcrest.Matchers.startsWith("PO")));
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在應回傳 404")
        void shouldReturn404WhenPolicyHolderNotFound() throws Exception {
            // Given
            AddPolicyRequest request = createValidRequest();

            when(addPolicyCommandHandler.handle(any()))
                    .thenThrow(new PolicyHolderNotFoundException("PH9999999999"));

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders/PH9999999999/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("POLICY_HOLDER_NOT_FOUND"));
        }

        @Test
        @DisplayName("INACTIVE 保戶新增保單應回傳 400")
        void shouldReturn400WhenPolicyHolderIsInactive() throws Exception {
            // Given
            AddPolicyRequest request = createValidRequest();

            when(addPolicyCommandHandler.handle(any()))
                    .thenThrow(new IllegalStateException("Cannot add policy to inactive policyholder"));

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders/PH0000000001/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
        }

        @Test
        @DisplayName("缺少必填欄位應回傳 400")
        void shouldReturn400WhenMissingRequiredFields() throws Exception {
            // Given
            AddPolicyRequest request = new AddPolicyRequest();
            // Missing all required fields

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders/PH0000000001/policies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
