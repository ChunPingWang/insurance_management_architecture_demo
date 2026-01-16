package com.insurance.policyholder.application.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolderReadModel Tests")
class PolicyHolderReadModelTest {

    private static final String ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String GENDER = "MALE";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String MOBILE_PHONE = "0912345678";
    private static final String EMAIL = "john@example.com";
    private static final String ZIP_CODE = "10001";
    private static final String CITY = "Taipei";
    private static final String DISTRICT = "Xinyi";
    private static final String STREET = "123 Test Street";
    private static final String STATUS = "ACTIVE";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 1, 10, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 6, 1, 15, 30);
    private static final Long VERSION = 1L;

    private PolicyHolderReadModel createFullModel() {
        return new PolicyHolderReadModel(
                ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                STATUS, CREATED_AT, UPDATED_AT, VERSION
        );
    }

    @Nested
    @DisplayName("Constructor and Basic Properties Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create read model with all properties")
        void shouldCreateReadModelWithAllProperties() {
            // When
            PolicyHolderReadModel model = createFullModel();

            // Then
            assertEquals(ID, model.getId());
            assertEquals(NATIONAL_ID, model.getNationalId());
            assertEquals(NAME, model.getName());
            assertEquals(GENDER, model.getGender());
            assertEquals(BIRTH_DATE, model.getBirthDate());
            assertEquals(MOBILE_PHONE, model.getMobilePhone());
            assertEquals(EMAIL, model.getEmail());
            assertEquals(ZIP_CODE, model.getZipCode());
            assertEquals(CITY, model.getCity());
            assertEquals(DISTRICT, model.getDistrict());
            assertEquals(STREET, model.getStreet());
            assertEquals(STATUS, model.getStatus());
            assertEquals(CREATED_AT, model.getCreatedAt());
            assertEquals(UPDATED_AT, model.getUpdatedAt());
            assertEquals(VERSION, model.getVersion());
        }

        @Test
        @DisplayName("should handle null values")
        void shouldHandleNullValues() {
            // When
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null
            );

            // Then
            assertNull(model.getId());
            assertNull(model.getNationalId());
            assertNull(model.getName());
            assertNull(model.getGender());
            assertNull(model.getBirthDate());
            assertNull(model.getMobilePhone());
            assertNull(model.getEmail());
            assertNull(model.getZipCode());
            assertNull(model.getCity());
            assertNull(model.getDistrict());
            assertNull(model.getStreet());
            assertNull(model.getStatus());
            assertNull(model.getCreatedAt());
            assertNull(model.getUpdatedAt());
            assertNull(model.getVersion());
        }
    }

    @Nested
    @DisplayName("Full Address Tests")
    class FullAddressTests {

        @Test
        @DisplayName("should return full address with all components")
        void shouldReturnFullAddressWithAllComponents() {
            // When
            PolicyHolderReadModel model = createFullModel();

            // Then
            String fullAddress = model.getFullAddress();
            assertEquals("10001 TaipeiXinyi123 Test Street", fullAddress);
        }

        @Test
        @DisplayName("should format full address correctly")
        void shouldFormatFullAddressCorrectly() {
            // Given
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, "80000", "Kaohsiung", "Sanmin", "456 Another Street",
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // When
            String fullAddress = model.getFullAddress();

            // Then
            assertEquals("80000 KaohsiungSanmin456 Another Street", fullAddress);
        }

        @Test
        @DisplayName("should handle null address components in full address")
        void shouldHandleNullAddressComponentsInFullAddress() {
            // Given
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, null, null, null, null,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // When
            String fullAddress = model.getFullAddress();

            // Then - concatenation with nulls
            assertEquals("null nullnullnull", fullAddress);
        }
    }

    @Nested
    @DisplayName("Masked National ID Tests")
    class MaskedNationalIdTests {

        @Test
        @DisplayName("should mask national ID correctly")
        void shouldMaskNationalIdCorrectly() {
            // When
            PolicyHolderReadModel model = createFullModel();

            // Then
            assertEquals("A123***789", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should mask different national IDs")
        void shouldMaskDifferentNationalIds() {
            PolicyHolderReadModel model1 = new PolicyHolderReadModel(
                    ID, "F131104093", NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );
            assertEquals("F131***093", model1.getMaskedNationalId());

            PolicyHolderReadModel model2 = new PolicyHolderReadModel(
                    ID, "B987654321", NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );
            assertEquals("B987***321", model2.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return null when national ID is null")
        void shouldReturnNullWhenNationalIdIsNull() {
            // Given
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, null, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // Then
            assertNull(model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return original when national ID is too short")
        void shouldReturnOriginalWhenNationalIdIsTooShort() {
            // Given
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, "A12345", NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // Then
            assertEquals("A12345", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return empty string for empty national ID")
        void shouldReturnEmptyStringForEmptyNationalId() {
            // Given
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, "", NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // Then
            assertEquals("", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should handle 9 character national ID")
        void shouldHandleNineCharacterNationalId() {
            // Given - exactly 9 characters (less than 10)
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, "A12345678", NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, VERSION
            );

            // Then - should return original
            assertEquals("A12345678", model.getMaskedNationalId());
        }
    }

    @Nested
    @DisplayName("Status Values Tests")
    class StatusValuesTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    "ACTIVE", CREATED_AT, UPDATED_AT, VERSION
            );
            assertEquals("ACTIVE", model.getStatus());
        }

        @Test
        @DisplayName("should handle INACTIVE status")
        void shouldHandleInactiveStatus() {
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    "INACTIVE", CREATED_AT, UPDATED_AT, VERSION
            );
            assertEquals("INACTIVE", model.getStatus());
        }

        @Test
        @DisplayName("should handle SUSPENDED status")
        void shouldHandleSuspendedStatus() {
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    "SUSPENDED", CREATED_AT, UPDATED_AT, VERSION
            );
            assertEquals("SUSPENDED", model.getStatus());
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("should store created at timestamp")
        void shouldStoreCreatedAtTimestamp() {
            LocalDateTime createdAt = LocalDateTime.of(2024, 3, 15, 9, 30, 45);
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, createdAt, UPDATED_AT, VERSION
            );
            assertEquals(createdAt, model.getCreatedAt());
        }

        @Test
        @DisplayName("should store updated at timestamp")
        void shouldStoreUpdatedAtTimestamp() {
            LocalDateTime updatedAt = LocalDateTime.of(2024, 12, 25, 18, 45, 30);
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, updatedAt, VERSION
            );
            assertEquals(updatedAt, model.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Version Tests")
    class VersionTests {

        @Test
        @DisplayName("should store version number")
        void shouldStoreVersionNumber() {
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, 5L
            );
            assertEquals(5L, model.getVersion());
        }

        @Test
        @DisplayName("should handle zero version")
        void shouldHandleZeroVersion() {
            PolicyHolderReadModel model = new PolicyHolderReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE,
                    MOBILE_PHONE, EMAIL, ZIP_CODE, CITY, DISTRICT, STREET,
                    STATUS, CREATED_AT, UPDATED_AT, 0L
            );
            assertEquals(0L, model.getVersion());
        }
    }
}
