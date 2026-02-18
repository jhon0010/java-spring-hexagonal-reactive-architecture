package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import lombok.Data;

@Data
public class StockSummaryResult {
    private String symbol;
    private double price;
    private String name;
    private String description;
}
