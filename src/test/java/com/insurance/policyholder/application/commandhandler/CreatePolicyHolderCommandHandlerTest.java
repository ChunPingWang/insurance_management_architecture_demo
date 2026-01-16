package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.CreatePolicyHolderCommand;
import com.insurance.policyholder.application.port.output.DomainEventPublisher;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePolicyHolderCommandHandler Tests")
class CreatePolicyHolderCommandHandlerTest {

    @Mock
    private PolicyHolderRepository policyHolderRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private CreatePolicyHolderCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreatePolicyHolderCommandHandler(policyHolderRepository, domainEventPublisher);
    }

    private CreatePolicyHolderCommand createValidCommand() {
        return new CreatePolicyHolderCommand(
                "A123456789",
                "王小明",
                "MALE",
                LocalDate.of(1990, 1, 15),
                "0912345678",
                "test@example.com",
                "100",
                "台北市",
                "中正區",
                "忠孝東路100號"
        );
    }

    @Nested
    @DisplayName("成功建立保戶")
    class SuccessfulCreationTests {

        @Test
        @DisplayName("應成功建立保戶並返回 ReadModel")
        void shouldCreatePolicyHolderAndReturnReadModel() {
            // Given
            CreatePolicyHolderCommand command = createValidCommand();

            when(policyHolderRepository.existsByNationalId(any(NationalId.class))).thenReturn(false);
            when(policyHolderRepository.save(any(PolicyHolder.class))).thenAnswer(invocation -> {
                PolicyHolder saved = invocation.getArgument(0);
                return saved;
            });

            // When
            PolicyHolderReadModel result = handler.handle(command);

            // Then
            assertNotNull(result);
            assertEquals("A123456789", result.getNationalId());
            assertEquals("王小明", result.getName());
            assertEquals("MALE", result.getGender());
            assertEquals("ACTIVE", result.getStatus());

            verify(policyHolderRepository).existsByNationalId(any(NationalId.class));
            verify(policyHolderRepository).save(any(PolicyHolder.class));
        }

        @Test
        @DisplayName("應儲存正確的保戶資料")
        void shouldSaveCorrectPolicyHolderData() {
            // Given
            CreatePolicyHolderCommand command = createValidCommand();

            when(policyHolderRepository.existsByNationalId(any(NationalId.class))).thenReturn(false);
            when(policyHolderRepository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            handler.handle(command);

            // Then
            ArgumentCaptor<PolicyHolder> captor = ArgumentCaptor.forClass(PolicyHolder.class);
            verify(policyHolderRepository).save(captor.capture());

            PolicyHolder savedPolicyHolder = captor.getValue();
            assertEquals("A123456789", savedPolicyHolder.getNationalId().getValue());
            assertEquals("王小明", savedPolicyHolder.getPersonalInfo().getName());
            assertEquals(Gender.MALE, savedPolicyHolder.getPersonalInfo().getGender());
            assertEquals(LocalDate.of(1990, 1, 15), savedPolicyHolder.getPersonalInfo().getBirthDate());
            assertEquals("0912345678", savedPolicyHolder.getContactInfo().getMobilePhone());
            assertEquals("test@example.com", savedPolicyHolder.getContactInfo().getEmail());
            assertEquals("100", savedPolicyHolder.getAddress().getZipCode());
            assertEquals("台北市", savedPolicyHolder.getAddress().getCity());
            assertEquals("中正區", savedPolicyHolder.getAddress().getDistrict());
            assertEquals("忠孝東路100號", savedPolicyHolder.getAddress().getStreet());
            assertEquals(PolicyHolderStatus.ACTIVE, savedPolicyHolder.getStatus());
        }

        @Test
        @DisplayName("返回的 ReadModel 應包含正確的保戶編號格式")
        void shouldReturnReadModelWithCorrectPolicyHolderIdFormat() {
            // Given
            CreatePolicyHolderCommand command = createValidCommand();

            when(policyHolderRepository.existsByNationalId(any(NationalId.class))).thenReturn(false);
            when(policyHolderRepository.save(any(PolicyHolder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PolicyHolderReadModel result = handler.handle(command);

            // Then
            assertNotNull(result.getId());
            assertTrue(result.getId().startsWith("PH"));
            assertEquals(12, result.getId().length()); // PH + 10 digits = 12 characters
        }
    }

    @Nested
    @DisplayName("驗證失敗")
    class ValidationFailureTests {

        @Test
        @DisplayName("身分證字號已存在時應拋出例外")
        void shouldThrowExceptionWhenNationalIdExists() {
            // Given
            CreatePolicyHolderCommand command = createValidCommand();

            when(policyHolderRepository.existsByNationalId(any(NationalId.class))).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> handler.handle(command)
            );

            assertTrue(exception.getMessage().contains("already exists") ||
                       exception.getMessage().contains("已存在"));

            verify(policyHolderRepository).existsByNationalId(any(NationalId.class));
            verify(policyHolderRepository, never()).save(any(PolicyHolder.class));
        }
    }
}
