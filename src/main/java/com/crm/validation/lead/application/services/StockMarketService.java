package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.in.StockMarket;
import com.crm.validation.lead.application.ports.out.endpoints.StockMarketPort;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.StockCompanyEntity;
import com.crm.validation.lead.infrastructure.adapter.out.db.repositories.StockCompanyRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockMarketService implements StockMarket {

    private final StockMarketPort stockMarketPort;
    private final StockCompanyRepository stockCompanyRepository;

    public StockMarketService(StockMarketPort stockMarketPort, StockCompanyRepository stockCompanyRepository) {
        this.stockMarketPort = stockMarketPort;
        this.stockCompanyRepository = stockCompanyRepository;
    }

    @Override
    public Flux<StockSummaryResult> streamAnalytics() {
        return stockMarketPort.streamAnalytics()
                .flatMap(priceTick -> 
                    stockCompanyRepository.findByStockSymbol(priceTick.symbol())
                        .map(company -> {
                            StockSummaryResult result = new StockSummaryResult();
                            result.setSymbol(priceTick.symbol());
                            result.setPrice(priceTick.price());
                            result.setName(company.getName());
                            result.setDescription(company.getDescription());
                            return result;
                        })
                        .switchIfEmpty(Mono.fromCallable(() -> {
                            StockSummaryResult result = new StockSummaryResult();
                            result.setSymbol(priceTick.symbol());
                            result.setPrice(priceTick.price());
                            result.setName("Unknown");
                            result.setDescription("Company not found");
                            return result;
                        }))
                );
    }
}
