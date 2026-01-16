package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import com.insurance.policyholder.application.readmodel.PagedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PageResponse Tests")
class PageResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create page response with default constructor")
        void shouldCreatePageResponseWithDefaultConstructor() {
            PageResponse<String> response = new PageResponse<>();

            assertNull(response.getContent());
            assertEquals(0, response.getPage());
            assertEquals(0, response.getSize());
            assertEquals(0, response.getTotalElements());
            assertEquals(0, response.getTotalPages());
            assertFalse(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("should create page response with all properties")
        void shouldCreatePageResponseWithAllProperties() {
            // Given
            List<String> content = Arrays.asList("item1", "item2", "item3");

            // When
            PageResponse<String> response = new PageResponse<>(
                    content, 0, 10, 25, 3, true, false
            );

            // Then
            assertEquals(content, response.getContent());
            assertEquals(0, response.getPage());
            assertEquals(10, response.getSize());
            assertEquals(25, response.getTotalElements());
            assertEquals(3, response.getTotalPages());
            assertTrue(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("should create page response for last page")
        void shouldCreatePageResponseForLastPage() {
            List<String> content = Arrays.asList("item1", "item2");

            PageResponse<String> response = new PageResponse<>(
                    content, 2, 10, 22, 3, false, true
            );

            assertFalse(response.isFirst());
            assertTrue(response.isLast());
            assertEquals(2, response.getPage());
        }

        @Test
        @DisplayName("should create page response for single page")
        void shouldCreatePageResponseForSinglePage() {
            List<String> content = Arrays.asList("item1");

            PageResponse<String> response = new PageResponse<>(
                    content, 0, 10, 1, 1, true, true
            );

            assertTrue(response.isFirst());
            assertTrue(response.isLast());
        }

        @Test
        @DisplayName("should create page response with empty content")
        void shouldCreatePageResponseWithEmptyContent() {
            PageResponse<String> response = new PageResponse<>(
                    Collections.emptyList(), 0, 10, 0, 0, true, true
            );

            assertTrue(response.getContent().isEmpty());
            assertEquals(0, response.getTotalElements());
            assertEquals(0, response.getTotalPages());
        }
    }

    @Nested
    @DisplayName("Factory Method from(PagedResult, mapper) Tests")
    class FactoryMethodWithMapperTests {

        @Test
        @DisplayName("should create page response from paged result with mapper")
        void shouldCreatePageResponseFromPagedResultWithMapper() {
            // Given
            List<Integer> sourceContent = Arrays.asList(1, 2, 3);
            PagedResult<Integer> pagedResult = new PagedResult<>(
                    sourceContent, 0, 10, 30
            );

            // When
            PageResponse<String> response = PageResponse.from(pagedResult, String::valueOf);

            // Then
            assertEquals(3, response.getContent().size());
            assertEquals("1", response.getContent().get(0));
            assertEquals("2", response.getContent().get(1));
            assertEquals("3", response.getContent().get(2));
            assertEquals(0, response.getPage());
            assertEquals(10, response.getSize());
            assertEquals(30, response.getTotalElements());
            assertEquals(3, response.getTotalPages());
            assertTrue(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("should create page response from paged result with complex mapper")
        void shouldCreatePageResponseFromPagedResultWithComplexMapper() {
            // Given
            record Source(String name, int value) {}
            record Target(String displayName) {}

            List<Source> sourceContent = Arrays.asList(
                    new Source("a", 1),
                    new Source("b", 2)
            );
            PagedResult<Source> pagedResult = new PagedResult<>(
                    sourceContent, 1, 2, 10
            );

            // When
            PageResponse<Target> response = PageResponse.from(
                    pagedResult,
                    s -> new Target(s.name() + "-" + s.value())
            );

            // Then
            assertEquals(2, response.getContent().size());
            assertEquals("a-1", response.getContent().get(0).displayName());
            assertEquals("b-2", response.getContent().get(1).displayName());
        }

        @Test
        @DisplayName("should handle empty paged result with mapper")
        void shouldHandleEmptyPagedResultWithMapper() {
            // Given
            PagedResult<Integer> pagedResult = new PagedResult<>(
                    Collections.emptyList(), 0, 10, 0
            );

            // When
            PageResponse<String> response = PageResponse.from(pagedResult, String::valueOf);

            // Then
            assertTrue(response.getContent().isEmpty());
            assertEquals(0, response.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Factory Method from(PagedResult) Tests")
    class FactoryMethodWithoutMapperTests {

        @Test
        @DisplayName("should create page response from paged result without mapper")
        void shouldCreatePageResponseFromPagedResultWithoutMapper() {
            // Given
            List<String> content = Arrays.asList("item1", "item2");
            PagedResult<String> pagedResult = new PagedResult<>(
                    content, 0, 10, 20
            );

            // When
            PageResponse<String> response = PageResponse.from(pagedResult);

            // Then
            assertEquals(content, response.getContent());
            assertEquals(0, response.getPage());
            assertEquals(10, response.getSize());
            assertEquals(20, response.getTotalElements());
            assertEquals(2, response.getTotalPages());
            assertTrue(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("should preserve pagination info from paged result")
        void shouldPreservePaginationInfoFromPagedResult() {
            // Given - middle page
            List<String> content = Arrays.asList("a", "b", "c");
            PagedResult<String> pagedResult = new PagedResult<>(
                    content, 2, 3, 10
            );

            // When
            PageResponse<String> response = PageResponse.from(pagedResult);

            // Then
            assertEquals(2, response.getPage());
            assertEquals(3, response.getSize());
            assertEquals(10, response.getTotalElements());
            assertEquals(4, response.getTotalPages());
            assertFalse(response.isFirst());
            assertFalse(response.isLast());
        }

        @Test
        @DisplayName("should handle single page paged result")
        void shouldHandleSinglePagePagedResult() {
            // Given
            List<String> content = Arrays.asList("only");
            PagedResult<String> pagedResult = new PagedResult<>(
                    content, 0, 10, 1
            );

            // When
            PageResponse<String> response = PageResponse.from(pagedResult);

            // Then
            assertTrue(response.isFirst());
            assertTrue(response.isLast());
            assertEquals(1, response.getTotalPages());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set content")
        void shouldSetContent() {
            PageResponse<String> response = new PageResponse<>();
            List<String> content = Arrays.asList("a", "b");

            response.setContent(content);

            assertEquals(content, response.getContent());
        }

        @Test
        @DisplayName("should set page")
        void shouldSetPage() {
            PageResponse<String> response = new PageResponse<>();

            response.setPage(5);

            assertEquals(5, response.getPage());
        }

        @Test
        @DisplayName("should set size")
        void shouldSetSize() {
            PageResponse<String> response = new PageResponse<>();

            response.setSize(25);

            assertEquals(25, response.getSize());
        }

        @Test
        @DisplayName("should set total elements")
        void shouldSetTotalElements() {
            PageResponse<String> response = new PageResponse<>();

            response.setTotalElements(100L);

            assertEquals(100L, response.getTotalElements());
        }

        @Test
        @DisplayName("should set total pages")
        void shouldSetTotalPages() {
            PageResponse<String> response = new PageResponse<>();

            response.setTotalPages(10);

            assertEquals(10, response.getTotalPages());
        }

        @Test
        @DisplayName("should set first flag")
        void shouldSetFirstFlag() {
            PageResponse<String> response = new PageResponse<>();

            response.setFirst(true);

            assertTrue(response.isFirst());
        }

        @Test
        @DisplayName("should set last flag")
        void shouldSetLastFlag() {
            PageResponse<String> response = new PageResponse<>();

            response.setLast(true);

            assertTrue(response.isLast());
        }
    }

    @Nested
    @DisplayName("Generic Type Tests")
    class GenericTypeTests {

        @Test
        @DisplayName("should work with Integer type")
        void shouldWorkWithIntegerType() {
            List<Integer> content = Arrays.asList(1, 2, 3);
            PageResponse<Integer> response = new PageResponse<>(
                    content, 0, 10, 3, 1, true, true
            );

            assertEquals(Integer.valueOf(1), response.getContent().get(0));
        }

        @Test
        @DisplayName("should work with custom record type")
        void shouldWorkWithCustomRecordType() {
            record Item(String id, String name) {}
            List<Item> content = Arrays.asList(new Item("1", "test"));

            PageResponse<Item> response = new PageResponse<>(
                    content, 0, 10, 1, 1, true, true
            );

            assertEquals("1", response.getContent().get(0).id());
            assertEquals("test", response.getContent().get(0).name());
        }
    }
}
