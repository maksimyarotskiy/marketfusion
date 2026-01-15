package com.marketfusion.service;


import com.marketfusion.entity.Sale;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SaleRepository;
import com.marketfusion.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SaleRepository saleRepository;
    private final SecurityUtil securityUtil;

    private Seller getCurrentSeller() {
        return securityUtil.getCurrentSeller();
    }

    public double getRevenueLast30Days() {
        Seller seller = getCurrentSeller();
        LocalDateTime start = LocalDateTime.now().minusDays(30);

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtAfter(
                seller.getId(), start);

        return sales.stream()
                .mapToDouble(Sale::getRevenue)
                .sum();
    }

    public Map<LocalDateTime, Double> getDailyRevenueLast30Days() {
        Seller seller = getCurrentSeller();
        LocalDateTime start = LocalDateTime.now().minusDays(30);

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtAfter(
                seller.getId(), start);

        return sales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSoldAt().toLocalDate().atStartOfDay(),
                        Collectors.summingDouble(Sale::getRevenue)
                ));
    }

    public List<TopProduct> getTopProducts(int limit) {
        Seller seller = getCurrentSeller();

        List<Object[]> results = saleRepository.findTopProductsByRevenue(
                seller.getId(), limit);

        return results.stream()
                .map(row -> new TopProduct(
                        (Long) row[0],           // productId
                        (String) row[1],         // name
                        (Double) row[2] // totalRevenue
                ))
                .toList();
    }

    public record TopProduct(Long productId, String name, Double totalRevenue) {}

}
