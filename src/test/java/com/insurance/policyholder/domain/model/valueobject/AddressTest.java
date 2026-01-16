package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Value Object Tests")
class AddressTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的地址")
        void shouldCreateValidAddress() {
            Address address = Address.of("100", "台北市", "中正區", "忠孝東路一段1號");
            assertNotNull(address);
            assertEquals("100", address.getZipCode());
            assertEquals("台北市", address.getCity());
            assertEquals("中正區", address.getDistrict());
            assertEquals("忠孝東路一段1號", address.getStreet());
        }
    }

    @Nested
    @DisplayName("驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("郵遞區號為空應拋出例外")
        void shouldThrowExceptionForNullZipCode() {
            assertThrows(IllegalArgumentException.class,
                    () -> Address.of(null, "台北市", "中正區", "忠孝東路一段1號"));
        }

        @Test
        @DisplayName("縣市為空應拋出例外")
        void shouldThrowExceptionForNullCity() {
            assertThrows(IllegalArgumentException.class,
                    () -> Address.of("100", null, "中正區", "忠孝東路一段1號"));
        }

        @Test
        @DisplayName("區域為空應拋出例外")
        void shouldThrowExceptionForNullDistrict() {
            assertThrows(IllegalArgumentException.class,
                    () -> Address.of("100", "台北市", null, "忠孝東路一段1號"));
        }

        @Test
        @DisplayName("街道地址為空應拋出例外")
        void shouldThrowExceptionForNullStreet() {
            assertThrows(IllegalArgumentException.class,
                    () -> Address.of("100", "台北市", "中正區", null));
        }

        @Test
        @DisplayName("郵遞區號超過5位數應拋出例外")
        void shouldThrowExceptionForZipCodeTooLong() {
            assertThrows(IllegalArgumentException.class,
                    () -> Address.of("123456", "台北市", "中正區", "忠孝東路一段1號"));
        }
    }

    @Nested
    @DisplayName("格式化測試")
    class FormattingTests {

        @Test
        @DisplayName("應正確格式化完整地址")
        void shouldFormatFullAddress() {
            Address address = Address.of("100", "台北市", "中正區", "忠孝東路一段1號");
            assertEquals("100 台北市中正區忠孝東路一段1號", address.getFullAddress());
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同內容應相等")
        void shouldBeEqualForSameContent() {
            Address address1 = Address.of("100", "台北市", "中正區", "忠孝東路一段1號");
            Address address2 = Address.of("100", "台北市", "中正區", "忠孝東路一段1號");
            assertEquals(address1, address2);
            assertEquals(address1.hashCode(), address2.hashCode());
        }

        @Test
        @DisplayName("不同內容應不相等")
        void shouldNotBeEqualForDifferentContent() {
            Address address1 = Address.of("100", "台北市", "中正區", "忠孝東路一段1號");
            Address address2 = Address.of("106", "台北市", "大安區", "忠孝東路四段1號");
            assertNotEquals(address1, address2);
        }
    }
}
