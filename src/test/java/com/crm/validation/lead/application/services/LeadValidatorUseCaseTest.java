package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.mocks.JudicialRecordsMock;
import com.crm.validation.lead.application.ports.mocks.NationalRegistryMock;
import com.crm.validation.lead.application.ports.mocks.ScoringMock;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.exceptions.LeadAlreadyExistException;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;
import com.crm.validation.lead.infrastructure.adapter.out.db.repositories.LeadRepository;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class LeadValidatorUseCaseTest {

    @Mock
    private LeadRepository leadRepository;

    private LeadValidatorUseCase useCase;
    private LeadDto validLeadDto;

    @BeforeEach
    void setUp() {
        validLeadDto = LeadObjectMother.createValidLeadDto();
    }

    @Test
    @DisplayName("Should promote lead to prospect when all validations pass")
    void shouldPromoteLeadToProspectWhenAllValidationsPass() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        when(leadRepository.findByCoreData(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.empty());

        LeadEntity savedEntity = LeadObjectMother.createProspectEntity();
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.PROSPECT;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not promote lead when judicial records validation fails")
    void shouldNotPromoteLeadWhenJudicialRecordsValidationFails() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(false),
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        LeadEntity savedEntity = LeadObjectMother.createProspectEntity();
        savedEntity.setState(LeadState.REJECTED.name());
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return !validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(LeadEntity.class));
    }

    @Test
    @DisplayName("Should not promote lead when national registry validation fails")
    void shouldNotPromoteLeadWhenNationalRegistryValidationFails() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(false),
                new ScoringMock(true),
                leadRepository
        );

        LeadEntity savedEntity = LeadObjectMother.createProspectEntity();
        savedEntity.setState(LeadState.REJECTED.name());
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return !validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(LeadEntity.class));
    }

    @Test
    @DisplayName("Should not promote lead when scoring validation fails")
    void shouldNotPromoteLeadWhenScoringValidationFails() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(true),
                new ScoringMock(false),
                leadRepository
        );

        LeadEntity savedEntity = LeadObjectMother.createProspectEntity();
        savedEntity.setState(LeadState.REJECTED.name());
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(savedEntity));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return !validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(LeadEntity.class));
    }


    @Test
    @DisplayName("Should update existing lead to prospect when it exists but is not a prospect")
    void shouldUpdateExistingLeadToProspectWhenItExistsButIsNotAProspect() {
        // Given
        LeadEntity existingLead = LeadObjectMother.createProspectEntity();
        existingLead.setState(LeadState.REJECTED.name()); // Existing lead is in REJECTED state

        when(leadRepository.findByCoreData(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(existingLead));

        LeadEntity updatedEntity = LeadObjectMother.createProspectEntity();
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(updatedEntity));

        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.PROSPECT;
                })
                .verifyComplete();

        // Verify the lead was updated
        verify(leadRepository, times(2)).save(any(LeadEntity.class));
    }

    @Test
    @DisplayName("Should save rejected lead to repository when validation fails")
    void shouldSaveRejectedLeadToRepositoryWhenValidationFails() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(false), // This will cause the validation to fail
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        LeadEntity rejectedEntity = LeadObjectMother.createProspectEntity();
        rejectedEntity.setState(LeadState.REJECTED.name());
        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.just(rejectedEntity));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.getLead();
                    return !validationResult.getValidations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the rejected lead was saved
        verify(leadRepository, times(1)).save(any(LeadEntity.class));
    }

    @Test
    @DisplayName("Should handle repository error when saving lead")
    void shouldHandleRepositoryErrorWhenSavingLead() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        when(leadRepository.findByCoreData(anyString(), anyString(), anyInt()))
                .thenReturn(Mono.empty());

        when(leadRepository.save(any(LeadEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLeadDto);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Database error"))
                .verify();
    }
}

