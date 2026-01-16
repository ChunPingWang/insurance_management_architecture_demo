package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolderResponse Tests")
class PolicyHolderResponseTest {

    private static final String ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String GENDER = "MALE";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String MOBILE_PHONE = "0912345678";
    private static final String EMAIL = "john@example.com";
    private static final String STATUS = "ACTIVE";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 6, 1, 15, 30);
    private static final Long VERSION = 1L;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create response with default constructor")
        void shouldCreateResponseWithDefaultConstructor() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            assertNull(response.getId());
            assertNull(response.getNationalId());
            assertNull(response.getName());
            assertNull(response.getGender());
            assertNull(response.getBirthDate());
            assertNull(response.getMobilePhone());
            assertNull(response.getEmail());
            assertNull(response.getAddress());
            assertNull(response.getStatus());
            assertNull(response.getCreatedAt());
            assertNull(response.getUpdatedAt());
            assertNull(response.getVersion());
        }
    }

    @Nested
    @DisplayName("Basic Property Setter Tests")
    class BasicPropertySetterTests {

        @Test
        @DisplayName("should set id")
        void shouldSetId() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setId(ID);

            assertEquals(ID, response.getId());
        }

        @Test
        @DisplayName("should set national id")
        void shouldSetNationalId() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setNationalId(NATIONAL_ID);

            assertEquals(NATIONAL_ID, response.getNationalId());
        }

        @Test
        @DisplayName("should set name")
        void shouldSetName() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setName(NAME);

            assertEquals(NAME, response.getName());
        }

        @Test
        @DisplayName("should set gender")
        void shouldSetGender() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setGender(GENDER);

            assertEquals(GENDER, response.getGender());
        }

        @Test
        @DisplayName("should set birth date")
        void shouldSetBirthDate() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setBirthDate(BIRTH_DATE);

            assertEquals(BIRTH_DATE, response.getBirthDate());
        }

        @Test
        @DisplayName("should set mobile phone")
        void shouldSetMobilePhone() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setMobilePhone(MOBILE_PHONE);

            assertEquals(MOBILE_PHONE, response.getMobilePhone());
        }

        @Test
        @DisplayName("should set email")
        void shouldSetEmail() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setEmail(EMAIL);

            assertEquals(EMAIL, response.getEmail());
        }

        @Test
        @DisplayName("should set status")
        void shouldSetStatus() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setStatus(STATUS);

            assertEquals(STATUS, response.getStatus());
        }

        @Test
        @DisplayName("should set version")
        void shouldSetVersion() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setVersion(VERSION);

            assertEquals(VERSION, response.getVersion());
        }
    }

    @Nested
    @DisplayName("Address Tests")
    class AddressTests {

        @Test
        @DisplayName("should set address response")
        void shouldSetAddressResponse() {
            PolicyHolderResponse response = new PolicyHolderResponse();
            AddressResponse address = new AddressResponse("10001", "Taipei", "Xinyi", "123 Test St");

            response.setAddress(address);

            assertNotNull(response.getAddress());
            assertEquals("10001", response.getAddress().getZipCode());
            assertEquals("Taipei", response.getAddress().getCity());
            assertEquals("Xinyi", response.getAddress().getDistrict());
            assertEquals("123 Test St", response.getAddress().getStreet());
        }

        @Test
        @DisplayName("should set null address")
        void shouldSetNullAddress() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setAddress(null);

            assertNull(response.getAddress());
        }

        @Test
        @DisplayName("should access nested address full address")
        void shouldAccessNestedAddressFullAddress() {
            PolicyHolderResponse response = new PolicyHolderResponse();
            AddressResponse address = new AddressResponse("10001", "Taipei", "Xinyi", "123 Test St");

            response.setAddress(address);

            assertEquals("10001 TaipeiXinyi123 Test St", response.getAddress().getFullAddress());
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("should set created at timestamp")
        void shouldSetCreatedAtTimestamp() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setCreatedAt(CREATED_AT);

            assertEquals(CREATED_AT, response.getCreatedAt());
        }

        @Test
        @DisplayName("should set updated at timestamp")
        void shouldSetUpdatedAtTimestamp() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setUpdatedAt(UPDATED_AT);

            assertEquals(UPDATED_AT, response.getUpdatedAt());
        }

        @Test
        @DisplayName("should handle timestamps with nanoseconds")
        void shouldHandleTimestampsWithNanoseconds() {
            PolicyHolderResponse response = new PolicyHolderResponse();
            LocalDateTime timestampWithNanos = LocalDateTime.of(2024, 6, 15, 10, 30, 45, 123456789);

            response.setCreatedAt(timestampWithNanos);

            assertEquals(timestampWithNanos, response.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Gender Value Tests")
    class GenderValueTests {

        @Test
        @DisplayName("should handle MALE gender")
        void shouldHandleMaleGender() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setGender("MALE");

            assertEquals("MALE", response.getGender());
        }

        @Test
        @DisplayName("should handle FEMALE gender")
        void shouldHandleFemaleGender() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setGender("FEMALE");

            assertEquals("FEMALE", response.getGender());
        }
    }

    @Nested
    @DisplayName("Status Value Tests")
    class StatusValueTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setStatus("ACTIVE");

            assertEquals("ACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("should handle INACTIVE status")
        void shouldHandleInactiveStatus() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setStatus("INACTIVE");

            assertEquals("INACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("should handle SUSPENDED status")
        void shouldHandleSuspendedStatus() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setStatus("SUSPENDED");

            assertEquals("SUSPENDED", response.getStatus());
        }
    }

    @Nested
    @DisplayName("Version Tests")
    class VersionTests {

        @Test
        @DisplayName("should handle zero version")
        void shouldHandleZeroVersion() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setVersion(0L);

            assertEquals(0L, response.getVersion());
        }

        @Test
        @DisplayName("should handle large version number")
        void shouldHandleLargeVersionNumber() {
            PolicyHolderResponse response = new PolicyHolderResponse();

            response.setVersion(999999L);

            assertEquals(999999L, response.getVersion());
        }
    }

    @Nested
    @DisplayName("Full Response Tests")
    class FullResponseTests {

        @Test
        @DisplayName("should set all properties correctly")
        void shouldSetAllPropertiesCorrectly() {
            // Given
            PolicyHolderResponse response = new PolicyHolderResponse();
            AddressResponse address = new AddressResponse("10001", "Taipei", "Xinyi", "123 Test St");

            // When
            response.setId(ID);
            response.setNationalId(NATIONAL_ID);
            response.setName(NAME);
            response.setGender(GENDER);
            response.setBirthDate(BIRTH_DATE);
            response.setMobilePhone(MOBILE_PHONE);
            response.setEmail(EMAIL);
            response.setAddress(address);
            response.setStatus(STATUS);
            response.setCreatedAt(CREATED_AT);
            response.setUpdatedAt(UPDATED_AT);
            response.setVersion(VERSION);

            // Then
            assertEquals(ID, response.getId());
            assertEquals(NATIONAL_ID, response.getNationalId());
            assertEquals(NAME, response.getName());
            assertEquals(GENDER, response.getGender());
            assertEquals(BIRTH_DATE, response.getBirthDate());
            assertEquals(MOBILE_PHONE, response.getMobilePhone());
            assertEquals(EMAIL, response.getEmail());
            assertNotNull(response.getAddress());
            assertEquals(STATUS, response.getStatus());
            assertEquals(CREATED_AT, response.getCreatedAt());
            assertEquals(UPDATED_AT, response.getUpdatedAt());
            assertEquals(VERSION, response.getVersion());
        }
    }
}
