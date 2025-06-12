package com.crm.validation.lead.application.services.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs N validators in sequence, accumulates all errors.
 */
public class CompositeValidator<T> implements Validator<T> {
    private final List<Validator<T>> delegates = new ArrayList<>();

    public CompositeValidator<T> add(Validator<T> v) {
        delegates.add(v);
        return this;
    }

    @Override
    public ValidationResult validate(T t) {
        ValidationResult result = new ValidationResult();
        for (Validator<T> v : delegates) {
            result.merge(v.validate(t));
        }
        return result;
    }
}
