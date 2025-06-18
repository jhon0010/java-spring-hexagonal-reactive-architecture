package com.crm.validation.lead.domain.model.valueobjects;

import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import lombok.Value;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 * Encapsulates email validation logic.
 */
@Value
public class Email {
    String value;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Factory method to create a valid Email
     *
     * @param email The email address
     * @return A new Email instance
     * @throws InvalidLeadDataException if validation fails
     */
    public static Email of(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidLeadDataException("Email cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidLeadDataException("Invalid email format");
        }

        return new Email(email);
    }

    /**
     * Private constructor to enforce use of factory method
     */
    private Email(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
