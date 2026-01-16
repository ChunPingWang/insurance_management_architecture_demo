package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AddressResponse Tests")
class AddressResponseTest {

    private static final String ZIP_CODE = "10001";
    private static final String CITY = "Taipei";
    private static final String DISTRICT = "Xinyi";
    private static final String STREET = "123 Test Street";

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create address response with default constructor")
        void shouldCreateAddressResponseWithDefaultConstructor() {
            AddressResponse response = new AddressResponse();

            assertNull(response.getZipCode());
            assertNull(response.getCity());
            assertNull(response.getDistrict());
            assertNull(response.getStreet());
            assertNull(response.getFullAddress());
        }

        @Test
        @DisplayName("should create address response with all properties")
        void shouldCreateAddressResponseWithAllProperties() {
            // When
            AddressResponse response = new AddressResponse(ZIP_CODE, CITY, DISTRICT, STREET);

            // Then
            assertEquals(ZIP_CODE, response.getZipCode());
            assertEquals(CITY, response.getCity());
            assertEquals(DISTRICT, response.getDistrict());
            assertEquals(STREET, response.getStreet());
        }

        @Test
        @DisplayName("should calculate full address in constructor")
        void shouldCalculateFullAddressInConstructor() {
            // When
            AddressResponse response = new AddressResponse(ZIP_CODE, CITY, DISTRICT, STREET);

            // Then
            assertEquals("10001 TaipeiXinyi123 Test Street", response.getFullAddress());
        }

        @Test
        @DisplayName("should handle different address components")
        void shouldHandleDifferentAddressComponents() {
            AddressResponse response = new AddressResponse(
                    "80000", "Kaohsiung", "Sanmin", "456 Another Street"
            );

            assertEquals("80000 KaohsiungSanmin456 Another Street", response.getFullAddress());
        }

        @Test
        @DisplayName("should handle null values in full address calculation")
        void shouldHandleNullValuesInFullAddressCalculation() {
            AddressResponse response = new AddressResponse(null, null, null, null);

            assertEquals("null nullnullnull", response.getFullAddress());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set zip code")
        void shouldSetZipCode() {
            AddressResponse response = new AddressResponse();

            response.setZipCode("12345");

            assertEquals("12345", response.getZipCode());
        }

        @Test
        @DisplayName("should set city")
        void shouldSetCity() {
            AddressResponse response = new AddressResponse();

            response.setCity("New City");

            assertEquals("New City", response.getCity());
        }

        @Test
        @DisplayName("should set district")
        void shouldSetDistrict() {
            AddressResponse response = new AddressResponse();

            response.setDistrict("New District");

            assertEquals("New District", response.getDistrict());
        }

        @Test
        @DisplayName("should set street")
        void shouldSetStreet() {
            AddressResponse response = new AddressResponse();

            response.setStreet("New Street");

            assertEquals("New Street", response.getStreet());
        }

        @Test
        @DisplayName("should set full address directly")
        void shouldSetFullAddressDirectly() {
            AddressResponse response = new AddressResponse();

            response.setFullAddress("Custom Full Address");

            assertEquals("Custom Full Address", response.getFullAddress());
        }

        @Test
        @DisplayName("setters should not affect full address after construction")
        void settersShouldNotAffectFullAddressAfterConstruction() {
            // Given
            AddressResponse response = new AddressResponse(ZIP_CODE, CITY, DISTRICT, STREET);
            String originalFullAddress = response.getFullAddress();

            // When
            response.setZipCode("99999");
            response.setCity("Other City");

            // Then - full address was calculated at construction and is not recalculated
            assertEquals(originalFullAddress, response.getFullAddress());
        }
    }

    @Nested
    @DisplayName("Full Address Format Tests")
    class FullAddressFormatTests {

        @Test
        @DisplayName("should format full address with space after zip code")
        void shouldFormatFullAddressWithSpaceAfterZipCode() {
            AddressResponse response = new AddressResponse("10000", "City", "District", "Street");

            String fullAddress = response.getFullAddress();

            assertTrue(fullAddress.startsWith("10000 "));
        }

        @Test
        @DisplayName("should concatenate city and district without space")
        void shouldConcatenateCityAndDistrictWithoutSpace() {
            AddressResponse response = new AddressResponse("10000", "Taipei", "Xinyi", "Street");

            String fullAddress = response.getFullAddress();

            assertTrue(fullAddress.contains("TaipeiXinyi"));
        }

        @Test
        @DisplayName("should concatenate district and street without space")
        void shouldConcatenateDistrictAndStreetWithoutSpace() {
            AddressResponse response = new AddressResponse("10000", "Taipei", "Xinyi", "Main St");

            String fullAddress = response.getFullAddress();

            assertTrue(fullAddress.contains("XinyiMain St"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle empty string components")
        void shouldHandleEmptyStringComponents() {
            AddressResponse response = new AddressResponse("", "", "", "");

            // Format: zipCode + " " + city + district + street = "" + " " + "" + "" + "" = " "
            assertEquals(" ", response.getFullAddress());
        }

        @Test
        @DisplayName("should handle whitespace in components")
        void shouldHandleWhitespaceInComponents() {
            AddressResponse response = new AddressResponse("10001", "New Taipei", "Da An", "No. 100, Sec. 2");

            assertEquals("10001 New TaipeiDa AnNo. 100, Sec. 2", response.getFullAddress());
        }

        @Test
        @DisplayName("should handle long address components")
        void shouldHandleLongAddressComponents() {
            String longStreet = "123 Very Long Street Name That Goes On And On";
            AddressResponse response = new AddressResponse("10001", "City", "District", longStreet);

            assertTrue(response.getFullAddress().endsWith(longStreet));
        }

        @Test
        @DisplayName("should handle special characters in address")
        void shouldHandleSpecialCharactersInAddress() {
            AddressResponse response = new AddressResponse(
                    "10001", "Taipei", "Xinyi", "No.1, Ln. 100, Sec. 5, Xinyi Rd."
            );

            String fullAddress = response.getFullAddress();
            assertTrue(fullAddress.contains("No.1, Ln. 100, Sec. 5, Xinyi Rd."));
        }
    }
}
