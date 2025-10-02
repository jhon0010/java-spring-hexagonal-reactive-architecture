package com.crm.validation.lead.infrastructure.adapter.commons.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.valueobjects.PersonalInfo;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadValidationResultDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.services.validator.LeadDtoDataValidatorService;

@Mapper(
        builder = @Builder(disableBuilder = false),
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {LeadId.class}
)
public interface LeadWebMapper {
    Logger log = LoggerFactory.getLogger(LeadWebMapper.class);
    LeadWebMapper INSTANCE = Mappers.getMapper(LeadWebMapper.class);

    /**
     * Maps a Lead domain entity to a LeadDto.
     * Since the domain uses value objects and the DTO uses primitives,
     * we need to extract the values from the value objects.
     */
    @Mapping(source = "personalInfo.name", target = "name")
    @Mapping(source = "personalInfo.birthdate", target = "birthdate")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "phoneNumber.value", target = "phoneNumber")
    @Mapping(source = "document.type", target = "documentType")
    @Mapping(source = "document.number", target = "documentNumber")
    LeadDto leadToLeadDto(Lead lead);

    /**
     * Maps a LeadDto to a Lead domain entity.
     * This creates all the necessary value objects from the primitive values in the DTO.
     */
    @Mapping(target = "personalInfo", expression = "java(PersonalInfo.of(leadDto.name(), leadDto.birthdate()))")
    @Mapping(target = "email", expression = "java(Email.of(leadDto.email()))")
    @Mapping(target = "phoneNumber", expression = "java(PhoneNumber.of(leadDto.phoneNumber()))")
    @Mapping(target = "document", expression = "java(Document.of(leadDto.documentType(), leadDto.documentNumber()))")
    @Mapping(target = "state", source = ".", qualifiedByName = "determineLeadState")
    Lead leadDtoToLead(LeadDto leadDto);

    /**
     * Custom mapping method to determine the appropriate LeadState
     * This allows for business rules to be applied during mapping
     */
    @Named("determineLeadState")
    default LeadState determineLeadState(LeadDto leadDto) {
        LeadDtoDataValidatorService.ValidationResult validationResult = LeadDtoDataValidatorService.isEmailValid()
                .and(LeadDtoDataValidatorService.isAnAdult())
                .and(LeadDtoDataValidatorService.isPhoneValid())
                .apply(leadDto);
        log.info("Result of the data validation = [{}]", validationResult.toString());

        if (LeadDtoDataValidatorService.ValidationResult.SUCCESS.equals(validationResult)) {
            return LeadState.ON_VALIDATION;
        } else {
            return LeadState.REFUSED;
        }
    }

    /**
     * Creates a Lead with an explicit state, useful for state transitions
     *
     * @param leadDto The source DTO
     * @param state   The explicit state to set
     * @return A Lead domain entity with the specified state
     */
    default Lead leadDtoToLeadWithState(LeadDto leadDto, LeadState state) {
        // Create the lead using the value objects directly to avoid duplicate validation
        return Lead.builder()
                .id(LeadId.generate())
                .personalInfo(PersonalInfo.of(leadDto.name(), leadDto.birthdate()))
                .email(Email.of(leadDto.email()))
                .phoneNumber(PhoneNumber.of(leadDto.phoneNumber()))
                .document(Document.of(leadDto.documentType(), leadDto.documentNumber()))
                .state(state)
                .build();
    }

    /**
     * Maps a LeadValidationResult domain object to a LeadValidationResultDto.
     * This prevents domain objects from crossing the adapter boundary.
     *
     * @param result The domain validation result
     * @return A DTO representing the validation result
     */
    @Mapping(source = "lead.id.value", target = "id")
    @Mapping(source = "lead.personalInfo.name", target = "name")
    @Mapping(source = "lead.personalInfo.birthdate", target = "birthdate")
    @Mapping(source = "lead.email.value", target = "email")
    @Mapping(source = "lead.phoneNumber.value", target = "phoneNumber")
    @Mapping(source = "lead.document.type", target = "documentType")
    @Mapping(source = "lead.document.number", target = "documentNumber")
    @Mapping(source = "lead.state", target = "state", qualifiedByName = "leadStateToString")
    @Mapping(source = "validations.valid", target = "isValid")
    @Mapping(source = "validations.errors", target = "validationErrors")
    LeadValidationResultDto leadValidationResultToDto(LeadValidationResult result);

    /**
     * Convert LeadState enum to String for the DTO
     */
    @Named("leadStateToString")
    default String leadStateToString(LeadState state) {
        return state != null ? state.name() : null;
    }
}