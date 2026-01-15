package com.marketfusion.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    @NotNull
    private BigDecimal price;

}
