package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyId Value Object Tests")
class PolicyIdTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的 PolicyId")
        void shouldCreateValidPolicyId() {
            PolicyId id = PolicyId.of("PO0000000001");
            assertNotNull(id);
            assertEquals("PO0000000001", id.getValue());
        }

        @Test
        @DisplayName("應成功產生新的 PolicyId")
        void shouldGenerateNewPolicyId() {
            PolicyId id = PolicyId.generate();
            assertNotNull(id);
            assertTrue(id.getValue().startsWith("PO"));
            assertEquals(12, id.getValue().length());
        }

        @Test
        @DisplayName("應使用指定序號產生 PolicyId")
        void shouldGenerateIdWithSpecificSequence() {
            PolicyId id = PolicyId.generate(123L);
            assertEquals("PO0000000123", id.getValue());
        }

        @Test
        @DisplayName("序號為 0 應產生正確的 PolicyId")
        void shouldGenerateIdWithZeroSequence() {
            PolicyId id = PolicyId.generate(0L);
            assertEquals("PO0000000000", id.getValue());
        }

        @Test
        @DisplayName("大序號應產生正確格式的 PolicyId")
        void shouldGenerateIdWithLargeSequence() {
            PolicyId id = PolicyId.generate(9999999999L);
            assertEquals("PO9999999999", id.getValue());
        }
    }

    @Nested
    @DisplayName("驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("空值應拋出例外")
        void shouldThrowExceptionForNullValue() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of(null)
            );
            assertTrue(ex.getMessage().contains("cannot be null or empty"));
        }

        @Test
        @DisplayName("空字串應拋出例外")
        void shouldThrowExceptionForEmptyValue() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("")
            );
            assertTrue(ex.getMessage().contains("cannot be null or empty"));
        }

        @Test
        @DisplayName("不符合格式應拋出例外")
        void shouldThrowExceptionForInvalidFormat() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("INVALID")
            );
            assertTrue(ex.getMessage().contains("Invalid PolicyId format"));
        }

        @Test
        @DisplayName("缺少 PO 前綴應拋出例外")
        void shouldThrowExceptionForMissingPrefix() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("0000000001")
            );
        }

        @Test
        @DisplayName("錯誤前綴應拋出例外")
        void shouldThrowExceptionForWrongPrefix() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("PH0000000001")
            );
        }

        @Test
        @DisplayName("序號過短應拋出例外")
        void shouldThrowExceptionForShortSequence() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("PO00001")
            );
        }

        @Test
        @DisplayName("序號過長應拋出例外")
        void shouldThrowExceptionForLongSequence() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("PO00000000001")
            );
        }

        @Test
        @DisplayName("負序號應拋出例外")
        void shouldThrowExceptionForNegativeSequence() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.generate(-1L)
            );
            assertTrue(ex.getMessage().contains("non-negative"));
        }

        @Test
        @DisplayName("包含字母的序號應拋出例外")
        void shouldThrowExceptionForAlphabeticSequence() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> PolicyId.of("PO000000000A")
            );
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同值應相等")
        void shouldBeEqualForSameValue() {
            PolicyId id1 = PolicyId.of("PO0000000001");
            PolicyId id2 = PolicyId.of("PO0000000001");
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("不同值應不相等")
        void shouldNotBeEqualForDifferentValue() {
            PolicyId id1 = PolicyId.of("PO0000000001");
            PolicyId id2 = PolicyId.of("PO0000000002");
            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("與 null 比較應不相等")
        void shouldNotBeEqualToNull() {
            PolicyId id = PolicyId.of("PO0000000001");
            assertNotEquals(null, id);
        }

        @Test
        @DisplayName("與不同類型物件比較應不相等")
        void shouldNotBeEqualToDifferentType() {
            PolicyId id = PolicyId.of("PO0000000001");
            assertNotEquals("PO0000000001", id);
        }

        @Test
        @DisplayName("與自己比較應相等")
        void shouldBeEqualToItself() {
            PolicyId id = PolicyId.of("PO0000000001");
            assertEquals(id, id);
        }
    }

    @Nested
    @DisplayName("toString 測試")
    class ToStringTests {

        @Test
        @DisplayName("toString 應回傳值")
        void shouldReturnValueInToString() {
            PolicyId id = PolicyId.of("PO0000000001");
            assertEquals("PO0000000001", id.toString());
        }
    }
}
