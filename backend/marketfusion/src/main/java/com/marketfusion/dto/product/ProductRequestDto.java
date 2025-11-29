package com.marketfusion.dto.product;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String sku;
    private Double price;
}
