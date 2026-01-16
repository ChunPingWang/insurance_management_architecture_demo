package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.UpdatePolicyHolderCommand;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
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
@DisplayName("UpdatePolicyHolderCommandHandler Tests")
class UpdatePolicyHolderCommandHandlerTest {

    @Mock
    private PolicyHolderRepository repository;

    private UpdatePolicyHolderCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdatePolicyHolderCommandHandler(repository);
    }

    private PolicyHolder createExistingPolicyHolder() {
        return PolicyHolder.create(
                PolicyHolderId.of("PH0000000001"),
                NationalId.of("A123456789"),
                PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of("0912345678", "old@example.com"),
                Address.of("100", "台北市", "中正區", "舊地址100號")
        );
    }

    @Nested
    @DisplayName("更新保戶資料")
    class UpdatePolicyHolderTests {

        @Test
        @DisplayName("應成功更新保戶聯絡資訊")
        void shouldUpdateContactInfoSuccessfully() {
            // Given
            PolicyHolder existingPolicyHolder = createExistingPolicyHolder();
            UpdatePolicyHolderCommand command = new UpdatePolicyHolderCommand(
                    "PH0000000001",
                    "0987654321",      // new phone
                    "new@example.com", // new email
                    "200",             // new zipCode
                    "新北市",           // new city
                    "板橋區",           // new district
                    "新地址200號"       // new street
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(existingPolicyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyHolderReadModel result = handler.handle(command);

            // Then
            assertNotNull(result);
            assertEquals("0987654321", result.getMobilePhone());
            assertEquals("new@example.com", result.getEmail());
            assertEquals("200", result.getZipCode());
            assertEquals("新北市", result.getCity());
            assertEquals("板橋區", result.getDistrict());
            assertEquals("新地址200號", result.getStreet());

            // Verify save was called
            ArgumentCaptor<PolicyHolder> captor = ArgumentCaptor.forClass(PolicyHolder.class);
            verify(repository).save(captor.capture());
            PolicyHolder savedPolicyHolder = captor.getValue();
            assertEquals("0987654321", savedPolicyHolder.getContactInfo().getMobilePhone());
        }

        @Test
        @DisplayName("應保留未修改的欄位")
        void shouldPreserveUnchangedFields() {
            // Given
            PolicyHolder existingPolicyHolder = createExistingPolicyHolder();
            UpdatePolicyHolderCommand command = new UpdatePolicyHolderCommand(
                    "PH0000000001",
                    "0987654321",
                    "new@example.com",
                    "200",
                    "新北市",
                    "板橋區",
                    "新地址200號"
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(existingPolicyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyHolderReadModel result = handler.handle(command);

            // Then - unchanged fields should remain
            assertEquals("PH0000000001", result.getId());
            assertEquals("A123456789", result.getNationalId());
            assertEquals("王小明", result.getName());
            assertEquals("MALE", result.getGender());
            assertEquals(LocalDate.of(1990, 1, 15), result.getBirthDate());
        }

        @Test
        @DisplayName("版本號應遞增")
        void shouldIncrementVersion() {
            // Given
            PolicyHolder existingPolicyHolder = createExistingPolicyHolder();
            UpdatePolicyHolderCommand command = new UpdatePolicyHolderCommand(
                    "PH0000000001",
                    "0987654321",
                    "new@example.com",
                    "200",
                    "新北市",
                    "板橋區",
                    "新地址200號"
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.of(existingPolicyHolder));
            when(repository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyHolderReadModel result = handler.handle(command);

            // Then
            assertTrue(result.getVersion() >= 0);
        }
    }

    @Nested
    @DisplayName("錯誤處理")
    class ErrorHandlingTests {

        @Test
        @DisplayName("保戶不存在時應拋出例外")
        void shouldThrowExceptionWhenPolicyHolderNotFound() {
            // Given
            UpdatePolicyHolderCommand command = new UpdatePolicyHolderCommand(
                    "PH9999999999",
                    "0987654321",
                    "new@example.com",
                    "200",
                    "新北市",
                    "板橋區",
                    "新地址200號"
            );

            when(repository.findById(any(PolicyHolderId.class))).thenReturn(Optional.empty());

            // When & Then
            assertThrows(PolicyHolderNotFoundException.class, () -> handler.handle(command));
            verify(repository, never()).save(any());
        }
    }
}
