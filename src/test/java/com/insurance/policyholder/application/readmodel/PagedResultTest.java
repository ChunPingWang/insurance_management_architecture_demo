package com.insurance.policyholder.application.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PagedResult Tests")
class PagedResultTest {

    @Nested
    @DisplayName("Constructor and Basic Properties Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create paged result with content")
        void shouldCreatePagedResultWithContent() {
            // Given
            List<String> content = Arrays.asList("item1", "item2", "item3");

            // When
            PagedResult<String> result = new PagedResult<>(content, 0, 10, 3);

            // Then
            assertEquals(content, result.getContent());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(3, result.getTotalElements());
        }

        @Test
        @DisplayName("should create paged result with empty content")
        void shouldCreatePagedResultWithEmptyContent() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 0);

            // Then
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("should handle null content")
        void shouldHandleNullContent() {
            // When
            PagedResult<String> result = new PagedResult<>(null, 0, 10, 0);

            // Then
            assertNull(result.getContent());
        }
    }

    @Nested
    @DisplayName("Total Pages Calculation Tests")
    class TotalPagesTests {

        @Test
        @DisplayName("should calculate total pages correctly")
        void shouldCalculateTotalPagesCorrectly() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 25);

            // Then
            assertEquals(3, result.getTotalPages()); // ceil(25/10) = 3
        }

        @Test
        @DisplayName("should return 1 page when total equals size")
        void shouldReturnOnePageWhenTotalEqualsSize() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 10);

            // Then
            assertEquals(1, result.getTotalPages());
        }

        @Test
        @DisplayName("should return 0 pages when size is 0")
        void shouldReturnZeroPagesWhenSizeIsZero() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 0, 10);

            // Then
            assertEquals(0, result.getTotalPages());
        }

        @Test
        @DisplayName("should return 0 pages when no elements")
        void shouldReturnZeroPagesWhenNoElements() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 0);

            // Then
            assertEquals(0, result.getTotalPages());
        }

        @Test
        @DisplayName("should calculate pages for partial last page")
        void shouldCalculatePagesForPartialLastPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 21);

            // Then
            assertEquals(3, result.getTotalPages()); // ceil(21/10) = 3
        }
    }

    @Nested
    @DisplayName("First Page Tests")
    class FirstPageTests {

        @Test
        @DisplayName("should be first page when page is 0")
        void shouldBeFirstPageWhenPageIsZero() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 100);

            // Then
            assertTrue(result.isFirst());
        }

        @Test
        @DisplayName("should not be first page when page is greater than 0")
        void shouldNotBeFirstPageWhenPageIsGreaterThanZero() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 1, 10, 100);

            // Then
            assertFalse(result.isFirst());
        }

        @Test
        @DisplayName("should not be first page on last page")
        void shouldNotBeFirstPageOnLastPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 9, 10, 100);

            // Then
            assertFalse(result.isFirst());
        }
    }

    @Nested
    @DisplayName("Last Page Tests")
    class LastPageTests {

        @Test
        @DisplayName("should be last page when on final page")
        void shouldBeLastPageWhenOnFinalPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 9, 10, 100);

            // Then
            assertTrue(result.isLast());
        }

        @Test
        @DisplayName("should not be last page when not on final page")
        void shouldNotBeLastPageWhenNotOnFinalPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 100);

            // Then
            assertFalse(result.isLast());
        }

        @Test
        @DisplayName("should be last page when total pages is 0")
        void shouldBeLastPageWhenTotalPagesIsZero() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 0);

            // Then
            assertTrue(result.isLast());
        }

        @Test
        @DisplayName("should be last page on single page result")
        void shouldBeLastPageOnSinglePageResult() {
            // When
            PagedResult<String> result = new PagedResult<>(Arrays.asList("a", "b"), 0, 10, 2);

            // Then
            assertTrue(result.isLast());
        }

        @Test
        @DisplayName("should be last page when page exceeds total pages")
        void shouldBeLastPageWhenPageExceedsTotalPages() {
            // When - page 5 when only 3 total pages
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 5, 10, 25);

            // Then
            assertTrue(result.isLast());
        }
    }

    @Nested
    @DisplayName("Has Content Tests")
    class HasContentTests {

        @Test
        @DisplayName("should have content when list is not empty")
        void shouldHaveContentWhenListIsNotEmpty() {
            // When
            PagedResult<String> result = new PagedResult<>(Arrays.asList("item1"), 0, 10, 1);

            // Then
            assertTrue(result.hasContent());
        }

        @Test
        @DisplayName("should not have content when list is empty")
        void shouldNotHaveContentWhenListIsEmpty() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 0);

            // Then
            assertFalse(result.hasContent());
        }

        @Test
        @DisplayName("should not have content when list is null")
        void shouldNotHaveContentWhenListIsNull() {
            // When
            PagedResult<String> result = new PagedResult<>(null, 0, 10, 0);

            // Then
            assertFalse(result.hasContent());
        }
    }

    @Nested
    @DisplayName("Has Next Tests")
    class HasNextTests {

        @Test
        @DisplayName("should have next when not on last page")
        void shouldHaveNextWhenNotOnLastPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 100);

            // Then
            assertTrue(result.hasNext());
        }

        @Test
        @DisplayName("should not have next when on last page")
        void shouldNotHaveNextWhenOnLastPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 9, 10, 100);

            // Then
            assertFalse(result.hasNext());
        }

        @Test
        @DisplayName("should not have next when only one page")
        void shouldNotHaveNextWhenOnlyOnePage() {
            // When
            PagedResult<String> result = new PagedResult<>(Arrays.asList("a"), 0, 10, 1);

            // Then
            assertFalse(result.hasNext());
        }
    }

    @Nested
    @DisplayName("Has Previous Tests")
    class HasPreviousTests {

        @Test
        @DisplayName("should have previous when not on first page")
        void shouldHavePreviousWhenNotOnFirstPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 1, 10, 100);

            // Then
            assertTrue(result.hasPrevious());
        }

        @Test
        @DisplayName("should not have previous when on first page")
        void shouldNotHavePreviousWhenOnFirstPage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 10, 100);

            // Then
            assertFalse(result.hasPrevious());
        }
    }

    @Nested
    @DisplayName("Generic Type Tests")
    class GenericTypeTests {

        @Test
        @DisplayName("should work with Integer type")
        void shouldWorkWithIntegerType() {
            // When
            PagedResult<Integer> result = new PagedResult<>(Arrays.asList(1, 2, 3), 0, 10, 3);

            // Then
            assertEquals(3, result.getContent().size());
            assertEquals(Integer.valueOf(1), result.getContent().get(0));
        }

        @Test
        @DisplayName("should work with custom object type")
        void shouldWorkWithCustomObjectType() {
            // Given
            record TestItem(String name) {}
            List<TestItem> items = Arrays.asList(new TestItem("test1"), new TestItem("test2"));

            // When
            PagedResult<TestItem> result = new PagedResult<>(items, 0, 10, 2);

            // Then
            assertEquals(2, result.getContent().size());
            assertEquals("test1", result.getContent().get(0).name());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("should handle large total elements")
        void shouldHandleLargeTotalElements() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 0, 100, 1000000);

            // Then
            assertEquals(10000, result.getTotalPages());
            assertFalse(result.isLast());
        }

        @Test
        @DisplayName("should handle middle page")
        void shouldHandleMiddlePage() {
            // When
            PagedResult<String> result = new PagedResult<>(Collections.emptyList(), 5, 10, 100);

            // Then
            assertFalse(result.isFirst());
            assertFalse(result.isLast());
            assertTrue(result.hasNext());
            assertTrue(result.hasPrevious());
        }
    }
}
