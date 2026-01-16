package com.insurance.policyholder.domain.model.valueobject;

import com.insurance.policyholder.domain.model.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PersonalInfo Value Object Tests")
class PersonalInfoTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的 PersonalInfo")
        void shouldCreateValidPersonalInfo() {
            PersonalInfo info = PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15));
            assertNotNull(info);
            assertEquals("王小明", info.getName());
            assertEquals(Gender.MALE, info.getGender());
            assertEquals(LocalDate.of(1990, 1, 15), info.getBirthDate());
        }
    }

    @Nested
    @DisplayName("驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("姓名為空應拋出例外")
        void shouldThrowExceptionForNullName() {
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of(null, Gender.MALE, LocalDate.of(1990, 1, 15)));
        }

        @Test
        @DisplayName("姓名空字串應拋出例外")
        void shouldThrowExceptionForEmptyName() {
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of("", Gender.MALE, LocalDate.of(1990, 1, 15)));
        }

        @Test
        @DisplayName("姓名超過50字元應拋出例外")
        void shouldThrowExceptionForNameTooLong() {
            String longName = "A".repeat(51);
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of(longName, Gender.MALE, LocalDate.of(1990, 1, 15)));
        }

        @Test
        @DisplayName("性別為空應拋出例外")
        void shouldThrowExceptionForNullGender() {
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of("王小明", null, LocalDate.of(1990, 1, 15)));
        }

        @Test
        @DisplayName("出生日期為空應拋出例外")
        void shouldThrowExceptionForNullBirthDate() {
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of("王小明", Gender.MALE, null));
        }

        @Test
        @DisplayName("未滿18歲應拋出例外")
        void shouldThrowExceptionForUnder18() {
            LocalDate under18 = LocalDate.now().minusYears(17);
            assertThrows(IllegalArgumentException.class,
                    () -> PersonalInfo.of("王小明", Gender.MALE, under18));
        }

        @Test
        @DisplayName("剛滿18歲應成功建立")
        void shouldCreateForExactly18() {
            LocalDate exactly18 = LocalDate.now().minusYears(18);
            assertDoesNotThrow(() -> PersonalInfo.of("王小明", Gender.MALE, exactly18));
        }
    }

    @Nested
    @DisplayName("年齡計算測試")
    class AgeCalculationTests {

        @Test
        @DisplayName("應正確計算年齡")
        void shouldCalculateAgeCorrectly() {
            LocalDate birthDate = LocalDate.now().minusYears(30);
            PersonalInfo info = PersonalInfo.of("王小明", Gender.MALE, birthDate);
            assertEquals(30, info.getAge());
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同內容應相等")
        void shouldBeEqualForSameContent() {
            PersonalInfo info1 = PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15));
            PersonalInfo info2 = PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15));
            assertEquals(info1, info2);
            assertEquals(info1.hashCode(), info2.hashCode());
        }

        @Test
        @DisplayName("不同內容應不相等")
        void shouldNotBeEqualForDifferentContent() {
            PersonalInfo info1 = PersonalInfo.of("王小明", Gender.MALE, LocalDate.of(1990, 1, 15));
            PersonalInfo info2 = PersonalInfo.of("李小華", Gender.FEMALE, LocalDate.of(1992, 5, 20));
            assertNotEquals(info1, info2);
        }
    }
}
