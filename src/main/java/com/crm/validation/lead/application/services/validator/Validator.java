package com.crm.validation.lead.application.services.validator;

@FunctionalInterface
public interface Validator<T> {
    /**
     * Validate t; return a ValidationResult (never null).
     */
    ValidationResult validate(T t);
}
