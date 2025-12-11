package com.marketfusion.mapper;

import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.dto.product.ProductResponseDto;
import com.marketfusion.dto.product.ProductUpdateDto;
import com.marketfusion.entity.Product;
import com.marketfusion.entity.Shop;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public Product toEntity(ProductRequestDto dto, Shop shop) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setShop(shop);
        return product;
    }

    public ProductResponseDto toDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setShopId(product.getShop().getId());
        return dto;
    }

    public void updateEntity(Product product, ProductUpdateDto dto) {
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
    }
}
