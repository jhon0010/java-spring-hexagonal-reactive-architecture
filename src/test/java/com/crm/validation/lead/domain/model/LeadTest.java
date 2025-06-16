package com.crm.validation.lead.domain.model;

import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    @Test
    @DisplayName("Should create a valid Lead from LeadDto")
    void shouldCreateValidLeadFromDto() {
        // Given
        LeadDto validLeadDto = LeadObjectMother.createValidLeadDto();

        // When
        Lead lead = Lead.fromDto(validLeadDto);

        // Then
        assertNotNull(lead);
        assertEquals(validLeadDto.name(), lead.getName());
        assertEquals(validLeadDto.email(), lead.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid-email", "missing-at-symbol.com", "nodomain.com"})
    @DisplayName("Should throw exception when email is invalid")
    void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
        // Given
        LeadDto invalidLeadDto = LeadObjectMother.createValidLeadDto().toBuilder().email(invalidEmail).build();
        assertThrows(InvalidLeadDataException.class, () -> Lead.fromDto(invalidLeadDto));
    }

    @Test
    @DisplayName("Should promote lead to prospect when validation is successful")
    void shouldPromoteLeadToProspectWhenValidationSuccessful() {
        // Given
        LeadDto validLeadDto = LeadObjectMother.createValidLeadDto();
        ValidationResults validationResult = LeadObjectMother.createValidValidationResults();

        // When
        var result = Lead.promoteLeadToProspect(validLeadDto, validationResult);

        // Then
        assertNotNull(result);
        assertEquals("PROSPECT", result.getLead().getState().name());
        assertSame(LeadState.PROSPECT, result.getLead().getState());
    }

    @Test
    @DisplayName("Should not promote lead to prospect when validation fails")
    void shouldNotPromoteLeadToProspectWhenValidationFails() {
        // Given
        LeadDto validLeadDto = LeadObjectMother.createValidLeadDto();
        ValidationResults errorsValidationResults = LeadObjectMother.createErrorsValidationResults();

        // When
        var result = Lead.promoteLeadToProspect(validLeadDto, errorsValidationResults);

        // Then
        assertNotNull(result);
        assertEquals(LeadState.PROSPECT.name(), result.getLead().getState().name());
    }
}
