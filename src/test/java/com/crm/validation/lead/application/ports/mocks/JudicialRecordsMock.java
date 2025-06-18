package com.crm.validation.lead.application.ports.mocks;

import com.crm.validation.lead.application.ports.out.endpoints.JudicialRecordsPort;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import reactor.core.publisher.Mono;

public class JudicialRecordsMock implements JudicialRecordsPort {

    private final boolean shouldPass;

    public JudicialRecordsMock(boolean shouldPass) {
        this.shouldPass = shouldPass;
    }

    @Override
    public Mono<ValidationOutcome> apply(Lead lead) {
        if (shouldPass) {
            return Mono.just(LeadObjectMother.createValidValidationOutcome());
        } else {
            return Mono.just(LeadObjectMother.createInvalidValidationOutcome("The lead has judicial records"));
        }
    }
}
