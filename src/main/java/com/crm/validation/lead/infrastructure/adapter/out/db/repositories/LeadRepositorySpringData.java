package com.crm.validation.lead.infrastructure.adapter.out.db.repositories;

import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadJPAEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface LeadRepositorySpringData extends ReactiveCrudRepository<LeadJPAEntity, UUID> {

    @Query("""
        SELECT * FROM leads
        WHERE email = :email
          AND phone_number = :phoneNumber
          AND document_number = :documentNumber
        LIMIT 1
        """)
    Mono<LeadJPAEntity> findByCoreData(@Param("email") String email, @Param("phoneNumber") String phoneNumber, @Param("documentNumber") Integer documentNumber);

    @Query("""
         UPDATE leads
         SET state = :targetState
         WHERE id    = :id
           AND state <> :targetState
         RETURNING *
         """)
    Mono<LeadJPAEntity> promoteIfNotYet(@Param("id") String id, @Param("targetState") String targetState);

    @Query("SELECT * FROM leads WHERE state = :state")
    Flux<LeadJPAEntity> findByState(@Param("state") String state);

    @Query("""
        SELECT * FROM leads
        WHERE document_type = :documentType
          AND document_number = :documentNumber
        LIMIT 1
        """)
    Mono<LeadJPAEntity> findByDocumentTypeAndDocumentNumber(@Param("documentType") String documentType, @Param("documentNumber") Integer documentNumber);

}