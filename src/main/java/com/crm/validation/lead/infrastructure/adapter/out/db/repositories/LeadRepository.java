package com.crm.validation.lead.infrastructure.adapter.out.db.repositories;

import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LeadRepository extends ReactiveCrudRepository<LeadEntity, UUID> {

    @Query("""
        SELECT * FROM leads
        WHERE email = :email
          AND phone_number = :phoneNumber
          AND document_number = :documentNumber
        LIMIT 1
        """)
    Mono<LeadEntity> findByCoreData(@Param("email") String email, @Param("phoneNumber") String phoneNumber, @Param("documentNumber") Integer documentNumber);

    @Query("""
         UPDATE leads
         SET state = :targetState
         WHERE id    = :id
           AND state <> :targetState
         RETURNING *
         """)
    Mono<LeadEntity> promoteIfNotYet(@Param("id") String id, @Param("targetState") String targetState);

}
