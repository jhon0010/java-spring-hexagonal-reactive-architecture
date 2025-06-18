package com.crm.validation.lead.domain.model;

import com.crm.validation.lead.domain.model.validator.ValidationResults;
import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid-email", "missing-at-symbol.com", "nodomain.com"})
    @DisplayName("Should throw exception when email is invalid")
    void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
        // Given
        LeadDto invalidLeadDto = LeadObjectMother.createValidLeadDto().toBuilder().email(invalidEmail).build();
        assertThrows(InvalidLeadDataException.class, () -> Lead.create(UUID.randomUUID(),
                invalidLeadDto.name(),
                invalidLeadDto.birthdate(),
                invalidLeadDto.email(),
                invalidLeadDto.phoneNumber(),
                invalidLeadDto.documentType(),
                invalidLeadDto.documentNumber(),
                LeadState.CREATED
                )
        );
    }

    @Test
    @DisplayName("Should promote lead to prospect when validation is successful")
    void shouldPromoteLeadToProspectWhenValidationSuccessful() {
        // Given
        Lead validLead = LeadObjectMother.createValidLead();
        ValidationResults validationResult = LeadObjectMother.createValidValidationResults();

        // When
        var result = validLead.promoteLeadToProspect(validationResult);

        // Then
        assertNotNull(result);
        assertEquals("PROSPECT", result.lead().getState().name());
        assertSame(LeadState.PROSPECT, result.lead().getState());
    }

    @Test
    @DisplayName("Should not promote lead to prospect when validation fails")
    void shouldNotPromoteLeadToProspectWhenValidationFails() {
        // Given
        Lead validLead = LeadObjectMother.createValidLead();
        ValidationResults errorsValidationResults = LeadObjectMother.createErrorsValidationResults();

        // When
        var result = validLead.promoteLeadToProspect(errorsValidationResults);

        // Then
        assertNotNull(result);
        assertEquals(LeadState.PROSPECT.name(), result.lead().getState().name());
    }
}
