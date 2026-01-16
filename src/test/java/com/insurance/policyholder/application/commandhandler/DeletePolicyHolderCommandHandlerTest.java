package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.DeletePolicyHolderCommand;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePolicyHolderCommandHandler Tests")
class DeletePolicyHolderCommandHandlerTest {

    @Mock
    private PolicyHolderRepository repository;

    private DeletePolicyHolderCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DeletePolicyHolderCommandHandler(repository);
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

    @Nested
    @DisplayName("刪除保戶")
    class DeletePolicyHolderTests {

        @Test
        @DisplayName("應成功軟刪除保戶（狀態變為 INACTIVE）")
        void shouldSoftDeletePolicyHolderSuccessfully() {
            // Given
            PolicyHolder existingPolicyHolder = createActivePolicyHolder();
            DeletePolicyHolderCommand command = new DeletePolicyHolderCommand("PH0000000001");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(existingPolicyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            handler.handle(command);

            // Then
            ArgumentCaptor<PolicyHolder> captor = ArgumentCaptor.forClass(PolicyHolder.class);
            verify(repository).save(captor.capture());
            PolicyHolder savedPolicyHolder = captor.getValue();
            assertEquals(PolicyHolderStatus.INACTIVE, savedPolicyHolder.getStatus());
        }

        @Test
        @DisplayName("刪除後保戶資料應保留")
        void shouldPreservePolicyHolderDataAfterDeletion() {
            // Given
            PolicyHolder existingPolicyHolder = createActivePolicyHolder();
            DeletePolicyHolderCommand command = new DeletePolicyHolderCommand("PH0000000001");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(existingPolicyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            handler.handle(command);

            // Then
            ArgumentCaptor<PolicyHolder> captor = ArgumentCaptor.forClass(PolicyHolder.class);
            verify(repository).save(captor.capture());
            PolicyHolder savedPolicyHolder = captor.getValue();

            // Data should be preserved
            assertEquals("PH0000000001", savedPolicyHolder.getId().getValue());
            assertEquals("A123456789", savedPolicyHolder.getNationalId().getValue());
            assertEquals("王小明", savedPolicyHolder.getPersonalInfo().getName());
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在時應拋出例外")
        void shouldThrowExceptionWhenPolicyHolderNotFound() {
            // Given
            DeletePolicyHolderCommand command = new DeletePolicyHolderCommand("PH9999999999");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handle(command));
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("已刪除的保戶再次刪除應拋出例外")
        void shouldThrowExceptionWhenDeletingAlreadyInactivePolicyHolder() {
            // Given
            PolicyHolder inactivePolicyHolder = PolicyHolder.reconstitute(
                    PolicyHolderId.of("PH0000000001"),
                    NationalId.of("A123456789"),
                    PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15)),
                    ContactInfo.of("0912345678", "test@example.com"),
                    Address.of("100", "台北市", "中正區", "忠孝東路100號"),
                    PolicyHolderStatus.INACTIVE,
                    1L
            );
            DeletePolicyHolderCommand command = new DeletePolicyHolderCommand("PH0000000001");

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(inactivePolicyHolder));

            // When & Then
            assertThrows(IllegalStateException.class, () -> handler.handle(command));
            verify(repository, never()).save(any());
        }
    }
}
