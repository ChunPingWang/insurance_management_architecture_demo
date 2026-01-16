package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Nested
    @DisplayName("Basic Error Response Tests")
    class BasicErrorResponseTests {

        @Test
        @DisplayName("should create error response with all properties")
        void shouldCreateErrorResponseWithAllProperties() {
            // When
            ErrorResponse response = ErrorResponse.of(400, "BAD_REQUEST", "Invalid input", "/api/test");

            // Then
            assertEquals(400, response.getStatus());
            assertEquals("BAD_REQUEST", response.getError());
            assertEquals("Invalid input", response.getMessage());
            assertEquals("/api/test", response.getPath());
            assertNotNull(response.getTimestamp());
            assertTrue(response.getFieldErrors().isEmpty());
        }

        @Test
        @DisplayName("should create 404 error response")
        void shouldCreate404ErrorResponse() {
            ErrorResponse response = ErrorResponse.of(404, "NOT_FOUND", "Resource not found", "/api/resource/1");

            assertEquals(404, response.getStatus());
            assertEquals("NOT_FOUND", response.getError());
        }

        @Test
        @DisplayName("should create 500 error response")
        void shouldCreate500ErrorResponse() {
            ErrorResponse response = ErrorResponse.of(500, "INTERNAL_ERROR", "Server error", "/api/test");

            assertEquals(500, response.getStatus());
            assertEquals("INTERNAL_ERROR", response.getError());
        }

        @Test
        @DisplayName("should handle null message")
        void shouldHandleNullMessage() {
            ErrorResponse response = ErrorResponse.of(400, "ERROR", null, "/api/test");

            assertNull(response.getMessage());
        }
    }

    @Nested
    @DisplayName("Field Error Tests")
    class FieldErrorTests {

        @Test
        @DisplayName("should create error response with field errors")
        void shouldCreateErrorResponseWithFieldErrors() {
            // Given
            List<ErrorResponse.FieldError> fieldErrors = Arrays.asList(
                    new ErrorResponse.FieldError("name", "Name is required", null),
                    new ErrorResponse.FieldError("email", "Invalid email format", "invalid-email")
            );

            // When
            ErrorResponse response = ErrorResponse.of(400, "VALIDATION_ERROR", "Validation failed", "/api/test", fieldErrors);

            // Then
            assertEquals(2, response.getFieldErrors().size());
            assertEquals("name", response.getFieldErrors().get(0).getField());
            assertEquals("Name is required", response.getFieldErrors().get(0).getMessage());
            assertNull(response.getFieldErrors().get(0).getRejectedValue());
            assertEquals("email", response.getFieldErrors().get(1).getField());
            assertEquals("invalid-email", response.getFieldErrors().get(1).getRejectedValue());
        }

        @Test
        @DisplayName("should create error response with null field errors list")
        void shouldCreateErrorResponseWithNullFieldErrorsList() {
            // When
            ErrorResponse response = ErrorResponse.of(400, "ERROR", "Error", "/api/test", null);

            // Then
            assertNotNull(response.getFieldErrors());
            assertTrue(response.getFieldErrors().isEmpty());
        }

        @Test
        @DisplayName("should create error response with empty field errors list")
        void shouldCreateErrorResponseWithEmptyFieldErrorsList() {
            // When
            ErrorResponse response = ErrorResponse.of(400, "ERROR", "Error", "/api/test", Arrays.asList());

            // Then
            assertTrue(response.getFieldErrors().isEmpty());
        }
    }

    @Nested
    @DisplayName("FieldError Inner Class Tests")
    class FieldErrorInnerClassTests {

        @Test
        @DisplayName("should create field error with all properties")
        void shouldCreateFieldErrorWithAllProperties() {
            // When
            ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("fieldName", "Error message", "rejected");

            // Then
            assertEquals("fieldName", fieldError.getField());
            assertEquals("Error message", fieldError.getMessage());
            assertEquals("rejected", fieldError.getRejectedValue());
        }

        @Test
        @DisplayName("should create field error with null rejected value")
        void shouldCreateFieldErrorWithNullRejectedValue() {
            ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("field", "message", null);

            assertNull(fieldError.getRejectedValue());
        }

        @Test
        @DisplayName("should create field error with numeric rejected value")
        void shouldCreateFieldErrorWithNumericRejectedValue() {
            ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("age", "Must be positive", -5);

            assertEquals(-5, fieldError.getRejectedValue());
        }

        @Test
        @DisplayName("should create field error with object rejected value")
        void shouldCreateFieldErrorWithObjectRejectedValue() {
            record TestValue(String name) {}
            TestValue rejectedValue = new TestValue("test");

            ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("object", "Invalid object", rejectedValue);

            assertEquals(rejectedValue, fieldError.getRejectedValue());
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("should have timestamp set automatically")
        void shouldHaveTimestampSetAutomatically() {
            ErrorResponse response = ErrorResponse.of(400, "ERROR", "message", "/path");

            assertNotNull(response.getTimestamp());
        }
    }
}
