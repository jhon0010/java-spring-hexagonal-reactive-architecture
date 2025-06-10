package com.crm.validation.lead.application.ports.out;

import com.crm.validation.lead.domain.model.Lead;
import reactor.core.publisher.Mono;

public interface NationalRegistryPort {
    Mono<Boolean> isPresentOnNationalRegistry(Lead lead);
}
