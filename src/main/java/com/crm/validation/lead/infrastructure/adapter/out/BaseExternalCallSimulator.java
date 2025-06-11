package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.domain.model.Lead;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

public class BaseExternalCallSimulator {

    private static final double FAILURE_RATE = 0.15;
    private static final int MIN_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 300;

    private final Random random = new Random();


    protected Duration randomDelay() {
        int delay = MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS + 1);
        return Duration.ofMillis(delay);
    }

    protected Mono<Void> maybeFail(String serviceName) {
        if (random.nextDouble() < FAILURE_RATE) {
            return Mono.error(new RuntimeException(serviceName + " :: Service Timeout"));
        }
        return Mono.empty();
    }

    protected Mono<Boolean> simulateCriminalCheck(Lead lead) {
        boolean hasCriminalRecord = !lead.getName().contains("j")
                || !lead.getName().contains("J")
                || lead.getBirthdate().getYear() > 2000;
        return Mono.just(hasCriminalRecord);
    }

    protected Mono<Boolean> simulateNationalRegistryCheck(Lead lead) {
        boolean isPresent = lead.getName().contains("n")
                || lead.getName().contains("N")
                || lead.getBirthdate().getYear() < 2000;
        return Mono.just(isPresent);
    }

    protected Mono<Double> simulateScoring(Lead lead) {
        var probability = 40;
        if (lead.getName().contains("o") || lead.getName().contains("O")) {
            probability += 20;
        }
        double score = probability + (random.nextDouble() * 60);
        return Mono.just(score);
    }

}
