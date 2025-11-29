package com.marketfusion.dto.product;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String sku;
    private Double price;
    private Long shopId;
}
