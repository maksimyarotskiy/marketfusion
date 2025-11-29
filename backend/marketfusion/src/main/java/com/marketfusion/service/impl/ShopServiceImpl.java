package com.marketfusion.service.impl;

import com.marketfusion.entity.Shop;
import com.marketfusion.repository.ShopRepository;
import com.marketfusion.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Override
    public Shop create(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public List<Shop> getAll() {
        return shopRepository.findAll();
    }


}
