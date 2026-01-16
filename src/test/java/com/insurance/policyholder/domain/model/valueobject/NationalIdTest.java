package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NationalId Value Object Tests")
class NationalIdTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的台灣身分證字號")
        void shouldCreateValidNationalId() {
            NationalId id = NationalId.of("A123456789");
            assertNotNull(id);
            assertEquals("A123456789", id.getValue());
        }

        @ParameterizedTest
        @ValueSource(strings = {"A123456789", "F131104093"})
        @DisplayName("應接受有效的身分證字號格式")
        void shouldAcceptValidNationalIds(String nationalId) {
            assertDoesNotThrow(() -> NationalId.of(nationalId));
        }
    }

    @Nested
    @DisplayName("格式驗證測試")
    class FormatValidationTests {

        @Test
        @DisplayName("空值應拋出例外")
        void shouldThrowExceptionForNullValue() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of(null));
        }

        @Test
        @DisplayName("空字串應拋出例外")
        void shouldThrowExceptionForEmptyValue() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of(""));
        }

        @Test
        @DisplayName("長度不正確應拋出例外")
        void shouldThrowExceptionForInvalidLength() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("A12345678"));
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("A1234567890"));
        }

        @Test
        @DisplayName("首位非英文應拋出例外")
        void shouldThrowExceptionForNonLetterFirstChar() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("1234567890"));
        }

        @Test
        @DisplayName("第二位非1或2應拋出例外")
        void shouldThrowExceptionForInvalidSecondDigit() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("A323456789"));
        }

        @Test
        @DisplayName("包含非數字字元應拋出例外")
        void shouldThrowExceptionForNonDigits() {
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("A12345678X"));
        }
    }

    @Nested
    @DisplayName("檢查碼驗證測試")
    class ChecksumValidationTests {

        @Test
        @DisplayName("無效檢查碼應拋出例外")
        void shouldThrowExceptionForInvalidChecksum() {
            // A123456789 是有效的，A123456780 應該是無效的
            assertThrows(IllegalArgumentException.class, () -> NationalId.of("A123456780"));
        }
    }

    @Nested
    @DisplayName("遮罩功能測試")
    class MaskingTests {

        @Test
        @DisplayName("應正確遮罩身分證字號")
        void shouldMaskNationalId() {
            NationalId id = NationalId.of("A123456789");
            assertEquals("A123***789", id.getMasked());
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同值應相等")
        void shouldBeEqualForSameValue() {
            NationalId id1 = NationalId.of("A123456789");
            NationalId id2 = NationalId.of("A123456789");
            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("不同值應不相等")
        void shouldNotBeEqualForDifferentValue() {
            NationalId id1 = NationalId.of("A123456789");
            NationalId id2 = NationalId.of("F131104093");
            assertNotEquals(id1, id2);
        }
    }
}
