package com.crm.validation.lead.domain.model;

import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.validator.LeadDataValidatorService;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.domain.mappers.LeadMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

/**
 * TODO: Make sure it is immutable, and implement idempotence.
 */
@Log4j2
@Builder
@Data
public class Lead {
    private final String id;
    private final String name;
    private final LocalDate birthdate;
    private final LeadState state;
    private final String email;
    private final String phoneNumber;
    private final String documentType;
    private final int documentNumber;

    private static final double SCORE_THRESHOLD = 60;

    /**
     * Validates if a lead dto has a valid data set and returns a Lead object from it in CREATED state.
     * @param leadDto
     * @return Lead object if the data is valid.
     * @throws InvalidLeadDataException if the data is not valid.
     */
    public static Lead fromDto(LeadDto leadDto) throws InvalidLeadDataException {

        LeadDataValidatorService.ValidationResult validationResult = LeadDataValidatorService.isEmailValid()
                .and(LeadDataValidatorService.isAnAdult())
                .and(LeadDataValidatorService.isPhoneValid())
                .apply(leadDto);
        log.info("Result of the data validation = [{}]", validationResult.toString());

        if (LeadDataValidatorService.ValidationResult.SUCCESS.equals(validationResult)) {
            log.info("Lead data is valid, proceeding to create Lead object.");
            return LeadMapper.dtoToDomain(leadDto);
        } else {
            log.error("Lead data validation failed: {}", validationResult);
            throw new InvalidLeadDataException("Invalid lead data: " + validationResult);
        }
    }

    public static LeadValidationResult promoteLeadToProspect(LeadDto leadDto, ValidationResults result) {

        Lead lead = fromDto(leadDto);
        log.info("Starting validations in order to promote a lead {} to {}", leadDto, LeadState.PROSPECT);
        if (!result.isValid()) {
            log.warn("Validation failed, the prospect will be rejected:");
            result.getErrors().forEach(err -> log.error("  • {}",err));
            return LeadValidationResult
                    .builder()
                    .lead(LeadMapper.changeState(lead, LeadState.REJECTED))
                    .validations(result)
                    .build();
        }
        log.info("CONGRATULATIONS !!! , The lead {} will be PROMOTED to {}", leadDto.documentNumber() ,LeadState.PROSPECT);
        return LeadValidationResult
                .builder()
                .lead(LeadMapper.changeState(lead, LeadState.PROSPECT))
                .validations(result)
                .build();
    }

    public static boolean isScoreEnoughToPromoteToProspect(double score){
        return score >= SCORE_THRESHOLD;
    }

}
