package com.marketfusion.service;

import com.marketfusion.entity.PriceHistory;
import com.marketfusion.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistory save(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    public List<PriceHistory> findByProduct(Long productId) {
        return priceHistoryRepository.findByProductIdOrderByCapturedAtDesc(productId);
    }

    public List<PriceHistory> findByProductBetween(Long productId, LocalDateTime from, LocalDateTime to) {
        return priceHistoryRepository.findByProductIdAndCapturedAtBetweenOrderByCapturedAtAsc(productId, from, to);
    }

    public Optional<PriceHistory> findLastByProduct(Long productId) {
        return priceHistoryRepository.findTopByProductIdOrderByCapturedAtDesc(productId);
    }
}
