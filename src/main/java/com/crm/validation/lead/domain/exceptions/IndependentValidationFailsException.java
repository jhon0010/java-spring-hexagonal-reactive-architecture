package com.crm.validation.lead.domain.exceptions;

/**
 * Exception thrown when the lead independent validation fails.
 * Throw it after validate the previous independent validations and before to call the dependant one in case on failure.
 */
public class IndependentValidationFailsException extends RuntimeException {
    public IndependentValidationFailsException(String message) {
        super(message);
    }
}
