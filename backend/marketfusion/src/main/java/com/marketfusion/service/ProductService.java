package com.marketfusion.service;

import com.marketfusion.entity.Product;

import java.util.List;

public interface ProductService {

    Product create(Product product, Long shopId);

    List<Product> getByShop(Long shopId);
}
