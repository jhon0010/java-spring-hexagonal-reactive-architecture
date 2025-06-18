package com.crm.validation.lead.application.services.validator;

import com.crm.validation.lead.domain.exceptions.IndependentValidationFailsException;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import reactor.core.publisher.Mono;

/**
 * A functional interface for validators that depend on the outcome of previous validations.
 * The validations marked as dependent must be executed sequentially, as they may rely on the results of previous validations.
 * @param <T>
 */
@FunctionalInterface
public interface DependentValidator<T>  {
    Mono<ValidationOutcome> apply(T target, ValidationOutcome... previous) throws IndependentValidationFailsException;
}
