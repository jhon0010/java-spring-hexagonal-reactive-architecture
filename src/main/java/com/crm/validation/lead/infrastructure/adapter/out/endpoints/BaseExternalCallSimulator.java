package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.services.validator.ValidationOutcome;
import com.crm.validation.lead.domain.exceptions.IndependentValidationFailsException;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Log4j2
public class BaseExternalCallSimulator {

    private static final double FAILURE_RATE = 0.15;
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
     *
     * Check if the lead is an adult and if the document number is odd.
     *
     * @param leadDto The lead data transfer object containing the lead's information.
     * @return true if the lead has a criminal record, false otherwise.
     */
    protected Mono<Boolean> simulateCriminalCheck(LeadDto leadDto) {

        return Mono.delay(randomDelay())
                .thenReturn(!this.isEven(leadDto.documentNumber())
                        && isAdult(leadDto.birthdate())
                );
    }

    /**
     * Simulates a call to a national registry service to check if the lead is present on the national registry.
     * @param leadDto
     * @return true if the lead is present on the national registry, false otherwise.
     */
    protected Mono<Boolean> simulateNationalRegistryCheck(LeadDto leadDto) {
        return Mono.delay(randomDelay())
                .thenReturn(this.isEven(leadDto.documentNumber())
                        && leadDto.documentType().contains(DocumentType.CC.name()));
    }

    protected Mono<Double> simulateScoring(LeadDto leadDto, ValidationOutcome... previous) {
        var probability = this.isEven(leadDto.documentNumber()) ? 20.0 : 80.0;

        int invalidPenalty = (int) Arrays.stream(previous)
                .map(ValidationOutcome::validation)
                .filter(validation -> !validation.isValid())
                .count() * 10;

        probability = probability - invalidPenalty;

        var score = 100 - (random.nextDouble() * probability);
        log.info("SCORE :: for lead {}: {}", leadDto.documentNumber(), score);
        return Mono.delay(randomDelay())
                .thenReturn(score);
    }

}
