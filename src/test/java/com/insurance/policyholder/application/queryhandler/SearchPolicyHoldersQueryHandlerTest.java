package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.output.PolicyHolderQueryRepository;
import com.insurance.policyholder.application.query.SearchPolicyHoldersQuery;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.application.readmodel.PagedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchPolicyHoldersQueryHandler Tests")
class SearchPolicyHoldersQueryHandlerTest {

    @Mock
    private PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository;

    private SearchPolicyHoldersQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SearchPolicyHoldersQueryHandler(queryRepository);
    }

    private PolicyHolderReadModel createMockReadModel(String id, String name) {
        return new PolicyHolderReadModel(
                id,
                "A123456789",
                name,
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
    @DisplayName("姓名模糊搜尋")
    class SearchByNameTests {

        @Test
        @DisplayName("應成功搜尋符合姓名的保戶")
        void shouldReturnMatchingPolicyHolders() {
            // Given
            SearchPolicyHoldersQuery query = SearchPolicyHoldersQuery.byName("王", 0, 10);
            List<PolicyHolderReadModel> mockResults = Arrays.asList(
                    createMockReadModel("PH0000000001", "王小明"),
                    createMockReadModel("PH0000000002", "王大華")
            );

            when(queryRepository.searchByName(eq("王"), eq(0), eq(10))).thenReturn(mockResults);
            when(queryRepository.countByName(eq("王"))).thenReturn(2L);

            // When
            PagedResult<PolicyHolderReadModel> result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertEquals(2, result.getTotalElements());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
        }

        @Test
        @DisplayName("無符合結果時應回傳空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            // Given
            SearchPolicyHoldersQuery query = SearchPolicyHoldersQuery.byName("不存在", 0, 10);

            when(queryRepository.searchByName(eq("不存在"), eq(0), eq(10))).thenReturn(Collections.emptyList());
            when(queryRepository.countByName(eq("不存在"))).thenReturn(0L);

            // When
            PagedResult<PolicyHolderReadModel> result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("分頁功能")
    class PaginationTests {

        @Test
        @DisplayName("應正確計算分頁資訊")
        void shouldCalculatePaginationCorrectly() {
            // Given
            SearchPolicyHoldersQuery query = SearchPolicyHoldersQuery.byName("王", 1, 5);
            List<PolicyHolderReadModel> mockResults = Arrays.asList(
                    createMockReadModel("PH0000000006", "王六"),
                    createMockReadModel("PH0000000007", "王七")
            );

            when(queryRepository.searchByName(eq("王"), eq(1), eq(5))).thenReturn(mockResults);
            when(queryRepository.countByName(eq("王"))).thenReturn(12L);

            // When
            PagedResult<PolicyHolderReadModel> result = handler.handle(query);

            // Then
            assertEquals(2, result.getContent().size());
            assertEquals(12, result.getTotalElements());
            assertEquals(3, result.getTotalPages()); // 12 / 5 = 2.4 -> 3
            assertEquals(1, result.getPage());
            assertEquals(5, result.getSize());
            assertFalse(result.isFirst());
            assertFalse(result.isLast());
        }

        @Test
        @DisplayName("第一頁應標記為 isFirst")
        void shouldMarkFirstPage() {
            // Given
            SearchPolicyHoldersQuery query = SearchPolicyHoldersQuery.byName("王", 0, 10);

            when(queryRepository.searchByName(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
            when(queryRepository.countByName(anyString())).thenReturn(5L);

            // When
            PagedResult<PolicyHolderReadModel> result = handler.handle(query);

            // Then
            assertTrue(result.isFirst());
        }

        @Test
        @DisplayName("最後一頁應標記為 isLast")
        void shouldMarkLastPage() {
            // Given
            SearchPolicyHoldersQuery query = SearchPolicyHoldersQuery.byName("王", 2, 5);

            when(queryRepository.searchByName(anyString(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
            when(queryRepository.countByName(anyString())).thenReturn(12L); // 3 pages total

            // When
            PagedResult<PolicyHolderReadModel> result = handler.handle(query);

            // Then
            assertTrue(result.isLast());
        }
    }
}
