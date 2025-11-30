package com.marketfusion.service.impl;


import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.entity.Product;
import com.marketfusion.entity.Shop;
import com.marketfusion.mapper.ProductMapper;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.repository.ShopRepository;
import com.marketfusion.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    @Override
    public Product create(ProductRequestDto dto, Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Магазин не найден"));

        Product product = ProductMapper.toEntity(dto, shop);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getByShop(Long shopId) {
        return productRepository.findByShopId(shopId);
    }
}
