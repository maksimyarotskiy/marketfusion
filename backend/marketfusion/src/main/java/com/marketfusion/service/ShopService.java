package com.marketfusion.service;

import com.marketfusion.entity.Shop;

import java.util.List;

public interface ShopService {
    Shop create(Shop shop);
    List<Shop> getAllForCurrentSeller();
    Shop getById(Long id);
}
