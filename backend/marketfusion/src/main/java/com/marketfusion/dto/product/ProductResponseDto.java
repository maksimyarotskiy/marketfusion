package com.marketfusion.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Long shopId;
}
