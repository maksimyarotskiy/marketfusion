package com.marketfusion.service.impl;

import com.marketfusion.entity.Seller;
import com.marketfusion.entity.Shop;
import com.marketfusion.repository.ShopRepository;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final SecurityUtil securityUtil;
    private final ShopRepository shopRepository;

    @Override
    public Shop create(Shop shop) {
        Seller currentSeller = securityUtil.getCurrentSeller();
        shop.setSeller(currentSeller);
        return shopRepository.save(shop);
    }

    @Override
    public List<Shop> getAllForCurrentSeller() {
        Seller currentSeller = securityUtil.getCurrentSeller();
        return shopRepository.findBySellerId(currentSeller.getId());
    }

    @Override
    public Shop getById(Long id) {
        Seller currentSeller = securityUtil.getCurrentSeller();
        return shopRepository.findByIdAndSellerId(id, currentSeller.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found or access denied"));
    }
}
