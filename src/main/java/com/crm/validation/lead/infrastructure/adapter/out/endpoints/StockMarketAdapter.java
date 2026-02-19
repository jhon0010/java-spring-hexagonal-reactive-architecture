package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.ports.out.endpoints.StockMarketPort;
import com.crm.validation.lead.domain.model.stock.PriceTick;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static java.lang.Math.random;

@Component
public class StockMarketAdapter implements StockMarketPort {

    @Override
    public Flux<PriceTick> streamAnalytics() {
            return Flux.interval(Duration.ofSeconds(1))
                    .map(tick -> {
                        double seed = random() * 100;
                        if (seed < 33) {
                            return new PriceTick("BTC", random() * 10000);
                        } else if (seed > 33 && seed < 66) {
                            return new PriceTick("ETH", random() * 10000);
                        } else {
                            return new PriceTick("LTC", random() * 10000);
                        }
                    });
    }
}
