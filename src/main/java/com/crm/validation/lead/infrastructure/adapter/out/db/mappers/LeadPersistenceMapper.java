package com.crm.validation.lead.infrastructure.adapter.out.db.mappers;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadJPAEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting between Lead domain entities and persistence entities.
 * This isolates the domain model from persistence concerns.
 */
@Mapper()
public interface LeadPersistenceMapper {
    LeadPersistenceMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(LeadPersistenceMapper.class);

    /**
     * Converts a domain entity to a persistence entity.
     * Maps from value objects to primitive types.
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "personalInfo.name", target = "name")
    @Mapping(source = "personalInfo.birthdate", target = "birthdate")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "phoneNumber.value", target = "phoneNumber")
    @Mapping(source = "document.type", target = "documentType")
    @Mapping(source = "document.number", target = "documentNumber")
    @Mapping(source = "state", target = "state", qualifiedByName = "mapStateToString")
    LeadJPAEntity toPersistenceEntity(Lead lead);

    /**
     * Converts a persistence entity to a domain entity.
     * Creates appropriate value objects from primitive types.
     */
    @Mapping(target = "id", expression = "java(LeadId.of(entity.getId()))")
    @Mapping(target = "personalInfo", expression = "java(PersonalInfo.of(entity.getName(), entity.getBirthdate()))")
    @Mapping(target = "email", expression = "java(Email.of(entity.getEmail()))")
    @Mapping(target = "phoneNumber", expression = "java(PhoneNumber.of(entity.getPhoneNumber()))")
    @Mapping(target = "document", expression = "java(Document.of(entity.getDocumentType(), entity.getDocumentNumber()))")
    @Mapping(source = "state", target = "state", qualifiedByName = "mapStringToState")
    Lead toDomainEntity(LeadJPAEntity entity);

    /**
     * Maps a LeadState enum to its string representation.
     */
    @Named("mapStateToString")
    default String mapStateToString(LeadState state) {
        return state != null ? state.name() : null;
    }

    /**
     * Maps a string to a LeadState enum.
     */
    @Named("mapStringToState")
    default LeadState mapStringToState(String state) {
        return state != null ? LeadState.valueOf(state) : null;
    }
}
