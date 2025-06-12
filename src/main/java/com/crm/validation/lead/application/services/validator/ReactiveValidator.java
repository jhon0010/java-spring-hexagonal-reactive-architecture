package com.crm.validation.lead.application.services.validator;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveValidator<T> {

    /**
     * Validate t; return a ValidationResult (never null).
     */
    Mono<ValidationResult> validate(T t);
}
