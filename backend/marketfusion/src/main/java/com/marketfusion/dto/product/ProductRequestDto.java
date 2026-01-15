package com.marketfusion.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDto {
    private String name;
    private String sku;
    private BigDecimal price;
}
