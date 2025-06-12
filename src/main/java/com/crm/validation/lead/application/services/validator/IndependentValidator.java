package com.crm.validation.lead.application.services.validator;

import reactor.core.publisher.Mono;

/**
 * A functional interface for validators that do not depend on the outcome of previous validations.
 * The validations marked as independent can be executed in parallel.
 * @param <T>
 */
@FunctionalInterface
public interface IndependentValidator<T> {
    Mono<ValidationOutcome> apply(T t);
}
