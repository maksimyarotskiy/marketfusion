package com.marketfusion.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductSalesSummaryDto {
    private Long productId;
    private String name;
    private String sku;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
}
