package com.insurance.policyholder.application.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolderListItemReadModel Tests")
class PolicyHolderListItemReadModelTest {

    private static final String ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String GENDER = "MALE";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String MOBILE_PHONE = "0912345678";
    private static final String STATUS = "ACTIVE";

    @Nested
    @DisplayName("Constructor and Basic Properties Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create read model with all properties")
        void shouldCreateReadModelWithAllProperties() {
            // When
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            assertEquals(ID, model.getId());
            assertEquals(NAME, model.getName());
            assertEquals(GENDER, model.getGender());
            assertEquals(BIRTH_DATE, model.getBirthDate());
            assertEquals(MOBILE_PHONE, model.getMobilePhone());
            assertEquals(STATUS, model.getStatus());
        }

        @Test
        @DisplayName("should handle null values")
        void shouldHandleNullValues() {
            // When
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    null, null, null, null, null, null, null
            );

            // Then
            assertNull(model.getId());
            assertNull(model.getName());
            assertNull(model.getGender());
            assertNull(model.getBirthDate());
            assertNull(model.getMobilePhone());
            assertNull(model.getStatus());
        }
    }

    @Nested
    @DisplayName("National ID Masking Tests")
    class NationalIdMaskingTests {

        @Test
        @DisplayName("should mask national ID correctly")
        void shouldMaskNationalIdCorrectly() {
            // When
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, "A123456789", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            assertEquals("A123***789", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should mask different national IDs")
        void shouldMaskDifferentNationalIds() {
            // Test with different first letters
            PolicyHolderListItemReadModel model1 = new PolicyHolderListItemReadModel(
                    ID, "F131104093", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );
            assertEquals("F131***093", model1.getMaskedNationalId());

            PolicyHolderListItemReadModel model2 = new PolicyHolderListItemReadModel(
                    ID, "B987654321", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );
            assertEquals("B987***321", model2.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return null when national ID is null")
        void shouldReturnNullWhenNationalIdIsNull() {
            // When
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, null, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            assertNull(model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return original when national ID is too short")
        void shouldReturnOriginalWhenNationalIdIsTooShort() {
            // When - ID less than 10 characters
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, "A12345", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            assertEquals("A12345", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should return original for empty string")
        void shouldReturnOriginalForEmptyString() {
            // When
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, "", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            assertEquals("", model.getMaskedNationalId());
        }

        @Test
        @DisplayName("should mask exactly 10 character ID")
        void shouldMaskExactlyTenCharacterId() {
            // When - exactly 10 characters
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, "A123456789", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            // Then
            String masked = model.getMaskedNationalId();
            assertEquals(10, masked.length());
            assertEquals("A123***789", masked);
        }
    }

    @Nested
    @DisplayName("Status Values Tests")
    class StatusValuesTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "ACTIVE"
            );
            assertEquals("ACTIVE", model.getStatus());
        }

        @Test
        @DisplayName("should handle INACTIVE status")
        void shouldHandleInactiveStatus() {
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "INACTIVE"
            );
            assertEquals("INACTIVE", model.getStatus());
        }

        @Test
        @DisplayName("should handle SUSPENDED status")
        void shouldHandleSuspendedStatus() {
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "SUSPENDED"
            );
            assertEquals("SUSPENDED", model.getStatus());
        }
    }

    @Nested
    @DisplayName("Gender Values Tests")
    class GenderValuesTests {

        @Test
        @DisplayName("should handle MALE gender")
        void shouldHandleMaleGender() {
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, "MALE", BIRTH_DATE, MOBILE_PHONE, STATUS
            );
            assertEquals("MALE", model.getGender());
        }

        @Test
        @DisplayName("should handle FEMALE gender")
        void shouldHandleFemaleGender() {
            PolicyHolderListItemReadModel model = new PolicyHolderListItemReadModel(
                    ID, NATIONAL_ID, NAME, "FEMALE", BIRTH_DATE, MOBILE_PHONE, STATUS
            );
            assertEquals("FEMALE", model.getGender());
        }
    }
}
