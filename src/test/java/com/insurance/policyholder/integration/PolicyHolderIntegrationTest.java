package com.insurance.policyholder.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddPolicyRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddressRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.CreatePolicyHolderRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.UpdatePolicyHolderRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 保戶管理系統整合測試
 * 驗證完整 API 工作流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PolicyHolder Integration Tests - Full API Workflow")
class PolicyHolderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private static String createdPolicyHolderId;
    private static String createdPolicyId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========================================
    // User Story 1: 新增保戶
    // ========================================

    @Test
    @Order(1)
    @DisplayName("US1: 應成功建立新保戶")
    void shouldCreatePolicyHolder() throws Exception {
        // Given
        CreatePolicyHolderRequest request = createPolicyHolderRequest();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/policyholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(matchesPattern("^PH\\d{10}$")))
                .andExpect(jsonPath("$.data.nationalId").value("A123456789"))
                .andExpect(jsonPath("$.data.name").value("王小明"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andReturn();

        // 儲存保戶 ID 供後續測試使用
        String response = result.getResponse().getContentAsString();
        createdPolicyHolderId = objectMapper.readTree(response).path("data").path("id").asText();
    }

    @Test
    @Order(2)
    @DisplayName("US1: 身分證字號重複應回傳錯誤")
    void shouldRejectDuplicateNationalId() throws Exception {
        // Given - 使用相同的身分證字號
        CreatePolicyHolderRequest request = createPolicyHolderRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/policyholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // User Story 2: 查詢保戶
    // ========================================

    @Test
    @Order(3)
    @DisplayName("US2: 應成功依 ID 查詢保戶")
    void shouldGetPolicyHolderById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders/{id}", createdPolicyHolderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(createdPolicyHolderId))
                .andExpect(jsonPath("$.data.nationalId").value("A123456789"))
                .andExpect(jsonPath("$.data.name").value("王小明"));
    }

    @Test
    @Order(4)
    @DisplayName("US2: 應成功依身分證字號查詢保戶")
    void shouldGetPolicyHolderByNationalId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders/national-id/{nationalId}", "A123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(createdPolicyHolderId))
                .andExpect(jsonPath("$.data.nationalId").value("A123456789"));
    }

    @Test
    @Order(5)
    @DisplayName("US2: 應成功搜尋保戶（姓名模糊搜尋）")
    void shouldSearchPolicyHoldersByName() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders")
                        .param("name", "王"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value(containsString("王")));
    }

    @Test
    @Order(6)
    @DisplayName("US2: 應成功取得分頁結果")
    void shouldGetPaginatedResults() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10));
    }

    // ========================================
    // User Story 3: 修改保戶
    // ========================================

    @Test
    @Order(7)
    @DisplayName("US3: 應成功更新保戶聯絡資訊")
    void shouldUpdatePolicyHolder() throws Exception {
        // Given
        UpdatePolicyHolderRequest request = createUpdateRequest();

        // When & Then
        mockMvc.perform(put("/api/v1/policyholders/{id}", createdPolicyHolderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.mobilePhone").value("0987654321"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.address.city").value("新北市"))
                // 身分證字號不可修改
                .andExpect(jsonPath("$.data.nationalId").value("A123456789"));
    }

    // ========================================
    // User Story 5: 新增保單
    // ========================================

    @Test
    @Order(8)
    @DisplayName("US5: 應成功為 ACTIVE 保戶新增保單")
    void shouldAddPolicyToActiveHolder() throws Exception {
        // Given
        AddPolicyRequest request = createAddPolicyRequest();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/policyholders/{id}/policies", createdPolicyHolderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(matchesPattern("^PO\\d{10}$")))
                .andExpect(jsonPath("$.data.policyType").value("LIFE"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andReturn();

        // 儲存保單 ID 供後續測試使用
        String response = result.getResponse().getContentAsString();
        createdPolicyId = objectMapper.readTree(response).path("data").path("id").asText();
    }

    // ========================================
    // User Story 6: 查詢保單
    // ========================================

    @Test
    @Order(9)
    @DisplayName("US6: 應成功查詢保戶的所有保單")
    void shouldGetPolicyHolderPolicies() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders/{id}/policies", createdPolicyHolderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(createdPolicyId))
                .andExpect(jsonPath("$.data[0].policyType").value("LIFE"));
    }

    @Test
    @Order(10)
    @DisplayName("US6: 應成功查詢單一保單")
    void shouldGetSinglePolicy() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders/{phId}/policies/{policyId}",
                        createdPolicyHolderId, createdPolicyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(createdPolicyId))
                .andExpect(jsonPath("$.data.policyType").value("LIFE"));
    }

    @Test
    @Order(11)
    @DisplayName("US6: 應支援保單類型篩選")
    void shouldFilterPoliciesByType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/policyholders/{id}/policies", createdPolicyHolderId)
                        .param("type", "LIFE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].policyType").value("LIFE"));
    }

    // ========================================
    // User Story 4: 刪除保戶
    // ========================================

    @Test
    @Order(12)
    @DisplayName("US4: 應成功軟刪除保戶")
    void shouldSoftDeletePolicyHolder() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/policyholders/{id}", createdPolicyHolderId))
                .andExpect(status().isNoContent());

        // 驗證狀態變為 INACTIVE
        mockMvc.perform(get("/api/v1/policyholders/{id}", createdPolicyHolderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @Order(13)
    @DisplayName("US4: 已刪除保戶不可再次刪除")
    void shouldNotDeleteAlreadyInactiveHolder() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/policyholders/{id}", createdPolicyHolderId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    @DisplayName("US5: INACTIVE 保戶不可新增保單")
    void shouldNotAddPolicyToInactiveHolder() throws Exception {
        // Given
        AddPolicyRequest request = createAddPolicyRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/policyholders/{id}/policies", createdPolicyHolderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // Error Handling Tests
    // ========================================

    @Test
    @Order(15)
    @DisplayName("不存在的保戶應回傳 404")
    void shouldReturn404ForNonExistentPolicyHolder() throws Exception {
        mockMvc.perform(get("/api/v1/policyholders/{id}", "PH9999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("POLICY_HOLDER_NOT_FOUND"));
    }

    @Test
    @Order(16)
    @DisplayName("無效的身分證字號應回傳驗證錯誤")
    void shouldRejectInvalidNationalId() throws Exception {
        // Given
        CreatePolicyHolderRequest request = createPolicyHolderRequest();
        request.setNationalId("INVALID");

        // When & Then
        mockMvc.perform(post("/api/v1/policyholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // Helper Methods
    // ========================================

    private CreatePolicyHolderRequest createPolicyHolderRequest() {
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

    private UpdatePolicyHolderRequest createUpdateRequest() {
        AddressRequest address = new AddressRequest();
        address.setZipCode("220");
        address.setCity("新北市");
        address.setDistrict("板橋區");
        address.setStreet("新地址200號");

        UpdatePolicyHolderRequest request = new UpdatePolicyHolderRequest();
        request.setMobilePhone("0987654321");
        request.setEmail("updated@example.com");
        request.setAddress(address);

        return request;
    }

    private AddPolicyRequest createAddPolicyRequest() {
        AddPolicyRequest request = new AddPolicyRequest();
        request.setPolicyType("LIFE");
        request.setPremium(new BigDecimal("10000"));
        request.setSumInsured(new BigDecimal("1000000"));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusYears(1));
        return request;
    }
}
