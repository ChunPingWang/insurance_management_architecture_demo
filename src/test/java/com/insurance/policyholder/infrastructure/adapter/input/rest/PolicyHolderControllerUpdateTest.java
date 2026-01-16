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
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.infrastructure.adapter.input.rest.mapper.PolicyHolderRestMapper;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddressRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.UpdatePolicyHolderRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyHolderController Update Tests")
class PolicyHolderControllerUpdateTest {

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

    private UpdatePolicyHolderRequest createValidUpdateRequest() {
        AddressRequest address = new AddressRequest();
        address.setZipCode("200");
        address.setCity("新北市");
        address.setDistrict("板橋區");
        address.setStreet("新地址200號");

        UpdatePolicyHolderRequest request = new UpdatePolicyHolderRequest();
        request.setMobilePhone("0987654321");
        request.setEmail("updated@example.com");
        request.setAddress(address);

        return request;
    }

    private PolicyHolderReadModel createMockUpdatedReadModel() {
        return new PolicyHolderReadModel(
                "PH0000000001",
                "A123456789",
                "王小明",
                "MALE",
                LocalDate.of(1990, 1, 15),
                "0987654321",
                "updated@example.com",
                "200",
                "新北市",
                "板橋區",
                "新地址200號",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
    }

    @Nested
    @DisplayName("PUT /api/v1/policyholders/{id} - 更新保戶")
    class UpdatePolicyHolderTests {

        @Test
        @DisplayName("應成功更新保戶並回傳 200")
        void shouldUpdatePolicyHolderSuccessfully() throws Exception {
            // Given
            UpdatePolicyHolderRequest request = createValidUpdateRequest();
            PolicyHolderReadModel mockResult = createMockUpdatedReadModel();

            when(updatePolicyHolderCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(put("/api/v1/policyholders/PH0000000001")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value("PH0000000001"))
                    .andExpect(jsonPath("$.data.mobilePhone").value("0987654321"))
                    .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                    .andExpect(jsonPath("$.data.address.city").value("新北市"))
                    .andExpect(jsonPath("$.data.version").value(1));
        }

        @Test
        @DisplayName("身分證字號應保持不變")
        void shouldPreserveNationalId() throws Exception {
            // Given
            UpdatePolicyHolderRequest request = createValidUpdateRequest();
            PolicyHolderReadModel mockResult = createMockUpdatedReadModel();

            when(updatePolicyHolderCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(put("/api/v1/policyholders/PH0000000001")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nationalId").value("A123456789"));
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在應回傳 404")
        void shouldReturn404WhenPolicyHolderNotFound() throws Exception {
            // Given
            UpdatePolicyHolderRequest request = createValidUpdateRequest();

            when(updatePolicyHolderCommandHandler.handle(any()))
                    .thenThrow(new PolicyHolderNotFoundException("PH9999999999"));

            // When & Then
            mockMvc.perform(put("/api/v1/policyholders/PH9999999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("POLICY_HOLDER_NOT_FOUND"));
        }

        @Test
        @DisplayName("缺少必填欄位應回傳 400")
        void shouldReturn400WhenMissingRequiredFields() throws Exception {
            // Given
            UpdatePolicyHolderRequest request = new UpdatePolicyHolderRequest();
            // Missing all required fields

            // When & Then
            mockMvc.perform(put("/api/v1/policyholders/PH0000000001")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
