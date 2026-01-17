package com.marketfusion.service;

import com.marketfusion.entity.Sale;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SaleRepository;
import com.marketfusion.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Seller currentSeller;

    @BeforeEach
    void setUp() {
        currentSeller = Seller.builder().id(1L).email("test@seller.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);
    }


    @Test
    void getRevenueLast30Days_emptySales_returnsZero() {
        when(saleRepository.findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of());

        BigDecimal revenue = analyticsService.getRevenueLast30Days();

        assertEquals(BigDecimal.ZERO, revenue);
        verify(saleRepository).findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getRevenueLast30Days_calculatesSumCorrectly() {
        List<Sale> sales = List.of(
                Sale.builder().revenue(new BigDecimal("100.50")).build(),
                Sale.builder().revenue(new BigDecimal("200.00")).build()
        );
        when(saleRepository.findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(sales);

        BigDecimal revenue = analyticsService.getRevenueLast30Days();

        assertEquals(new BigDecimal("300.50"), revenue);
        verify(saleRepository).findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void getDailyRevenueLast30Days_groupsByDayCorrectly() {
        LocalDateTime day1 = LocalDateTime.of(2026, 1, 10, 0, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 1, 12, 0, 0);

        List<Sale> sales = List.of(
                Sale.builder().soldAt(day1.plusHours(10)).revenue(new BigDecimal("100.00")).build(),
                Sale.builder().soldAt(day1.plusHours(15)).revenue(new BigDecimal("50.00")).build(),
                Sale.builder().soldAt(day2.plusHours(8)).revenue(new BigDecimal("200.00")).build()
        );
        when(saleRepository.findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(sales);

        Map<LocalDateTime, BigDecimal> daily = analyticsService.getDailyRevenueLast30Days();

        assertEquals(new BigDecimal("150.00"), daily.get(day1));
        assertEquals(new BigDecimal("200.00"), daily.get(day2));
        verify(saleRepository).findBySellerIdAndSoldAtAfter(eq(1L), any(LocalDateTime.class));
    }


    @Test
    void getTopProducts_returnsCorrectTopWithLimit() {
        List<Object[]> mockResults = List.of(
                new Object[]{1L, "Product A", 500.0},
                new Object[]{2L, "Product B", 300.0},
                new Object[]{3L, "Product C", 100.0}
        );
        when(saleRepository.findTopProductsByRevenue(eq(1L), eq(2)))
                .thenReturn(mockResults.subList(0, 2));

        List<AnalyticsService.TopProduct> top = analyticsService.getTopProducts(2);

        assertEquals(2, top.size());
        assertEquals(1L, top.get(0).productId());
        assertEquals("Product A", top.get(0).name());
        assertEquals(new BigDecimal("500.00"), top.get(0).totalRevenue());
        verify(saleRepository).findTopProductsByRevenue(eq(1L), eq(2));
    }

    @Test
    void getRevenueBetween_calculatesSumForPeriod() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 10, 23, 59, 59);

        List<Sale> sales = List.of(
                Sale.builder().revenue(new BigDecimal("150.00")).build(),
                Sale.builder().revenue(new BigDecimal("250.00")).build()
        );
        when(saleRepository.findBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to)))
                .thenReturn(sales);

        BigDecimal revenue = analyticsService.getRevenueBetween(from, to);

        assertEquals(new BigDecimal("400.00"), revenue);
        verify(saleRepository).findBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to));
    }

    @Test
    void getAverageCheckBetween_calculatesCorrectly() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 15, 23, 59, 59);

        List<Sale> sales = List.of(
                Sale.builder().revenue(new BigDecimal("100.00")).quantity(2).build(),
                Sale.builder().revenue(new BigDecimal("200.00")).quantity(3).build()
        );
        when(saleRepository.findBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to)))
                .thenReturn(sales);

        BigDecimal avgCheck = analyticsService.getAverageCheckBetween(from, to);

        assertEquals(new BigDecimal("60.00"), avgCheck);
        verify(saleRepository).findBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to));
    }

    @Test
    void getTotalItemsSoldBetween_sumsQuantity() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 15, 23, 59, 59);

        when(saleRepository.sumQuantityBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to)))
                .thenReturn(15L);

        long total = analyticsService.getTotalItemsSoldBetween(from, to);

        assertEquals(15L, total);
        verify(saleRepository).sumQuantityBySellerIdAndSoldAtBetween(eq(1L), eq(from), eq(to));
    }

    @Test
    void getRevenueByPlatformBetween_groupsByPlatform() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 15, 23, 59, 59);

        List<Object[]> mockResults = List.of(
                new Object[]{"WB", 500.0},
                new Object[]{"OZON", 300.0}
        );
        when(saleRepository.sumRevenueByPlatformAndSellerIdBetween(eq(1L), eq(from), eq(to)))
                .thenReturn(mockResults);

        Map<String, BigDecimal> byPlatform = analyticsService.getRevenueByPlatformBetween(from, to);

        assertEquals(new BigDecimal("500.00"), byPlatform.get("WB"));
        assertEquals(new BigDecimal("300.00"), byPlatform.get("OZON"));
        verify(saleRepository).sumRevenueByPlatformAndSellerIdBetween(eq(1L), eq(from), eq(to));
    }
}