package com.insurance.policyholder.infrastructure.adapter.input.rest;

import com.insurance.policyholder.application.commandhandler.AddPolicyCommandHandler;
import com.insurance.policyholder.application.commandhandler.CreatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.DeletePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.UpdatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderPoliciesQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyQueryHandler;
import com.insurance.policyholder.application.queryhandler.SearchPolicyHoldersQueryHandler;
import com.insurance.policyholder.application.readmodel.PagedResult;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyHolderController.class)
@Import(PolicyHolderRestMapper.class)
@DisplayName("PolicyHolderController Query Tests")
class PolicyHolderControllerQueryTest {

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
    @DisplayName("GET /api/v1/policyholders/{id} - 查詢單一保戶")
    class GetPolicyHolderByIdTests {

        @Test
        @DisplayName("應成功查詢保戶並回傳 200")
        void shouldReturnPolicyHolderSuccessfully() throws Exception {
            // Given
            PolicyHolderReadModel mockResult = createMockReadModel();
            when(getPolicyHolderQueryHandler.handle(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH0000000001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value("PH0000000001"))
                    .andExpect(jsonPath("$.data.nationalId").value("A123456789"))
                    .andExpect(jsonPath("$.data.name").value("王小明"));
        }

        @Test
        @DisplayName("保戶不存在時應回傳 404")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(getPolicyHolderQueryHandler.handle(any()))
                    .thenThrow(new PolicyHolderNotFoundException("PH9999999999"));

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/PH9999999999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/policyholders - 搜尋保戶")
    class SearchPolicyHoldersTests {

        @Test
        @DisplayName("應成功搜尋保戶並回傳分頁結果")
        void shouldReturnPagedResults() throws Exception {
            // Given
            PolicyHolderReadModel mockReadModel = createMockReadModel();
            PagedResult<PolicyHolderReadModel> pagedResult = new PagedResult<>(
                    Arrays.asList(mockReadModel),
                    0, 10, 1L
            );

            when(searchPolicyHoldersQueryHandler.handle(any())).thenReturn(pagedResult);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders")
                            .param("name", "王")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].id").value("PH0000000001"))
                    .andExpect(jsonPath("$.data.content[0].name").value("王小明"))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(10));
        }

        @Test
        @DisplayName("無符合結果時應回傳空列表")
        void shouldReturnEmptyListWhenNoMatch() throws Exception {
            // Given
            PagedResult<PolicyHolderReadModel> emptyResult = new PagedResult<>(
                    Collections.emptyList(),
                    0, 10, 0L
            );

            when(searchPolicyHoldersQueryHandler.handle(any())).thenReturn(emptyResult);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders")
                            .param("name", "不存在")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("預設分頁參數應為 page=0, size=20")
        void shouldUseDefaultPaginationParams() throws Exception {
            // Given
            PagedResult<PolicyHolderReadModel> pagedResult = new PagedResult<>(
                    Collections.emptyList(),
                    0, 20, 0L
            );

            when(searchPolicyHoldersQueryHandler.handle(any())).thenReturn(pagedResult);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/policyholders/national-id/{nationalId} - 根據身分證字號查詢")
    class GetByNationalIdTests {

        @Test
        @DisplayName("應成功根據身分證字號查詢保戶")
        void shouldReturnPolicyHolderByNationalId() throws Exception {
            // Given
            PolicyHolderReadModel mockResult = createMockReadModel();
            when(getPolicyHolderQueryHandler.handleByNationalId(any())).thenReturn(mockResult);

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/national-id/A123456789"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.nationalId").value("A123456789"));
        }

        @Test
        @DisplayName("身分證字號不存在時應回傳 404")
        void shouldReturn404WhenNationalIdNotFound() throws Exception {
            // Given
            when(getPolicyHolderQueryHandler.handleByNationalId(any()))
                    .thenThrow(new PolicyHolderNotFoundException("National ID not found: Z999999999"));

            // When & Then
            mockMvc.perform(get("/api/v1/policyholders/national-id/Z999999999"))
                    .andExpect(status().isNotFound());
        }
    }
}
