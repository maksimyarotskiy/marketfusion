package com.marketfusion.dto.shop;

import com.marketfusion.entity.Platform;
import lombok.Data;

@Data
public class ShopRequestDto {
    private String name;
    private Platform platform;
    private String apiKey;
}
