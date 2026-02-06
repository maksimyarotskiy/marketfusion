package com.marketfusion.dto.shop;

import com.marketfusion.entity.Platform;
import lombok.Data;

@Data
public class ShopResponseDto {
    private Long id;
    private String name;
    private Platform platform;
}
