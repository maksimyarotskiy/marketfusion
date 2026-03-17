package com.marketfusion.repository;

import com.marketfusion.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // Вся история цены по товару
    List<PriceHistory> findByProductIdOrderByCapturedAtDesc(Long productId);

    List<PriceHistory> findByProductIdAndCapturedAtBetweenOrderByCapturedAtAsc(
            Long productId,
            LocalDateTime from,
            LocalDateTime to
    );

    // Последняя актуальная цена по товару
    Optional<PriceHistory> findTopByProductIdOrderByCapturedAtDesc(Long productId);

    // История цен продавца за период
    @Query("SELECT ph FROM PriceHistory ph " +
            "WHERE ph.product.shop.seller.id = :sellerId " +
            "AND ph.capturedAt BETWEEN :from AND :to " +
            "ORDER BY ph.capturedAt ASC")
    List<PriceHistory> findBySellerIdAndCapturedAtBetween(
            @Param("sellerId") Long sellerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
