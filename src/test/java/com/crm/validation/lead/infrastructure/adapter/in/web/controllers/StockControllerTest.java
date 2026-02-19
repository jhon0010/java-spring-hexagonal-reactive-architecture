package com.crm.validation.lead.infrastructure.adapter.in.web.controllers;

import com.crm.validation.lead.application.ports.in.StockMarket;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

@WebFluxTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private StockMarket stockMarketServiceMock;

    @Test
    void shouldReturnStockSummary() {

        StockSummaryResult stockSummaryResult = StockSummaryResult.builder()
                .name("AAPL")
                .description("Apple Inc.")
                .averagePrice(150.0)
                .priceTicks(List.of(
                        new PriceTick("AAPL", 150.0)
                ))
                .build();

        Mockito.when(stockMarketServiceMock.streamAnalytics()).thenReturn(Flux.just(
                stockSummaryResult
        ));
        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockSummaryResult.class)
                .contains(stockSummaryResult)
                .hasSize(1);
    }

    @Test
    void shouldReturnEmptyStreamWhenNoDataAvailable() {
        Mockito.when(stockMarketServiceMock.streamAnalytics()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockSummaryResult.class)
                .hasSize(0);
    }

    @Test
    void shouldReturnOkWithErrorDescriptionWhenServiceThrowsException() {
        Mockito.when(stockMarketServiceMock.streamAnalytics())
                .thenReturn(Flux.error(new RuntimeException("Service unavailable")));

        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("success").isEqualTo("false")
                .jsonPath("error.code").isEqualTo("INTERNAL_ERROR");
    }

    @Test
    void shouldReturnMultipleStockSummaries() {
        StockSummaryResult appleStock = StockSummaryResult.builder()
                .name("AAPL")
                .description("Apple Inc.")
                .averagePrice(150.0)
                .priceTicks(List.of(new PriceTick("AAPL", 150.0)))
                .build();

        StockSummaryResult googleStock = StockSummaryResult.builder()
                .name("GOOGL")
                .description("Alphabet Inc.")
                .averagePrice(2500.0)
                .priceTicks(List.of(new PriceTick("GOOGL", 2500.0)))
                .build();

        Mockito.when(stockMarketServiceMock.streamAnalytics()).thenReturn(Flux.just(
                appleStock, googleStock
        ));

        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockSummaryResult.class)
                .hasSize(2)
                .contains(appleStock, googleStock);
    }

    @Test
    void shouldReturnTextEventStreamContentType() {
        Mockito.when(stockMarketServiceMock.streamAnalytics()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/event-stream; charset=UTF-8");
    }

    /**
     * SSE Format: Your endpoint returns text/event-stream, not JSON
     */
    @Test
    void shouldReturnValidJsonStructure() {
        StockSummaryResult stockSummary = StockSummaryResult.builder()
                .name("AAPL")
                .description("Apple Inc.")
                .averagePrice(150.0)
                .priceTicks(List.of(new PriceTick("AAPL", 150.0)))
                .build();

        Mockito.when(stockMarketServiceMock.streamAnalytics()).thenReturn(Flux.just(stockSummary));

        webTestClient.get()
                .uri("/stream/analytics")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockSummaryResult.class)// to deserialize the stream
                .hasSize(1)
                .value(results -> {
                    StockSummaryResult result = results.get(0);
                    assert "AAPL".equals(result.getName());
                    assert "Apple Inc.".equals(result.getDescription());
                    assert result.getAveragePrice() == 150.0;
                    assert result.getPriceTicks().get(0).symbol().equals("AAPL");
                    assert result.getPriceTicks().get(0).price() == 150.0;
                });
    }

}
