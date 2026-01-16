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
import com.insurance.policyholder.infrastructure.adapter.input.rest.mapper.PolicyHolderRestMapper;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddressRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.CreatePolicyHolderRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyHolderController Create Tests")
class PolicyHolderControllerCreateTest {

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

    private CreatePolicyHolderRequest createValidRequest() {
        AddressRequest address = new AddressRequest();
        address.setZipCode("100");
        address.setCity("台北市");
        address.setDistrict("中正區");
        address.setStreet("忠孝東路100號");

        CreatePolicyHolderRequest request = new CreatePolicyHolderRequest();
        request.setNationalId("A123456789");
        request.setName("王小明");
        request.setGender("MALE");
        request.setBirthDate(LocalDate.of(1990, 1, 15));
        request.setMobilePhone("0912345678");
        request.setEmail("test@example.com");
        request.setAddress(address);

        return request;
    }

    private PolicyHolderReadModel createMockReadModel() {
        return new PolicyHolderReadModel(
                "PH0000000001",
                "A123456789",
                "王小明",
                "MALE",
                LocalDate.of(1990, 1, 15),
                "0912345678",
                "test@example.com",
                "100",
                "台北市",
                "中正區",
                "忠孝東路100號",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now(),
                0L
        );
    }

    @Nested
    @DisplayName("POST /api/v1/policyholders - 建立保戶")
    class CreatePolicyHolderTests {

        @Test
        @DisplayName("應成功建立保戶並回傳 201")
        void shouldCreatePolicyHolderSuccessfully() throws Exception {
            // Given
            CreatePolicyHolderRequest request = createValidRequest();
            PolicyHolderReadModel mockResult = createMockReadModel();

            when(createPolicyHolderCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value("PH0000000001"))
                    .andExpect(jsonPath("$.data.nationalId").value("A123456789"))
                    .andExpect(jsonPath("$.data.name").value("王小明"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("回傳的保戶編號應為 PH + 10位數字格式")
        void shouldReturnPolicyHolderIdInCorrectFormat() throws Exception {
            // Given
            CreatePolicyHolderRequest request = createValidRequest();
            PolicyHolderReadModel mockResult = createMockReadModel();

            when(createPolicyHolderCommandHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(org.hamcrest.Matchers.matchesPattern("^PH\\d{10}$")));
        }
    }

    @Nested
    @DisplayName("驗證錯誤")
    class ValidationErrorTests {

        @Test
        @DisplayName("缺少必填欄位應回傳 400")
        void shouldReturn400WhenMissingRequiredFields() throws Exception {
            // Given
            CreatePolicyHolderRequest request = new CreatePolicyHolderRequest();
            // 未設定任何欄位

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("身分證字號已存在應回傳 400")
        void shouldReturn400WhenNationalIdExists() throws Exception {
            // Given
            CreatePolicyHolderRequest request = createValidRequest();

            when(createPolicyHolderCommandHandler.handle(any()))
                    .thenThrow(new IllegalArgumentException("National ID already exists"));

            // When & Then
            mockMvc.perform(post("/api/v1/policyholders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("INVALID_ARGUMENT"));
        }
    }
}
