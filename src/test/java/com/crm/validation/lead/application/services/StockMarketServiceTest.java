package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.out.endpoints.StockMarketPort;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.StockCompanyEntity;
import com.crm.validation.lead.infrastructure.adapter.out.db.repositories.StockCompanyRepository;
import com.crm.validation.lead.objectmother.StockObjectMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StockMarketServiceTest {

    @Mock
    private StockMarketPort stockMarketPortMock;
    @Mock
    private StockCompanyRepository stockCompanyRepositoryMock;

    @InjectMocks
    @Autowired
    private StockMarketService stockMarketService;

    @Test
    public void shouldReturnStockSummaryResult() {

        StockCompanyEntity companyEntity = StockObjectMother.bitcoinCompany();
        List<PriceTick> priceTicks = StockObjectMother.bitcoinPriceTicks();

        Mockito.when(stockCompanyRepositoryMock.findByStockSymbol(Mockito.anyString()))
                .thenReturn(Mono.just(companyEntity));

        double averagePrice = priceTicks.stream()
                .mapToDouble(PriceTick::price)
                .average()
                .orElse(0.0);

        StockSummaryResult stockSummaryResult = StockSummaryResult.builder()
                .name(companyEntity.getName())
                .description(companyEntity.getDescription())
                .averagePrice(averagePrice)
                .priceTicks(priceTicks)
                .build();

        Mockito.when(stockMarketPortMock.streamAnalytics()).thenReturn(Flux.fromIterable(priceTicks));

        Flux<StockSummaryResult> stockMarket = stockMarketService.streamAnalytics();

        StepVerifier.create(stockMarket)
                .expectNextMatches(result -> 
                    result.getName().equals("Bitcoin") &&
                    result.getDescription().equals("Digital cryptocurrency") &&
                    result.getAveragePrice() == averagePrice &&
                    result.getPriceTicks().size() == 5
                )
                .verifyComplete();
    }

    @Test
    void shouldProcessMultipleWindows() {
        List<PriceTick> allTicks = new ArrayList<>();
        allTicks.addAll(StockObjectMother.bitcoinPriceTicks());
        allTicks.addAll(StockObjectMother.applePriceTicks());

        Mockito.when(stockCompanyRepositoryMock.findByStockSymbol("BTC"))
                .thenReturn(Mono.just(StockObjectMother.bitcoinCompany()));
        Mockito.when(stockCompanyRepositoryMock.findByStockSymbol("AAPL"))
                .thenReturn(Mono.just(StockObjectMother.appleCompany()));

        Mockito.when(stockMarketPortMock.streamAnalytics()).thenReturn(Flux.fromIterable(allTicks));

        StepVerifier.create(stockMarketService.streamAnalytics())
                .expectNextMatches(result ->
                        result.getName().equals("Bitcoin") &&
                                result.getPriceTicks().size() == 5
                )
                .expectNextMatches(result ->
                        result.getName().equals("Apple Inc.") &&
                                result.getPriceTicks().size() == 5
                )
                .verifyComplete();
    }

    @Test
    void shouldHandleRepositoryError() {
        Mockito.when(stockCompanyRepositoryMock.findByStockSymbol("BTC"))
                .thenReturn(Mono.error(new RuntimeException("Database error")));
        Mockito.when(stockMarketPortMock.streamAnalytics())
                .thenReturn(Flux.fromIterable(StockObjectMother.bitcoinPriceTicks()));

        StepVerifier.create(stockMarketService.streamAnalytics())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandlePortErrorGracefully() {
        Mockito.when(stockMarketPortMock.streamAnalytics())
                .thenReturn(Flux.error(new RuntimeException("External API failed")));

        StepVerifier.create(stockMarketService.streamAnalytics())
                .expectNextCount(0) // No data emitted
                .verifyComplete(); // Stream completes due to onErrorResume
    }

}
