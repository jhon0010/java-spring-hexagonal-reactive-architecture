package com.crm.validation.lead.application.ports.in;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.StockSummaryResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockMarket {
    Flux<StockSummaryResult> streamAnalytics();
}
