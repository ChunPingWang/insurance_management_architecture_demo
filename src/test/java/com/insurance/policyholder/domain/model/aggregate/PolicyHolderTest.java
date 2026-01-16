package com.insurance.policyholder.domain.model.aggregate;

import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolder Aggregate Root Tests")
class PolicyHolderTest {

    private NationalId validNationalId;
    private PersonalInfo validPersonalInfo;
    private ContactInfo validContactInfo;
    private Address validAddress;

    @BeforeEach
    void setUp() {
        validNationalId = NationalId.of("A123456789");
        validPersonalInfo = PersonalInfo.of(
                "王小明",
                Gender.MALE,
                LocalDate.of(1990, 1, 15)
        );
        validContactInfo = ContactInfo.of("0912345678", "test@example.com");
        validAddress = Address.of("100", "台北市", "中正區", "忠孝東路100號");
    }

    @Nested
    @DisplayName("建立保戶測試")
    class CreateTests {

        @Test
        @DisplayName("應成功建立新保戶")
        void shouldCreatePolicyHolderSuccessfully() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            assertNotNull(policyHolder);
            assertNotNull(policyHolder.getId());
            assertTrue(policyHolder.getId().getValue().startsWith("PH"));
            assertEquals(validNationalId, policyHolder.getNationalId());
            assertEquals(validPersonalInfo, policyHolder.getPersonalInfo());
            assertEquals(validContactInfo, policyHolder.getContactInfo());
            assertEquals(validAddress, policyHolder.getAddress());
            assertEquals(PolicyHolderStatus.ACTIVE, policyHolder.getStatus());
        }

        @Test
        @DisplayName("新建保戶應為 ACTIVE 狀態")
        void shouldBeActiveStatus() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            assertEquals(PolicyHolderStatus.ACTIVE, policyHolder.getStatus());
        }

        @Test
        @DisplayName("新建保戶的保單清單應為空")
        void shouldHaveEmptyPolicies() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            assertTrue(policyHolder.getPolicies().isEmpty());
        }

        @Test
        @DisplayName("新建保戶應產生唯一的保戶編號")
        void shouldGenerateUniquePolicyHolderId() {
            PolicyHolder policyHolder1 = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            // 等待一毫秒以確保時間戳記不同
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            PolicyHolder policyHolder2 = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            assertNotEquals(policyHolder1.getId(), policyHolder2.getId());
        }
    }

    @Nested
    @DisplayName("重建保戶測試")
    class ReconstituteTests {

        @Test
        @DisplayName("應成功重建保戶")
        void shouldReconstitutePolicyHolder() {
            PolicyHolderId id = PolicyHolderId.generate();
            PolicyHolder policyHolder = PolicyHolder.reconstitute(
                    id,
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress,
                    PolicyHolderStatus.ACTIVE,
                    1L
            );

            assertEquals(id, policyHolder.getId());
            assertEquals(1L, policyHolder.getVersion());
        }
    }

    @Nested
    @DisplayName("更新聯絡資訊測試")
    class UpdateContactInfoTests {

        @Test
        @DisplayName("應成功更新聯絡資訊")
        void shouldUpdateContactInfo() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            ContactInfo newContactInfo = ContactInfo.of("0987654321", "new@example.com");
            policyHolder.updateContactInfo(newContactInfo);

            assertEquals(newContactInfo, policyHolder.getContactInfo());
        }
    }

    @Nested
    @DisplayName("更新地址測試")
    class UpdateAddressTests {

        @Test
        @DisplayName("應成功更新地址")
        void shouldUpdateAddress() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            Address newAddress = Address.of("200", "新北市", "板橋區", "文化路200號");
            policyHolder.updateAddress(newAddress);

            assertEquals(newAddress, policyHolder.getAddress());
        }
    }

    @Nested
    @DisplayName("停用保戶測試")
    class DeactivateTests {

        @Test
        @DisplayName("應成功停用保戶")
        void shouldDeactivatePolicyHolder() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            policyHolder.deactivate();

            assertEquals(PolicyHolderStatus.INACTIVE, policyHolder.getStatus());
        }

        @Test
        @DisplayName("已停用的保戶不應再次停用")
        void shouldThrowExceptionWhenDeactivatingInactivePolicyHolder() {
            PolicyHolder policyHolder = PolicyHolder.create(
                    validNationalId,
                    validPersonalInfo,
                    validContactInfo,
                    validAddress
            );

            policyHolder.deactivate();

            assertThrows(IllegalStateException.class, policyHolder::deactivate);
        }
    }
}
