package com.insurance.policyholder.domain.model.valueobject;

import com.insurance.policyholder.domain.model.enums.Gender;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * 個人資訊值物件
 */
public final class PersonalInfo {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_AGE = 18;

    private final String name;
    private final Gender gender;
    private final LocalDate birthDate;

    private PersonalInfo(String name, Gender gender, LocalDate birthDate) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    /**
     * 建立個人資訊
     */
    public static PersonalInfo of(String name, Gender gender, LocalDate birthDate) {
        validate(name, gender, birthDate);
        return new PersonalInfo(name, gender, birthDate);
    }

    private static void validate(String name, Gender gender, LocalDate birthDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("BirthDate cannot be null");
        }

        int age = calculateAge(birthDate);
        if (age < MIN_AGE) {
            throw new IllegalArgumentException("PolicyHolder must be at least " + MIN_AGE + " years old");
        }
    }

    private static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * 計算年齡
     */
    public int getAge() {
        return calculateAge(birthDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalInfo that = (PersonalInfo) o;
        return Objects.equals(name, that.name) &&
                gender == that.gender &&
                Objects.equals(birthDate, that.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, birthDate);
    }

    @Override
    public String toString() {
        return "PersonalInfo{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                '}';
    }
}
