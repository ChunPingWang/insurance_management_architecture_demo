package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.AddPolicyCommand;
import com.insurance.policyholder.application.port.output.DomainEventPublisher;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddPolicyCommandHandler Tests")
class AddPolicyCommandHandlerTest {

    @Mock
    private PolicyHolderRepository repository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private AddPolicyCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AddPolicyCommandHandler(repository, domainEventPublisher);
    }

    private PolicyHolder createActivePolicyHolder() {
        return PolicyHolder.create(
                PolicyHolderId.of("PH0000000001"),
                NationalId.of("A123456789"),
                PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("100", "台北市", "中正區", "忠孝東路100號")
        );
    }

    private PolicyHolder createInactivePolicyHolder() {
        return PolicyHolder.reconstitute(
                PolicyHolderId.of("PH0000000001"),
                NationalId.of("A123456789"),
                PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "test@example.com"),
                Address.of("100", "台北市", "中正區", "忠孝東路100號"),
                PolicyHolderStatus.INACTIVE,
                1L
        );
    }

    @Nested
    @DisplayName("新增保單")
    class AddPolicyTests {

        @Test
        @DisplayName("應成功為 ACTIVE 保戶新增保單")
        void shouldAddPolicyToActivePolicyHolder() {
            // Given
            PolicyHolder policyHolder = createActivePolicyHolder();
            AddPolicyCommand command = new AddPolicyCommand(
                    "PH0000000001",
                    "LIFE",
                    new BigDecimal("10000"),
                    new BigDecimal("1000000"),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyReadModel result = handler.handle(command);

            // Then
            assertNotNull(result);
            assertNotNull(result.getId());
            assertTrue(result.getId().startsWith("PO"));
            assertEquals("LIFE", result.getPolicyType());
            assertEquals(new BigDecimal("10000"), result.getPremium());
            assertEquals(new BigDecimal("1000000"), result.getSumInsured());
            assertEquals("ACTIVE", result.getStatus());

            // Verify policy was added
            ArgumentCaptor<PolicyHolder> captor = ArgumentCaptor.forClass(PolicyHolder.class);
            verify(repository).save(captor.capture());
            assertEquals(1, captor.getValue().getPolicies().size());
        }

        @Test
        @DisplayName("保單應包含正確的開始與結束日期")
        void shouldHaveCorrectDates() {
            // Given
            PolicyHolder policyHolder = createActivePolicyHolder();
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            AddPolicyCommand command = new AddPolicyCommand(
                    "PH0000000001",
                    "HEALTH",
                    new BigDecimal("5000"),
                    new BigDecimal("500000"),
                    startDate,
                    endDate
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(policyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyReadModel result = handler.handle(command);

            // Then
            assertEquals(startDate, result.getStartDate());
            assertEquals(endDate, result.getEndDate());
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在時應拋出例外")
        void shouldThrowExceptionWhenPolicyHolderNotFound() {
            // Given
            AddPolicyCommand command = new AddPolicyCommand(
                    "PH9999999999",
                    "LIFE",
                    new BigDecimal("10000"),
                    new BigDecimal("1000000"),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handle(command));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("INACTIVE 保戶不能新增保單")
        void shouldThrowExceptionWhenPolicyHolderIsInactive() {
            // Given
            PolicyHolder inactivePolicyHolder = createInactivePolicyHolder();
            AddPolicyCommand command = new AddPolicyCommand(
                    "PH0000000001",
                    "LIFE",
                    new BigDecimal("10000"),
                    new BigDecimal("1000000"),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(inactivePolicyHolder));

            // When & Then
            assertThrows(IllegalStateException.class, () -> handler.handle(command));
            verify(repository, never()).save(any());
        }
    }
}
