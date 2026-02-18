package com.crm.validation.lead.infrastructure.adapter.out.db.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
@Table(name = "stock_company")
public class StockCompanyEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    private String stockSymbol;
}
