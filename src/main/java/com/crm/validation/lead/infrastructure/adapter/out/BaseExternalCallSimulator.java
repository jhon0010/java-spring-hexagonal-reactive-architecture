package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

public class BaseExternalCallSimulator {

    private static final double FAILURE_RATE = 0.15;
    private static final int MIN_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 300;

    private final Random random = new Random();

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

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

    /**
     * Simulates a call to a judicial service to check if the lead has a criminal record.
     * @param leadDto
     * @return true if the lead has a criminal record, false otherwise.
     */
    protected Boolean simulateCriminalCheck(LeadDto leadDto) {
        return !this.isEven(leadDto.documentNumber());
    }

    /**
     * Simulates a call to a national registry service to check if the lead is present on the national registry.
     * @param leadDto
     * @return true if the lead is present on the national registry, false otherwise.
     */
    protected Boolean simulateNationalRegistryCheck(LeadDto leadDto) {
        return this.isEven(leadDto.documentNumber());
    }

    protected Double simulateScoring(LeadDto leadDto) {
        var probability = this.isEven(leadDto.documentNumber()) ? 70.0 : 30.0;
        return (random.nextDouble() * 100) - probability;
    }

}
