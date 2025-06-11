package com.crm.validation.lead.domain.exceptions;

/**
 * Exception thrown when the lead data is invalid.
 * This exception is used to indicate that the lead data does not meet the required validation criteria.
 */
public class InvalidLeadDataException extends RuntimeException {

    public InvalidLeadDataException(String message) {
        super(message);
    }
}
