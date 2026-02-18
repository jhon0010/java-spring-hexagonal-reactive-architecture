package com.crm.validation.lead.application.ports.out.endpoints;

import com.crm.validation.lead.domain.model.stock.PriceTick;
import reactor.core.publisher.Flux;

public interface StockMarketPort {
    Flux<PriceTick> streamAnalytics();
}
