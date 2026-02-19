package com.crm.validation.lead.objectmother;

import com.crm.validation.lead.domain.model.stock.PriceTick;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.StockCompanyEntity;

import java.util.Arrays;
import java.util.List;

public class StockObjectMother {

    public static StockCompanyEntity bitcoinCompany() {
        return StockCompanyEntity.builder()
                .id(1L)
                .stockSymbol("BTC")
                .name("Bitcoin")
                .description("Digital cryptocurrency")
                .build();
    }

    public static StockCompanyEntity appleCompany() {
        return StockCompanyEntity.builder()
                .id(2L)
                .stockSymbol("AAPL")
                .name("Apple Inc.")
                .description("Technology company")
                .build();
    }

    public static StockCompanyEntity googleCompany() {
        return StockCompanyEntity.builder()
                .id(3L)
                .stockSymbol("GOOGL")
                .name("Alphabet Inc.")
                .description("Technology conglomerate")
                .build();
    }

    public static StockCompanyEntity unknownCompany() {
        return StockCompanyEntity.builder()
                .id(99L)
                .stockSymbol("UNKNOWN")
                .name("Unknown Company")
                .description("Company not found")
                .build();
    }

    public static StockCompanyEntity customCompany(Long id, String symbol, String name, String description) {
        return StockCompanyEntity.builder()
                .id(id)
                .stockSymbol(symbol)
                .name(name)
                .description(description)
                .build();
    }

    public static List<PriceTick> bitcoinPriceTicks() {
        return Arrays.asList(
                new PriceTick("BTC", 100.0),
                new PriceTick("BTC", 110.0),
                new PriceTick("BTC", 120.0),
                new PriceTick("BTC", 130.0),
                new PriceTick("BTC", 340.0)
        );
    }

    public static List<PriceTick> applePriceTicks() {
        return Arrays.asList(
                new PriceTick("AAPL", 150.0),
                new PriceTick("AAPL", 155.0),
                new PriceTick("AAPL", 160.0),
                new PriceTick("AAPL", 165.0),
                new PriceTick("AAPL", 170.0)
        );
    }

    public static List<PriceTick> googlePriceTicks() {
        return Arrays.asList(
                new PriceTick("GOOGL", 2500.0),
                new PriceTick("GOOGL", 2550.0),
                new PriceTick("GOOGL", 2600.0),
                new PriceTick("GOOGL", 2650.0),
                new PriceTick("GOOGL", 2700.0)
        );
    }

    public static List<PriceTick> singlePriceTick(String symbol, double price) {
        return Arrays.asList(new PriceTick(symbol, price));
    }

    public static List<PriceTick> priceTicksForSymbol(String symbol, double basePrice, int count) {
        return Arrays.stream(new int[count])
                .mapToObj(i -> new PriceTick(symbol, basePrice + (i * 10.0)))
                .toList();
    }

    public static List<PriceTick> emptyPriceTicks() {
        return Arrays.asList();
    }

    // Predefined test scenarios
    public static class TestDataScenario {
        public static StockCompanyEntity bitcoin() {
            return bitcoinCompany();
        }

        public static List<PriceTick> bitcoinTicks() {
            return bitcoinPriceTicks();
        }

        public static StockCompanyEntity apple() {
            return appleCompany();
        }

        public static List<PriceTick> appleTicks() {
            return applePriceTicks();
        }

        public static StockCompanyEntity unknown() {
            return unknownCompany();
        }

        public static List<PriceTick> unknownTicks() {
            return singlePriceTick("UNKNOWN", 100.0);
        }
    }
}
