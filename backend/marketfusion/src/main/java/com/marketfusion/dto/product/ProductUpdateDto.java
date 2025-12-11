package com.marketfusion.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @NotBlank
    private String name;

    @NotBlank
    private String sku;

    @NotNull
    private Double price;

}
