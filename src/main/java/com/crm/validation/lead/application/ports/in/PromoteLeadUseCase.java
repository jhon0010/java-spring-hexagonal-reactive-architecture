package com.crm.validation.lead.application.ports.in;

import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import reactor.core.publisher.Mono;

/**
 * Input port for promoting a lead to prospect.
 *
 * This interface defines the primary use case of the application
 * and serves as a port for the primary adapter (web controller)
 * to interact with the application core.
 */
public interface PromoteLeadUseCase {

    /**
     * Promotes a lead to prospect if it passes all validations.
     *
     * @param lead The lead to be promoted
     * @return A Mono containing the validation result with the updated lead state
     */
    Mono<LeadValidationResult> promoteLeadToProspect(Lead lead);
}
