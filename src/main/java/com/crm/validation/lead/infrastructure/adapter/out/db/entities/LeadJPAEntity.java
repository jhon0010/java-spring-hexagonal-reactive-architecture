package com.crm.validation.lead.infrastructure.adapter.out.db.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@Table(name = "leads")
public class LeadJPAEntity {
    @Id
    private UUID id;
    private String name;
    private LocalDate birthdate;
    private String state;
    @Column()
    private String email;
    @Column("phone_number")
    private String phoneNumber;
    @Column("document_type")
    private String documentType;
    @Column("document_number")
    private int documentNumber;
}
