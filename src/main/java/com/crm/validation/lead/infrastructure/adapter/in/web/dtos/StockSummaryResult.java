package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import com.crm.validation.lead.domain.model.stock.PriceTick;
import lombok.Data;

import java.util.List;

@Data
public class StockSummaryResult {
    private List<PriceTick> priceTicks;
    private String name;
    private String description;
    private double averagePrice;
}
