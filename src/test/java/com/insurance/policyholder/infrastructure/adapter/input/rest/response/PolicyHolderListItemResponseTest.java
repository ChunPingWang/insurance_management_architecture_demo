package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyHolderListItemResponse Tests")
class PolicyHolderListItemResponseTest {

    private static final String ID = "PH0000000001";
    private static final String MASKED_NATIONAL_ID = "A123***789";
    private static final String NAME = "John Doe";
    private static final String GENDER = "MALE";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String MOBILE_PHONE = "0912345678";
    private static final String STATUS = "ACTIVE";

    private PolicyHolderListItemResponse createFullResponse() {
        return new PolicyHolderListItemResponse(
                ID, MASKED_NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
        );
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create response with default constructor")
        void shouldCreateResponseWithDefaultConstructor() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            assertNull(response.getId());
            assertNull(response.getMaskedNationalId());
            assertNull(response.getName());
            assertNull(response.getGender());
            assertNull(response.getBirthDate());
            assertNull(response.getMobilePhone());
            assertNull(response.getStatus());
        }

        @Test
        @DisplayName("should create response with all properties")
        void shouldCreateResponseWithAllProperties() {
            // When
            PolicyHolderListItemResponse response = createFullResponse();

            // Then
            assertEquals(ID, response.getId());
            assertEquals(MASKED_NATIONAL_ID, response.getMaskedNationalId());
            assertEquals(NAME, response.getName());
            assertEquals(GENDER, response.getGender());
            assertEquals(BIRTH_DATE, response.getBirthDate());
            assertEquals(MOBILE_PHONE, response.getMobilePhone());
            assertEquals(STATUS, response.getStatus());
        }

        @Test
        @DisplayName("should handle null values in constructor")
        void shouldHandleNullValuesInConstructor() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    null, null, null, null, null, null, null
            );

            assertNull(response.getId());
            assertNull(response.getMaskedNationalId());
            assertNull(response.getName());
            assertNull(response.getGender());
            assertNull(response.getBirthDate());
            assertNull(response.getMobilePhone());
            assertNull(response.getStatus());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set id")
        void shouldSetId() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setId("PH9999999999");

            assertEquals("PH9999999999", response.getId());
        }

        @Test
        @DisplayName("should set masked national id")
        void shouldSetMaskedNationalId() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setMaskedNationalId("B987***321");

            assertEquals("B987***321", response.getMaskedNationalId());
        }

        @Test
        @DisplayName("should set name")
        void shouldSetName() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setName("Jane Doe");

            assertEquals("Jane Doe", response.getName());
        }

        @Test
        @DisplayName("should set gender")
        void shouldSetGender() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setGender("FEMALE");

            assertEquals("FEMALE", response.getGender());
        }

        @Test
        @DisplayName("should set birth date")
        void shouldSetBirthDate() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();
            LocalDate newDate = LocalDate.of(1985, 6, 20);

            response.setBirthDate(newDate);

            assertEquals(newDate, response.getBirthDate());
        }

        @Test
        @DisplayName("should set mobile phone")
        void shouldSetMobilePhone() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setMobilePhone("0987654321");

            assertEquals("0987654321", response.getMobilePhone());
        }

        @Test
        @DisplayName("should set status")
        void shouldSetStatus() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse();

            response.setStatus("INACTIVE");

            assertEquals("INACTIVE", response.getStatus());
        }
    }

    @Nested
    @DisplayName("Gender Value Tests")
    class GenderValueTests {

        @Test
        @DisplayName("should handle MALE gender")
        void shouldHandleMaleGender() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, "MALE", BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            assertEquals("MALE", response.getGender());
        }

        @Test
        @DisplayName("should handle FEMALE gender")
        void shouldHandleFemaleGender() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, "FEMALE", BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            assertEquals("FEMALE", response.getGender());
        }
    }

    @Nested
    @DisplayName("Status Value Tests")
    class StatusValueTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "ACTIVE"
            );

            assertEquals("ACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("should handle INACTIVE status")
        void shouldHandleInactiveStatus() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "INACTIVE"
            );

            assertEquals("INACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("should handle SUSPENDED status")
        void shouldHandleSuspendedStatus() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, "SUSPENDED"
            );

            assertEquals("SUSPENDED", response.getStatus());
        }
    }

    @Nested
    @DisplayName("Masked National ID Tests")
    class MaskedNationalIdTests {

        @Test
        @DisplayName("should store pre-masked national ID")
        void shouldStorePreMaskedNationalId() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, "F131***093", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            assertEquals("F131***093", response.getMaskedNationalId());
        }

        @Test
        @DisplayName("should accept any masked format")
        void shouldAcceptAnyMaskedFormat() {
            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, "****456789", NAME, GENDER, BIRTH_DATE, MOBILE_PHONE, STATUS
            );

            assertEquals("****456789", response.getMaskedNationalId());
        }
    }

    @Nested
    @DisplayName("Birth Date Tests")
    class BirthDateTests {

        @Test
        @DisplayName("should store birth date correctly")
        void shouldStoreBirthDateCorrectly() {
            LocalDate birthDate = LocalDate.of(2000, 12, 31);

            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, GENDER, birthDate, MOBILE_PHONE, STATUS
            );

            assertEquals(birthDate, response.getBirthDate());
        }

        @Test
        @DisplayName("should handle old birth date")
        void shouldHandleOldBirthDate() {
            LocalDate oldBirthDate = LocalDate.of(1920, 1, 1);

            PolicyHolderListItemResponse response = new PolicyHolderListItemResponse(
                    ID, MASKED_NATIONAL_ID, NAME, GENDER, oldBirthDate, MOBILE_PHONE, STATUS
            );

            assertEquals(oldBirthDate, response.getBirthDate());
        }
    }
}
