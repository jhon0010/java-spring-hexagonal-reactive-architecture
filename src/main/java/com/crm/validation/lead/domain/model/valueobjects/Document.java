package com.crm.validation.lead.domain.model.valueobjects;

import lombok.Value;

/**
 * Value Object representing an identification document.
 * Encapsulates document type and number with validation logic.
 */
@Value
public class Document {
    String type;
    int number;

    /**
     * Factory method to create a valid Document
     *
     * @param type The document type (e.g., "passport", "national_id")
     * @param number The document number
     * @return A new Document instance
     * @throws IllegalArgumentException if validation fails
     */
    public static Document of(String type, int number) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Document type cannot be empty");
        }

        if (number <= 0) {
            throw new IllegalArgumentException("Document number must be positive");
        }

        return new Document(type, number);
    }

    /**
     * Private constructor to enforce use of factory method
     */
    private Document(String type, int number) {
        this.type = type;
        this.number = number;
    }

    /**
     * Returns a string representation of the document
     *
     * @return A formatted string with type and number
     */
    @Override
    public String toString() {
        return type + ": " + number;
    }
}
