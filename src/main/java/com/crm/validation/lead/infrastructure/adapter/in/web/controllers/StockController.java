package com.crm.validation.lead.infrastructure.adapter.in.web.controllers;

import com.crm.validation.lead.application.ports.in.StockMarket;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class StockController {

    private final StockMarket stockMarket;

    public StockController(StockMarket stockMarket) {
        this.stockMarket = stockMarket;
    }

    @GetMapping(value = "/stream/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockSummaryResult> streamAnalytics() {
        return stockMarket.streamAnalytics();
    }

}
