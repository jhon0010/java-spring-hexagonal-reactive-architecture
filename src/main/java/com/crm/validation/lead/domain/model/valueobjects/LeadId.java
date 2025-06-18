package com.crm.validation.lead.domain.model.valueobjects;

import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import lombok.Value;

import java.util.UUID;

/**
 * Value Object representing a Lead identifier.
 * Encapsulates the ID generation and validation logic.
 */
@Value
public class LeadId {
    UUID value;

    /**
     * Factory method to create a new random LeadId
     *
     * @return A new LeadId with a random UUID
     */
    public static LeadId generate() {
        return new LeadId(UUID.randomUUID());
    }

    /**
     * Factory method to create a LeadId from an existing string
     *
     * @param id The ID string to use
     * @return A new LeadId with the provided value
     * @throws IllegalArgumentException if the ID is invalid
     */
    public static LeadId of(UUID id) throws InvalidLeadDataException {
        if (id == null || id.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Lead ID cannot be empty");
        }
        if (!id.toString().contains("-")) {
            // Try to parse as UUID to validate format
            throw new InvalidLeadDataException("Invalid Lead UUID id format");
        }
        return new LeadId(id);
    }

    /**
     * Private constructor to enforce use of factory methods
     */
    private LeadId(UUID value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
