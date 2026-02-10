package com.marketfusion.repository;

import com.marketfusion.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("""
        SELECT s.product.id, s.product.name, s.product.sku, SUM(s.quantity)
        FROM Stock s
        WHERE s.product.shop.seller.id = :sellerId
        AND (:shopId IS NULL OR s.product.shop.id = :shopId)
        GROUP BY s.product.id, s.product.name, s.product.sku
        """)
    List<Object[]> summarizeBySellerAndShop(
            @Param("sellerId") Long sellerId,
            @Param("shopId") Long shopId);

    @Query("""
        SELECT s.product.id, SUM(s.quantity)
        FROM Stock s
        WHERE s.product.shop.seller.id = :sellerId
        GROUP BY s.product.id
        """)
    List<Object[]> totalByProduct(@Param("sellerId") Long sellerId);
}
