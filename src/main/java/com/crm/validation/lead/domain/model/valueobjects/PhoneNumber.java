package com.crm.validation.lead.domain.model.valueobjects;

import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object representing a phone number.
 * Encapsulates phone number validation and formatting.
 */
@Value
public class PhoneNumber {
    String value;

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    /**
     * Factory method to create a valid PhoneNumber
     *
     * @param phoneNumber The phone number string
     * @return A new PhoneNumber instance
     * @throws IllegalArgumentException if validation fails
     */
    public static PhoneNumber of(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        String normalized = phoneNumber.replaceAll("[\\s-()]", "");

        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        return new PhoneNumber(normalized);
    }

    /**
     * Private constructor to enforce use of factory method
     */
    private PhoneNumber(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
