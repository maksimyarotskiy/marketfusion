package com.marketfusion.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockSummaryDto {
    private Long productId;
    private String name;
    private String sku;
    private Long totalQuantity;
    private Double daysUntilOos;
}
