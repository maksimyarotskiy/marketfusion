package com.marketfusion.repository;

import com.marketfusion.entity.Sale;
import org.springframework.cglib.core.Local;
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

    // Для получения данных за период
    @Query("SELECT s FROM Sale s " +
            "WHERE s.product.shop.seller.id = :sellerId " +
            "AND s.soldAt BETWEEN :from AND :to")
    List<Sale> findBySellerIdAndSoldAtBetween(
            @Param("sellerId") Long sellerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Сумма количества проданных товаров
    @Query("SELECT SUM(s.quantity) FROM Sale s " +
            "WHERE s.product.shop.seller.id = :sellerId " +
            "AND s.soldAt BETWEEN :from AND :to")
    Long sumQuantityBySellerIdAndSoldAtBetween(
            @Param("sellerId") Long sellerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Выручка по платформам
    @Query(value = """
        SELECT sh.platform, SUM(s.revenue) as total
        FROM sales s
        JOIN products p ON s.product_id = p.id
        JOIN shops sh ON p.shop_id = sh.id
        WHERE sh.seller_id = :sellerId
        AND s.sold_at BETWEEN :from AND :to
        GROUP BY sh.platform
        """, nativeQuery = true)
    List<Object[]> sumRevenueByPlatformAndSellerIdBetween(
            @Param("sellerId") Long sellerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
