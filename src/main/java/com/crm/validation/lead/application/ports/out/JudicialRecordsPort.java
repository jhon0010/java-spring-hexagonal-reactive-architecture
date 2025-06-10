package com.crm.validation.lead.application.ports.out;

import com.crm.validation.lead.domain.model.Lead;
import reactor.core.publisher.Mono;

public interface JudicialRecordsPort {
    Mono<Boolean> hasCriminalRecord(Lead lead);
}
