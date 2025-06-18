package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Log4j2
public class BaseExternalCallSimulator {
    private static final int MIN_DELAY_MS = 40;
    private static final int MAX_DELAY_MS = 300;

    private final Random random = new Random();

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    protected Duration randomDelay() {
        int delay = MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS);
        return Duration.ofMillis(delay);
    }

    private boolean isAdult(LocalDate birthdate) {
        return birthdate != null && birthdate.isBefore(LocalDate.now().minusYears(18));
    }

    /**
     * Simulates a call to a judicial service to check if the lead has a criminal record.
     * @param lead The lead domain entity containing the lead's information.
     * @return true if the lead has a criminal record, false otherwise.
     */
    protected Mono<Boolean> simulateCriminalCheck(Lead lead) {

        return Mono.delay(randomDelay())
                .thenReturn(!this.isEven(lead.getDocumentNumber())
                        && isAdult(lead.getBirthdate())
                );
    }

    /**
     * Simulates a call to a national registry service to check if the lead is present on the national registry.
     * @param lead The lead domain entity containing the lead's information.
     * @return true if the lead is present on the national registry, false otherwise.
     */
    protected Mono<Boolean> simulateNationalRegistryCheck(Lead lead) {
        return Mono.delay(randomDelay())
                .thenReturn(this.isEven(lead.getDocumentNumber())
                        && lead.getDocumentType().contains(DocumentType.CC.name()));
    }

    /**
     * Simulates a scoring service call based on lead and previous validation outcomes.
     * @param lead The lead domain entity.
     * @param previous Previous validation outcomes.
     */
    protected Mono<Double> simulateScoring(Lead lead, ValidationOutcome... previous) {
        var probability = this.isEven(lead.getDocumentNumber()) ? 20.0 : 80.0;

        int invalidPenalty = (int) Arrays.stream(previous)
                .map(ValidationOutcome::validation)
                .filter(validation -> !validation.isValid())
                .count() * 10;

        probability -= invalidPenalty;

        var score = 100 - (random.nextDouble() * probability);
        log.info("SCORE :: for lead {}: {}", lead.getDocumentNumber(), score);
        return Mono.delay(randomDelay())
                .thenReturn(score);
    }

}
