package com.crm.validation.lead.application.services.validator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs N validators in sequence, accumulates all errors.
 */
public class CompositeValidator<T> implements ReactiveValidator<T> {
    private final List<ReactiveValidator<T>> delegates = new ArrayList<>();

    public CompositeValidator<T> add(ReactiveValidator<T> v) {
        delegates.add(v);
        return this;
    }

    @Override
    public Mono<ValidationResult> validate(T t) {
        return Flux.fromIterable(delegates)
                .flatMap(v -> v.validate(t))
                .reduce(new ValidationResult(), ValidationResult::merge);
    }
}
