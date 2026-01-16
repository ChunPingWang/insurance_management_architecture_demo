package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.output.PolicyHolderQueryRepository;
import com.insurance.policyholder.application.query.GetPolicyHolderByNationalIdQuery;
import com.insurance.policyholder.application.query.GetPolicyHolderQuery;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPolicyHolderQueryHandler Tests")
class GetPolicyHolderQueryHandlerTest {

    @Mock
    private PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository;

    private GetPolicyHolderQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetPolicyHolderQueryHandler(queryRepository);
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
    @DisplayName("根據保戶編號查詢")
    class GetByIdTests {

        @Test
        @DisplayName("應成功查詢存在的保戶")
        void shouldReturnPolicyHolderWhenExists() {
            // Given
            GetPolicyHolderQuery query = new GetPolicyHolderQuery("PH0000000001");
            PolicyHolderReadModel mockReadModel = createMockReadModel();

            when(queryRepository.findById(any())).thenReturn(Optional.of(mockReadModel));

            // When
            PolicyHolderReadModel result = handler.handle(query);

            // Then
            assertNotNull(result);
            assertEquals("PH0000000001", result.getId());
            assertEquals("A123456789", result.getNationalId());
            assertEquals("王小明", result.getName());
        }

        @Test
        @DisplayName("保戶不存在時應拋出例外")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            GetPolicyHolderQuery query = new GetPolicyHolderQuery("PH9999999999");

            when(queryRepository.findById(any())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handle(query));
        }
    }

    @Nested
    @DisplayName("根據身分證字號查詢")
    class GetByNationalIdTests {

        @Test
        @DisplayName("應成功根據身分證字號查詢保戶")
        void shouldReturnPolicyHolderByNationalId() {
            // Given
            GetPolicyHolderByNationalIdQuery query = new GetPolicyHolderByNationalIdQuery("A123456789");
            PolicyHolderReadModel mockReadModel = createMockReadModel();

            when(queryRepository.findByNationalId(any())).thenReturn(Optional.of(mockReadModel));

            // When
            PolicyHolderReadModel result = handler.handleByNationalId(query);

            // Then
            assertNotNull(result);
            assertEquals("A123456789", result.getNationalId());
        }

        @Test
        @DisplayName("身分證字號不存在時應拋出例外")
        void shouldThrowExceptionWhenNationalIdNotFound() {
            // Given - use a valid national ID format (B=11, checksum passes)
            GetPolicyHolderByNationalIdQuery query = new GetPolicyHolderByNationalIdQuery("B123456780");

            when(queryRepository.findByNationalId(any())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handleByNationalId(query));
        }
    }
}
