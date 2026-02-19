package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.in.StockMarket;
import com.crm.validation.lead.application.ports.out.endpoints.StockMarketPort;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
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

    /**
     * Stream analytics from stock market. giving all require information for real time dashboard.
     * Analyze the data by calculating a Moving Average of the last 5 prices.
     * Resilience: If the database fails, return a default "Unknown" company instead of crashing the stream.
     * @return StockSummaryResult, summary stock information.
     */
    @Override
    public Flux<StockSummaryResult> streamAnalytics() {
        return stockMarketPort.streamAnalytics()
                .onErrorResume(e -> Flux.empty()) // If the stream fails, return an empty stream keep the stream running
                .window(5)
                .flatMap(priceTickFlux -> 
                    priceTickFlux.collectList()
                        .flatMap(priceTicks -> {
                            if (priceTicks.isEmpty()) {
                                return Mono.empty();
                            }
                            
                            double averagePrice = priceTicks.stream()
                                .mapToDouble(PriceTick::price)
                                .average()
                                .orElse(0.0);
                            
                            PriceTick lastPriceTick = priceTicks.get(priceTicks.size() - 1);
                            
                            return stockCompanyRepository.findByStockSymbol(lastPriceTick.symbol())
                                .map(company ->
                                    StockSummaryResult.builder()
                                            .priceTicks(priceTicks)
                                            .averagePrice(averagePrice)
                                            .name(company.getName())
                                            .description(company.getDescription())
                                            .build()
                                )
                                .switchIfEmpty(Mono.fromCallable(() ->
                                    StockSummaryResult.builder()
                                            .priceTicks(priceTicks)
                                            .averagePrice(averagePrice)
                                            .name("Unknown")
                                            .description("Company not found")
                                            .build()
                                ));
                        })
                );
    }
}
