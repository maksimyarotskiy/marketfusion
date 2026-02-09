package com.marketfusion.service;

import com.marketfusion.dto.analytics.ProductSalesSummaryDto;
import com.marketfusion.entity.Sale;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SaleRepository;
import com.marketfusion.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public BigDecimal getRevenueLast30Days() {
        Seller seller = getCurrentSeller();
        LocalDateTime start = LocalDateTime.now().minusDays(30);

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtAfter(
                seller.getId(), start);

        return sales.stream()
                .map(Sale::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<LocalDateTime, BigDecimal> getDailyRevenueLast30Days() {
        Seller seller = getCurrentSeller();
        LocalDateTime start = LocalDateTime.now().minusDays(30);

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtAfter(
                seller.getId(), start);

        return sales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSoldAt().toLocalDate().atStartOfDay(),
                        Collectors.reducing(BigDecimal.ZERO, Sale::getRevenue, BigDecimal::add)
                ));
    }

    public List<TopProduct> getTopProducts(int limit) {
        Seller seller = getCurrentSeller();

        List<Object[]> results = saleRepository.findTopProductsByRevenue(
                seller.getId(), limit);

        return results.stream()
                .map(row -> new TopProduct(
                        (Long) row[0],
                        (String) row[1],
                        BigDecimal.valueOf(((Number) row[2]).doubleValue())
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    public List<TopProduct> getTopProductsBetween(LocalDateTime from, LocalDateTime to, int limit) {
        Seller seller = getCurrentSeller();

        List<Object[]> results = saleRepository.findTopProductsByRevenueBetween(
                seller.getId(), from, to, limit);

        return results.stream()
                .map(row -> new TopProduct(
                        (Long) row[0],
                        (String) row[1],
                        BigDecimal.valueOf(((Number) row[2]).doubleValue())
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    public BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtBetween(
                seller.getId(), from, to);

        return sales.stream()
                .map(Sale::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<LocalDateTime, BigDecimal> getDailyRevenueBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtBetween(
                seller.getId(), from, to);

        return sales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSoldAt().toLocalDate().atStartOfDay(),
                        Collectors.reducing(BigDecimal.ZERO, Sale::getRevenue, BigDecimal::add)
                ));
    }

    public BigDecimal getAverageCheckBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();

        List<Sale> sales = saleRepository.findBySellerIdAndSoldAtBetween(
                seller.getId(), from, to);

        if (sales.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalItems = sales.stream()
                .mapToLong(Sale::getQuantity)
                .sum();

        return totalItems == 0 ? BigDecimal.ZERO : totalRevenue.divide(
                BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP);
    }

    public long getTotalItemsSoldBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();
        return saleRepository.sumQuantityBySellerIdAndSoldAtBetween(
                seller.getId(), from, to);
    }

    public Map<String, BigDecimal> getRevenueByPlatformBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();

        List<Object[]> results = saleRepository.sumRevenueByPlatformAndSellerIdBetween(
                seller.getId(), from, to);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> BigDecimal.valueOf(((Number) row[1]).doubleValue())
                                .setScale(2, RoundingMode.HALF_UP)
                ));
    }

    public List<ProductSalesSummaryDto> getProductSalesSummaryBetween(LocalDateTime from, LocalDateTime to) {
        Seller seller = getCurrentSeller();

        List<Object[]> results = saleRepository.findProductSalesSummaryBetween(
                seller.getId(), from, to);

        return results.stream()
                .map(row -> new ProductSalesSummaryDto(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).longValue(),
                        BigDecimal.valueOf(((Number) row[4]).doubleValue()).setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    public String getProductSalesSummaryCsv(LocalDateTime from, LocalDateTime to) {
        List<ProductSalesSummaryDto> rows = getProductSalesSummaryBetween(from, to);

        StringBuilder sb = new StringBuilder();
        sb.append("productId,name,sku,totalQuantity,totalRevenue\n");

        for (ProductSalesSummaryDto r : rows) {
            sb.append(r.getProductId()).append(',')
                    .append(escapeCsv(r.getName())).append(',')
                    .append(escapeCsv(r.getSku())).append(',')
                    .append(r.getTotalQuantity()).append(',')
                    .append(r.getTotalRevenue())
                    .append('\n');
        }

        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }

    public record TopProduct(Long productId, String name, BigDecimal totalRevenue) {}
}
