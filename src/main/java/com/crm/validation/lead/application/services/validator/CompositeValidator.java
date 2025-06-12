package com.crm.validation.lead.application.services.validator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs N validators in sequence, accumulates all errors.
 */
public class CompositeValidator<T> implements IndependentValidator<T> {

    private final List<IndependentValidator<T>> independentRules = new ArrayList<>();
    private final List<DependentValidator<T>> dependentRules = new ArrayList<>();


    public CompositeValidator<T> addIndependent(IndependentValidator<T> r) {
        independentRules.add(r);
        return this;
    }

    public CompositeValidator<T> addDependent(DependentValidator<T> r) {
        dependentRules.add(r);
        return this;
    }

    @Override
    public Mono<ValidationOutcome> apply(T target) {

        Mono<List<ValidationOutcome>> phase0 = Flux.merge(
                        independentRules
                                .stream()
                                .map(r -> r.apply(target))
                                .toList())
                .collectList();

        Mono<List<ValidationOutcome>> phase1 = phase0.flatMap(outcomes ->
                Flux.merge(
                                dependentRules
                                        .stream()
                                        .map(r -> r.apply(target, outcomes.toArray(ValidationOutcome[]::new)))
                                        .toList())
                        .collectList()
        );

        return Mono.zip(phase0, phase1)
                .map(t -> mergeAll(t.getT1(), t.getT2()));
    }

    /**
     * Just merges all ValidationResults into a single ValidationOutcome.
     * The payload values are not included just needed for intermediate processing.
     * @param lists the ValidationOutcome lists to merge
     * @return a single ValidationOutcome containing all validation results
     */
    private ValidationOutcome mergeAll(List<ValidationOutcome>... lists) {
        ValidationResults allValidationResults = new ValidationResults();

        for (List<ValidationOutcome> o : lists) {
            o.forEach(vr -> allValidationResults.merge(vr.validation()));
        }
        return ValidationOutcome.builder()
                .validation(allValidationResults)
                .build();
    }
}
