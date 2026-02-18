package com.crm.validation.lead.infrastructure.adapter.out.db.repositories;

import com.crm.validation.lead.infrastructure.adapter.out.db.entities.StockCompanyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StockCompanyRepository extends ReactiveCrudRepository<StockCompanyEntity, Long> {
    Mono<StockCompanyEntity> findByStockSymbol(String stockSymbol);
}
