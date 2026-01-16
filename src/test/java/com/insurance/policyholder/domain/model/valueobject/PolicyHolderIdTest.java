package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolderId Value Object Tests")
class PolicyHolderIdTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的 PolicyHolderId")
        void shouldCreateValidPolicyHolderId() {
            PolicyHolderId id = PolicyHolderId.of("PH0000000001");
            assertNotNull(id);
            assertEquals("PH0000000001", id.getValue());
        }

        @Test
        @DisplayName("應成功產生新的 PolicyHolderId")
        void shouldGenerateNewPolicyHolderId() {
            PolicyHolderId id = PolicyHolderId.generate(1L);
            assertNotNull(id);
            assertTrue(id.getValue().startsWith("PH"));
            assertEquals(12, id.getValue().length());
        }

        @Test
        @DisplayName("產生的 ID 應包含正確格式的序號")
        void shouldGenerateIdWithCorrectSequence() {
            PolicyHolderId id = PolicyHolderId.generate(123L);
            assertEquals("PH0000000123", id.getValue());
        }
    }

    @Nested
    @DisplayName("驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("空值應拋出例外")
        void shouldThrowExceptionForNullValue() {
            assertThrows(IllegalArgumentException.class, () -> PolicyHolderId.of(null));
        }

        @Test
        @DisplayName("空字串應拋出例外")
        void shouldThrowExceptionForEmptyValue() {
            assertThrows(IllegalArgumentException.class, () -> PolicyHolderId.of(""));
        }

        @Test
        @DisplayName("不符合格式應拋出例外")
        void shouldThrowExceptionForInvalidFormat() {
            assertThrows(IllegalArgumentException.class, () -> PolicyHolderId.of("INVALID"));
        }

        @Test
        @DisplayName("缺少 PH 前綴應拋出例外")
        void shouldThrowExceptionForMissingPrefix() {
            assertThrows(IllegalArgumentException.class, () -> PolicyHolderId.of("0000000001"));
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同值應相等")
        void shouldBeEqualForSameValue() {
            PolicyHolderId id1 = PolicyHolderId.of("PH0000000001");
            PolicyHolderId id2 = PolicyHolderId.of("PH0000000001");
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("不同值應不相等")
        void shouldNotBeEqualForDifferentValue() {
            PolicyHolderId id1 = PolicyHolderId.of("PH0000000001");
            PolicyHolderId id2 = PolicyHolderId.of("PH0000000002");
            assertNotEquals(id1, id2);
        }
    }
}
