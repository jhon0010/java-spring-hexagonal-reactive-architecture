package com.crm.validation.lead.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.Period;

/**
 * Value Object representing personal information of a Lead.
 * Encapsulates name and birthdate with validation logic.
 */
public record PersonalInfo(String name, LocalDate birthdate) {
    /**
     * Factory method to create a valid PersonalInfo
     *
     * @param name      The person's name
     * @param birthdate The person's birthdate
     * @return A new PersonalInfo instance
     * @throws IllegalArgumentException if validation fails
     */
    public static PersonalInfo of(String name, LocalDate birthdate) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (birthdate == null) {
            throw new IllegalArgumentException("Birthdate cannot be null");
        }

        return new PersonalInfo(name, birthdate);
    }

    /**
     * Calculates the age of the person in years
     *
     * @return Age in years
     */
    public int getAge() {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    /**
     * Checks if the person is an adult (18 years or older)
     *
     * @return true if adult, false otherwise
     */
    public boolean isAdult() {
        return getAge() >= 18;
    }
}
