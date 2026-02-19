package com.crm.validation.lead.infrastructure.adapter.out.db.repositories;

import com.crm.validation.lead.domain.model.lead.Lead;
import com.crm.validation.lead.domain.model.lead.enums.DocumentType;
import com.crm.validation.lead.domain.model.lead.enums.LeadState;
import com.crm.validation.lead.domain.model.lead.valueobjects.Document;
import com.crm.validation.lead.domain.model.lead.valueobjects.Email;
import com.crm.validation.lead.domain.model.lead.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.lead.valueobjects.PersonalInfo;
import com.crm.validation.lead.domain.model.lead.valueobjects.PhoneNumber;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadRepositoryAdapterTest {

    @Mock
    private LeadRepositorySpringData leadRepositorySpringData;

    private LeadRepositoryAdapter leadRepositoryAdapter;

    @BeforeEach
    void setUp() {
        leadRepositoryAdapter = new LeadRepositoryAdapter(leadRepositorySpringData);
    }

    @Test
    void shouldSaveLead() {
        // Given
        Lead leadToSave = LeadObjectMother.createValidLead();
        LeadEntity savedEntity = LeadObjectMother.createEntityFromLead(leadToSave);
        
        when(leadRepositorySpringData.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<Lead> result = leadRepositoryAdapter.save(leadToSave);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(savedLead ->
                    savedLead.getEmail().equals(leadToSave.getEmail()) &&
                    savedLead.getState().equals(leadToSave.getState())
                )
                .verifyComplete();
    }

    @Test
    void shouldFindLeadById() {
        // Given
        UUID leadId = UUID.randomUUID();
        LeadEntity leadEntity = LeadObjectMother.createLeadEntity();
        leadEntity.setId(leadId);
        
        when(leadRepositorySpringData.findById(leadId))
                .thenReturn(Mono.just(leadEntity));

        // When
        Mono<Lead> result = leadRepositoryAdapter.findById(LeadId.of(leadId));

        // Then
        StepVerifier.create(result)
                .expectNextMatches(lead -> 
                    lead.getId().getValue().equals(leadId) &&
                    lead.getEmail().getValue().equals(leadEntity.getEmail()) &&
                    lead.getState().name().equals(leadEntity.getState())
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFoundById() {
        // Given
        UUID leadId = UUID.randomUUID();
        when(leadRepositorySpringData.findById(leadId))
                .thenReturn(Mono.empty());

        // When
        Mono<Lead> result = leadRepositoryAdapter.findById(LeadId.of(leadId));

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFindLeadByDocumentTypeAndDocumentNumber() {
        // Given
        DocumentType documentType = DocumentType.CC;
        int documentNumber = 1234;
        LeadEntity leadEntity = LeadObjectMother.createLeadEntity();
        
        when(leadRepositorySpringData.findByDocumentTypeAndDocumentNumber(
                documentType.name(), documentNumber))
                .thenReturn(Mono.just(leadEntity));

        // When
        Mono<Lead> result = leadRepositoryAdapter.findByDocumentTypeAndDocumentNumber(
                documentType, Document.of(documentType.name(), documentNumber));

        // Then
        StepVerifier.create(result)
                .expectNextMatches(lead -> 
                    lead.getDocument().getType().equals(documentType.name()) &&
                    lead.getDocument().getNumber() == documentNumber
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFoundByDocument() {
        // Given
        DocumentType documentType = DocumentType.CC;
        int documentNumber = 999999;
        
        when(leadRepositorySpringData.findByDocumentTypeAndDocumentNumber(
                documentType.name(), documentNumber))
                .thenReturn(Mono.empty());

        // When
        Mono<Lead> result = leadRepositoryAdapter.findByDocumentTypeAndDocumentNumber(
                documentType, Document.of(documentType.name(), documentNumber));

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldFindLeadsByState() {
        // Given
        LeadState state = LeadState.CREATED;
        LeadEntity entity1 = LeadObjectMother.createLeadEntity();
        LeadEntity entity2 = LeadObjectMother.createLeadEntity();
        entity2.setId(UUID.randomUUID());
        
        when(leadRepositorySpringData.findByState(state.name()))
                .thenReturn(Flux.just(entity1, entity2));

        // When
        Flux<Lead> result = leadRepositoryAdapter.findByState(state);

        // Then
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyFluxWhenNoLeadsFoundByState() {
        // Given
        LeadState state = LeadState.PROSPECT;
        
        when(leadRepositorySpringData.findByState(state.name()))
                .thenReturn(Flux.empty());

        // When
        Flux<Lead> result = leadRepositoryAdapter.findByState(state);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldDeleteLeadById() {
        // Given
        UUID leadId = UUID.randomUUID();
        
        when(leadRepositorySpringData.deleteById(leadId))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = leadRepositoryAdapter.deleteById(LeadId.of(leadId));

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenLeadNotFoundByCoreData() {
        // Given
        String email = "nonexistent@example.com";
        String phoneNumber = "+9999999999";
        int documentNumber = 999999;
        
        when(leadRepositorySpringData.findByCoreData(email, phoneNumber, documentNumber))
                .thenReturn(Mono.empty());

        // When
        Mono<Lead> result = leadRepositoryAdapter.findByCoreData(
                Email.of(email), 
                PhoneNumber.of(phoneNumber), 
                Document.of(DocumentType.CC.name(), documentNumber)
        );

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldDeleteAllLeads() {
        // Given
        when(leadRepositorySpringData.deleteAll())
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = leadRepositoryAdapter.deleteAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldHandleSaveError() {
        // Given
        Lead leadToSave = LeadObjectMother.createValidLead();
        
        when(leadRepositorySpringData.save(any(LeadEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<Lead> result = leadRepositoryAdapter.save(leadToSave);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleFindByIdError() {
        // Given
        UUID leadId = UUID.randomUUID();
        
        when(leadRepositorySpringData.findById(leadId))
                .thenReturn(Mono.error(new RuntimeException("Connection error")));

        // When
        Mono<Lead> result = leadRepositoryAdapter.findById(LeadId.of(leadId));

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleFindByStateError() {
        // Given
        LeadState state = LeadState.CREATED;
        
        when(leadRepositorySpringData.findByState(state.name()))
                .thenReturn(Flux.error(new RuntimeException("Query error")));

        // When
        Flux<Lead> result = leadRepositoryAdapter.findByState(state);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
