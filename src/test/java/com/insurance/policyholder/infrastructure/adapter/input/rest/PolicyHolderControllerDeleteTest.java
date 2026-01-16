package com.insurance.policyholder.infrastructure.adapter.input.rest;

import com.insurance.policyholder.application.commandhandler.AddPolicyCommandHandler;
import com.insurance.policyholder.application.commandhandler.CreatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.DeletePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.UpdatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderPoliciesQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyQueryHandler;
import com.insurance.policyholder.application.queryhandler.SearchPolicyHoldersQueryHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyHolderController Delete Tests")
class PolicyHolderControllerDeleteTest {

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

    @Nested
    @DisplayName("DELETE /api/v1/policyholders/{id} - 刪除保戶")
    class DeletePolicyHolderTests {

        @Test
        @DisplayName("應成功刪除保戶並回傳 204")
        void shouldDeletePolicyHolderSuccessfully() throws Exception {
            // Given
            doNothing().when(deletePolicyHolderCommandHandler).handle(any());

            // When & Then
            mockMvc.perform(delete("/api/v1/policyholders/PH0000000001"))
                    .andExpect(status().isNoContent());

            verify(deletePolicyHolderCommandHandler).handle(any());
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在應回傳 404")
        void shouldReturn404WhenPolicyHolderNotFound() throws Exception {
            // Given
            doThrow(new PolicyHolderNotFoundException("PH9999999999"))
                    .when(deletePolicyHolderCommandHandler).handle(any());

            // When & Then
            mockMvc.perform(delete("/api/v1/policyholders/PH9999999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("POLICY_HOLDER_NOT_FOUND"));
        }

        @Test
        @DisplayName("已刪除的保戶再次刪除應回傳 400")
        void shouldReturn400WhenDeletingAlreadyInactivePolicyHolder() throws Exception {
            // Given
            doThrow(new IllegalStateException("PolicyHolder is already inactive"))
                    .when(deletePolicyHolderCommandHandler).handle(any());

            // When & Then
            mockMvc.perform(delete("/api/v1/policyholders/PH0000000001"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
        }
    }
}
