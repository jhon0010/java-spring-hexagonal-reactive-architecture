package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.crm.validation.lead.objectmother.LeadObjectMother.createValidValidationOutcome;

@SpringBootTest
class ExternalAdapterIntegrationTest {

    @Autowired
    private JudicialRecordsAdapter judicialRecordsAdapter;

    @Autowired
    private NationalRegistryAdapter nationalRegistryAdapter;

    @Autowired
    private ScoringAdapter scoringAdapter;

    @Test
    @DisplayName("Judicial Records adapter should return valid result for leads without criminal records")
    void judicialRecordsAdapterShouldReturnValidResultForLeadsWithoutCriminalRecords() {
        // Given
        Lead lead = LeadObjectMother.createValidLead();

        // When - Directly access the simulation method to verify it works
        Mono<Boolean> simulationResult = judicialRecordsAdapter.simulateCriminalCheck(lead);

        // Then - The simulation should complete with a value
        StepVerifier.create(simulationResult)
                .expectNextMatches(hasCriminalRecord -> !hasCriminalRecord)
                .verifyComplete();
    }

    @Test
    @DisplayName("Judicial Records adapter should handle leads with criminal records")
    void judicialRecordsAdapterShouldHandleLeadsWithCriminalRecords() {
        // Given - Special document number that would trigger a criminal record match
        // (assuming the simulator recognizes certain patterns as "bad")
        Lead leadWithCriminalRecord = LeadObjectMother.createInvalidLead();

        // When
        var result = judicialRecordsAdapter.apply(leadWithCriminalRecord);

        // Then - If it emits a value, it should be invalid
        StepVerifier.create(result)
                .expectNextMatches(outcome -> !outcome.validation().isValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("National Registry adapter should return validation result")
    void nationalRegistryAdapterShouldReturnValidationResult() {
        // Given
        Lead lead = LeadObjectMother.createValidLead();

        // When
        var result = nationalRegistryAdapter.apply(lead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(outcome -> outcome.validation().isValid())
                .verifyComplete();
    }

    @Test
    @DisplayName("Scoring adapter should return validation result when previous validations pass")
    void scoringAdapterShouldReturnValidationResultWhenPreviousValidationsPass() {
        // Given
        Lead lead = LeadObjectMother.createValidLead();
        ValidationOutcome outcome = createValidValidationOutcome();

        // When
        var result = scoringAdapter.apply(lead, outcome);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(out -> out.validation().isValid())
                .verifyComplete();
    }

}

