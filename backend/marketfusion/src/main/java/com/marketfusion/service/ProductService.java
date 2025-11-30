package com.marketfusion.service;

import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.entity.Product;

import java.util.List;

public interface ProductService {

    Product create(ProductRequestDto dto, Long shopId);

    List<Product> getByShop(Long shopId);
}
