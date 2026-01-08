package com.marketfusion.service.impl;


import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.dto.product.ProductUpdateDto;
import com.marketfusion.entity.Product;
import com.marketfusion.entity.Seller;
import com.marketfusion.entity.Shop;
import com.marketfusion.exception.ProductNotFoundException;
import com.marketfusion.mapper.ProductMapper;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.repository.ShopRepository;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final SecurityUtil securityUtil;

    private Seller getCurrentSeller() {
        return securityUtil.getCurrentSeller();
    }

    private void checkShopOwnership(Long shopId) {
        Seller currentSeller = getCurrentSeller();
        shopRepository.findByIdAndSellerId(shopId, currentSeller.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found or access denied"));
    }

    @Override
    public Product create(ProductRequestDto dto, Long shopId) {
        checkShopOwnership(shopId);

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        Product product = ProductMapper.toEntity(dto, shop);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getByShop(Long shopId) {
        checkShopOwnership(shopId);
        return productRepository.findByShopId(shopId);
    }

    @Override
    public Product update(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        checkShopOwnership(product.getShop().getId());

        ProductMapper.updateEntity(product, dto);
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        checkShopOwnership(product.getShop().getId());

        productRepository.delete(product);
    }
}
