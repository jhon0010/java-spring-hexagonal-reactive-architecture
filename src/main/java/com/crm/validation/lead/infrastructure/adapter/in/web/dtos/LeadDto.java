package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeadDto {
    private final String id;
    private final String name;
    private final LocalDate birthdate;
}
