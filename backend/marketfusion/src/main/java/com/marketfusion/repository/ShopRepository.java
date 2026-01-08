package com.marketfusion.repository;

import com.marketfusion.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    List<Shop> findBySellerId(Long sellerId);

    Optional<Shop> findByIdAndSellerId(Long id, Long id1);
}
