package com.marketfusion.repository;

import com.marketfusion.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // Все продажи за период для конкретного продавца
    @Query("SELECT s FROM Sale s " +
            "WHERE s.product.shop.seller.id = :sellerId " +
            "AND s.soldAt > :startDate")
    List<Sale> findBySellerIdAndSoldAtAfter(
            @Param("sellerId") Long sellerId,
            @Param("startDate") LocalDateTime startDate);

    // Топ-N товаров по выручке для продавца
    @Query(value = """
        SELECT p.id, p.name, SUM(s.revenue) as totalRevenue
        FROM sales s
        JOIN products p ON s.product_id = p.id
        JOIN shops sh ON p.shop_id = sh.id
        WHERE sh.seller_id = :sellerId
        GROUP BY p.id, p.name
        ORDER BY totalRevenue DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTopProductsByRevenue(
            @Param("sellerId") Long sellerId,
            @Param("limit") int limit);
}
