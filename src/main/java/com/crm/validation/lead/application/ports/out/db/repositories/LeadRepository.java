package com.crm.validation.lead.application.ports.out.db.repositories;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for Lead aggregate root.
 *
 * This interface serves as an output port in the Hexagonal Architecture,
 * defining operations that the application layer can use to persist and
 * retrieve Lead entities without knowledge of the actual persistence mechanism.
 *
 * Following DDD principles, this repository speaks in domain terms and
 * operates on domain entities rather than persistence-specific constructs.
 */
public interface LeadRepository {

    /**
     * Saves a Lead entity to the repository.
     *
     * @param lead The Lead entity to save
     * @return A Mono with the saved Lead entity
     */
    Mono<Lead> save(Lead lead);

    /**
     * Finds a Lead entity by its unique identifier.
     *
     * @param id The Lead's unique identifier
     * @return A Mono that completes with the Lead if found, or empty if not found
     */
    Mono<Lead> findById(LeadId id);

    /**
     * Finds a Lead entity by document information.
     * This is a domain-specific query that helps enforce business rules
     * around unique document constraints.
     *
     * @param documentType The type of document
     * @param document The document number
     * @return A Mono that completes with the Lead if found, or empty if not found
     */
    Mono<Lead> findByDocumentTypeAndDocumentNumber(DocumentType documentType, Document document);

    /**
     * Retrieves all Leads in a specific state.
     *
     * @param state The state to filter by
     * @return A Flux of Lead entities in the specified state
     */
    Flux<Lead> findByState(LeadState state);

    /**
     * Deletes a Lead entity from the repository.
     *
     * @param id The ID of the Lead to delete
     * @return A Mono that completes when the operation is done
     */
    Mono<Void> deleteById(LeadId id);

    Mono<Lead> findByCoreData(Email email, PhoneNumber phoneNumber, Document document);

    Mono<Void> deleteAll();
}
