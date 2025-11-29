package com.marketfusion.mapper;

import com.marketfusion.dto.shop.ShopRequestDto;
import com.marketfusion.dto.shop.ShopResponseDto;
import com.marketfusion.entity.Shop;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ShopMapper {

    public Shop toEntity(ShopRequestDto dto) {
        Shop shop = new Shop();
        shop.setName(dto.getName());
        shop.setPlatform(dto.getPlatform());
        shop.setApiKey(dto.getApiKey());
        return shop;
    }

    public ShopResponseDto toDto(Shop shop) {
        ShopResponseDto dto = new ShopResponseDto();
        dto.setId(shop.getId());
        dto.setName(shop.getName());
        dto.setPlatform(shop.getPlatform());
        return dto;
    }
}
