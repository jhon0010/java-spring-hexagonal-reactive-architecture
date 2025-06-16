package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record LeadDto(String name, LocalDate birthdate, String email, String phoneNumber,
                      String documentType, int documentNumber) {}
