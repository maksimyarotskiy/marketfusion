package com.marketfusion.service;

import com.marketfusion.dto.stock.StockSummaryDto;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SaleRepository;
import com.marketfusion.repository.StockRepository;
import com.marketfusion.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final SaleRepository saleRepository;
    private final SecurityUtil securityUtil;

    public List<StockSummaryDto> getStockSummary(Long shopId) {
        Seller seller = securityUtil.getCurrentSeller();

        List<Object[]> stockRows = stockRepository.summarizeBySellerAndShop(seller.getId(), shopId);

        LocalDateTime from = LocalDateTime.now().minusDays(30);
        Map<Long, Long> avgDailyByProduct = buildAvgDailySales(seller.getId(), from);

        return stockRows.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    String name = (String) row[1];
                    String sku = (String) row[2];
                    Long qty = ((Number) row[3]).longValue();

                    Long avgDaily = avgDailyByProduct.getOrDefault(productId, 0L);
                    Double daysUntilOos = avgDaily == 0 ? null : qty.doubleValue() / avgDaily.doubleValue();

                    return new StockSummaryDto(productId, name, sku, qty, daysUntilOos);
                })
                .toList();
    }

    public String getStockSummaryCsv(Long shopId) {
        List<StockSummaryDto> rows = getStockSummary(shopId);

        StringBuilder sb = new StringBuilder();
        sb.append("productId,name,sku,totalQuantity,daysUntilOos\n");

        for (StockSummaryDto row : rows) {
            sb.append(row.getProductId()).append(',')
                    .append(escapeCsv(row.getName())).append(',')
                    .append(escapeCsv(row.getSku())).append(',')
                    .append(row.getTotalQuantity()).append(',')
                    .append(row.getDaysUntilOos() == null ? "" : row.getDaysUntilOos())
                    .append('\n');
        }

        return sb.toString();
    }

    private Map<Long, Long> buildAvgDailySales(Long sellerId, LocalDateTime from) {
        List<Object[]> sales = saleRepository.sumQuantityByProductAfter(sellerId, from);
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : sales) {
            Long productId = (Long) row[0];
            Long totalQty = ((Number) row[1]).longValue();
            long avgDaily = Math.max(0, Math.round(totalQty / 30.0));
            result.put(productId, avgDaily);
        }
        return result;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
