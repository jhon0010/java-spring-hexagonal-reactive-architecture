package com.crm.validation.lead.application.services.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple container for validation ERRORS.
 */
public class ValidationResults {
    private final List<String> errors = new ArrayList<>();

    public ValidationResults addError(String msg) {
        errors.add(msg);
        return this;
    }

    public ValidationResults merge(ValidationResults other) {
        errors.addAll(other.errors);
        return this;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getAllErrosInString() {
        return String.join(", ", errors);
    }

    @Override
    public String toString() {
        return this.getAllErrosInString();
    }

}
