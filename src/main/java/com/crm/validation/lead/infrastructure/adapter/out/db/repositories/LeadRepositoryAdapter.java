package com.crm.validation.lead.infrastructure.adapter.out.db.repositories;

import com.crm.validation.lead.application.ports.out.db.repositories.LeadRepository;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import com.crm.validation.lead.infrastructure.adapter.out.db.mappers.LeadPersistenceMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository implementation for Lead persistence.
 * This class adapts between the domain-oriented LeadRepository port
 * and the Spring Data R2DBC infrastructure.
 */
@Repository
public class LeadRepositoryAdapter implements LeadRepository {

    private final LeadRepositorySpringData leadRepositorySpringData;
    private final LeadPersistenceMapper mapper;

    public LeadRepositoryAdapter(LeadRepositorySpringData r2dbcRepository, LeadPersistenceMapper mapper) {
        this.leadRepositorySpringData = r2dbcRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Lead> save(Lead lead) {
        return Mono.just(lead)
                .map(mapper::toPersistenceEntity)
                .flatMap(leadRepositorySpringData::save)
                .map(mapper::toDomainEntity);
    }

    @Override
    public Mono<Lead> findById(LeadId leadId) {
        return leadRepositorySpringData.findById(leadId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public Mono<Lead> findByDocumentTypeAndDocumentNumber(DocumentType documentType, Document document) {
        return leadRepositorySpringData.findByDocumentTypeAndDocumentNumber(documentType.name(), document.getNumber())
                .map(mapper::toDomainEntity);
    }

    @Override
    public Flux<Lead> findByState(LeadState state) {
        return leadRepositorySpringData.findByState(state.name())
                .map(mapper::toDomainEntity);
    }

    @Override
    public Mono<Void> deleteById(LeadId id) {
        return leadRepositorySpringData.deleteById(id.getValue());
    }

    @Override
    public Mono<Lead> findByCoreData(Email email, PhoneNumber phoneNumber, Document document) {
        return leadRepositorySpringData.findByCoreData(
                email.getValue(),
                phoneNumber.getValue(),
                document.getNumber()
        ).map(mapper::toDomainEntity);
    }

    @Override
    public Mono<Void> deleteAll() {
        return leadRepositorySpringData.deleteAll();
    }
}
