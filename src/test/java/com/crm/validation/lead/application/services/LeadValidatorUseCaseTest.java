package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.mocks.JudicialRecordsMock;
import com.crm.validation.lead.application.ports.mocks.NationalRegistryMock;
import com.crm.validation.lead.application.ports.mocks.ScoringMock;
import com.crm.validation.lead.application.ports.out.db.repositories.LeadRepository;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import com.crm.validation.lead.application.services.LeadValidatorUseCase;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.mappers.LeadWebMapper;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadJPAEntity;
import com.crm.validation.lead.infrastructure.adapter.out.db.mappers.LeadPersistenceMapper;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadValidatorUseCaseTest {

    @Mock
    private LeadRepository leadRepository;

    private LeadPersistenceMapper leadPersistenceMapper;

    private LeadValidatorUseCase useCase;
    private Lead validLead;
    private final LeadWebMapper leadWebMapper = LeadWebMapper.INSTANCE;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test to avoid unfinished stubbing issues
        Mockito.reset(leadRepository);

        // Use the static instance of the mapper
        leadPersistenceMapper = LeadPersistenceMapper.INSTANCE;

        validLead = leadWebMapper.leadDtoToLead(LeadObjectMother.createValidLeadDto());
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        LeadJPAEntity prospectEntity = LeadObjectMother.createProspectEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(prospectEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return validationResult.validations().isValid() &&
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        LeadJPAEntity rejectedEntity = LeadObjectMother.createRejectedEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(rejectedEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return !validationResult.validations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(Lead.class));
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        LeadJPAEntity rejectedEntity = LeadObjectMother.createRejectedEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(rejectedEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return !validationResult.validations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(Lead.class));
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        LeadJPAEntity rejectedEntity = LeadObjectMother.createRejectedEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(rejectedEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return !validationResult.validations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the lead was saved with REJECTED state
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should update existing lead to prospect when it exists but is not a prospect")
    void shouldUpdateExistingLeadToProspectWhenItExistsButIsNotAProspect() {
        // Given
        useCase = new LeadValidatorUseCase(
                new JudicialRecordsMock(true),
                new NationalRegistryMock(true),
                new ScoringMock(true),
                leadRepository
        );

        // Setup mocks
        LeadJPAEntity existingLead = LeadObjectMother.createRejectedEntity();
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(existingLead)));

        LeadJPAEntity prospectEntity = LeadObjectMother.createProspectEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(prospectEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return validationResult.validations().isValid() &&
                           lead.getState() == LeadState.PROSPECT;
                })
                .verifyComplete();

        // Verify the lead was updated
        verify(leadRepository, times(1)).save(any(Lead.class));
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        LeadJPAEntity rejectedEntity = LeadObjectMother.createRejectedEntity();
        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.just(leadPersistenceMapper.toDomainEntity(rejectedEntity)));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(validationResult -> {
                    Lead lead = validationResult.lead();
                    return !validationResult.validations().isValid() &&
                           lead.getState() == LeadState.REJECTED;
                })
                .verifyComplete();

        // Verify the rejected lead was saved
        verify(leadRepository, times(1)).save(any(Lead.class));
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

        // Setup mocks
        when(leadRepository.findByCoreData(any(Email.class), any(PhoneNumber.class), any(Document.class)))
                .thenReturn(Mono.empty());

        when(leadRepository.save(any(Lead.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<LeadValidationResult> result = useCase.promoteLeadToProspect(validLead);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Database error"))
                .verify();
    }
}
