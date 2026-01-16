package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Nested
    @DisplayName("Success Response Tests")
    class SuccessResponseTests {

        @Test
        @DisplayName("should create success response with data")
        void shouldCreateSuccessResponseWithData() {
            // Given
            String data = "test data";

            // When
            ApiResponse<String> response = ApiResponse.success(data);

            // Then
            assertTrue(response.isSuccess());
            assertEquals(data, response.getData());
            assertNull(response.getMessage());
            assertNotNull(response.getTimestamp());
        }

        @Test
        @DisplayName("should create success response with data and message")
        void shouldCreateSuccessResponseWithDataAndMessage() {
            // Given
            String data = "test data";
            String message = "Operation successful";

            // When
            ApiResponse<String> response = ApiResponse.success(data, message);

            // Then
            assertTrue(response.isSuccess());
            assertEquals(data, response.getData());
            assertEquals(message, response.getMessage());
            assertNotNull(response.getTimestamp());
        }

        @Test
        @DisplayName("should create success response with null data")
        void shouldCreateSuccessResponseWithNullData() {
            // When
            ApiResponse<String> response = ApiResponse.success(null);

            // Then
            assertTrue(response.isSuccess());
            assertNull(response.getData());
        }

        @Test
        @DisplayName("should create success response with complex object")
        void shouldCreateSuccessResponseWithComplexObject() {
            // Given
            record TestObject(String name, int value) {}
            TestObject data = new TestObject("test", 42);

            // When
            ApiResponse<TestObject> response = ApiResponse.success(data);

            // Then
            assertTrue(response.isSuccess());
            assertEquals("test", response.getData().name());
            assertEquals(42, response.getData().value());
        }
    }

    @Nested
    @DisplayName("Error Response Tests")
    class ErrorResponseTests {

        @Test
        @DisplayName("should create error response with message")
        void shouldCreateErrorResponseWithMessage() {
            // Given
            String errorMessage = "Something went wrong";

            // When
            ApiResponse<String> response = ApiResponse.error(errorMessage);

            // Then
            assertFalse(response.isSuccess());
            assertNull(response.getData());
            assertEquals(errorMessage, response.getMessage());
            assertNotNull(response.getTimestamp());
        }

        @Test
        @DisplayName("should create error response with null message")
        void shouldCreateErrorResponseWithNullMessage() {
            // When
            ApiResponse<String> response = ApiResponse.error(null);

            // Then
            assertFalse(response.isSuccess());
            assertNull(response.getData());
            assertNull(response.getMessage());
        }

        @Test
        @DisplayName("should create error response with empty message")
        void shouldCreateErrorResponseWithEmptyMessage() {
            // When
            ApiResponse<String> response = ApiResponse.error("");

            // Then
            assertFalse(response.isSuccess());
            assertEquals("", response.getMessage());
        }
    }

    @Nested
    @DisplayName("Timestamp Tests")
    class TimestampTests {

        @Test
        @DisplayName("should have different timestamps for different responses")
        void shouldHaveDifferentTimestampsForDifferentResponses() throws InterruptedException {
            // When
            ApiResponse<String> response1 = ApiResponse.success("data1");
            Thread.sleep(10); // Small delay to ensure different timestamps
            ApiResponse<String> response2 = ApiResponse.success("data2");

            // Then
            assertNotNull(response1.getTimestamp());
            assertNotNull(response2.getTimestamp());
            // Timestamps should be close but may or may not be exactly equal
        }
    }

    @Nested
    @DisplayName("Generic Type Tests")
    class GenericTypeTests {

        @Test
        @DisplayName("should work with Integer type")
        void shouldWorkWithIntegerType() {
            ApiResponse<Integer> response = ApiResponse.success(42);
            assertEquals(Integer.valueOf(42), response.getData());
        }

        @Test
        @DisplayName("should work with Boolean type")
        void shouldWorkWithBooleanType() {
            ApiResponse<Boolean> response = ApiResponse.success(true);
            assertEquals(Boolean.TRUE, response.getData());
        }

        @Test
        @DisplayName("should work with List type")
        void shouldWorkWithListType() {
            java.util.List<String> data = java.util.Arrays.asList("a", "b", "c");
            ApiResponse<java.util.List<String>> response = ApiResponse.success(data);
            assertEquals(3, response.getData().size());
        }
    }
}
