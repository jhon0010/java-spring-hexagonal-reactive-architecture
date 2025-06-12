package com.crm.validation.lead.application.services.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public ValidationResult addError(String msg) {
        errors.add(msg);
        return this;
    }

    public ValidationResult merge(ValidationResult other) {
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
