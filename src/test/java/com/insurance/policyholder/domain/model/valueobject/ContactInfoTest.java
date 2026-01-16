package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContactInfo Value Object Tests")
class ContactInfoTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的 ContactInfo（含 Email）")
        void shouldCreateValidContactInfoWithEmail() {
            ContactInfo info = ContactInfo.of("0912345678", "wang@example.com");
            assertNotNull(info);
            assertEquals("0912345678", info.getMobilePhone());
            assertEquals("wang@example.com", info.getEmail());
        }

        @Test
        @DisplayName("應成功建立有效的 ContactInfo（不含 Email）")
        void shouldCreateValidContactInfoWithoutEmail() {
            ContactInfo info = ContactInfo.of("0912345678", null);
            assertNotNull(info);
            assertEquals("0912345678", info.getMobilePhone());
            assertNull(info.getEmail());
        }

        @Test
        @DisplayName("Email 空字串應視為 null")
        void shouldTreatEmptyEmailAsNull() {
            ContactInfo info = ContactInfo.of("0912345678", "");
            assertNull(info.getEmail());
        }
    }

    @Nested
    @DisplayName("手機號碼驗證測試")
    class MobilePhoneValidationTests {

        @Test
        @DisplayName("手機號碼為空應拋出例外")
        void shouldThrowExceptionForNullMobilePhone() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of(null, "wang@example.com"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0912345678", "0922333444", "0988123456"})
        @DisplayName("應接受有效的台灣手機號碼格式")
        void shouldAcceptValidMobilePhones(String phone) {
            assertDoesNotThrow(() -> ContactInfo.of(phone, null));
        }

        @Test
        @DisplayName("非 09 開頭應拋出例外")
        void shouldThrowExceptionForNon09Prefix() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("0812345678", null));
        }

        @Test
        @DisplayName("長度不正確應拋出例外")
        void shouldThrowExceptionForInvalidLength() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("091234567", null)); // 9 digits
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("09123456789", null)); // 11 digits
        }

        @Test
        @DisplayName("包含非數字應拋出例外")
        void shouldThrowExceptionForNonDigits() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("091234567a", null));
        }
    }

    @Nested
    @DisplayName("Email 驗證測試")
    class EmailValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"test@example.com", "user.name@domain.org", "a@b.co"})
        @DisplayName("應接受有效的 Email 格式")
        void shouldAcceptValidEmails(String email) {
            assertDoesNotThrow(() -> ContactInfo.of("0912345678", email));
        }

        @Test
        @DisplayName("無效 Email 格式應拋出例外")
        void shouldThrowExceptionForInvalidEmail() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("0912345678", "invalid-email"));
        }

        @Test
        @DisplayName("Email 缺少 @ 應拋出例外")
        void shouldThrowExceptionForEmailWithoutAt() {
            assertThrows(IllegalArgumentException.class,
                    () -> ContactInfo.of("0912345678", "invalidexample.com"));
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同內容應相等")
        void shouldBeEqualForSameContent() {
            ContactInfo info1 = ContactInfo.of("0912345678", "wang@example.com");
            ContactInfo info2 = ContactInfo.of("0912345678", "wang@example.com");
            assertEquals(info1, info2);
            assertEquals(info1.hashCode(), info2.hashCode());
        }

        @Test
        @DisplayName("不同內容應不相等")
        void shouldNotBeEqualForDifferentContent() {
            ContactInfo info1 = ContactInfo.of("0912345678", "wang@example.com");
            ContactInfo info2 = ContactInfo.of("0922333444", "lee@example.com");
            assertNotEquals(info1, info2);
        }
    }
}
