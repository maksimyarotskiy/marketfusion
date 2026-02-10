package com.marketfusion.service;

import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SaleRepository;
import com.marketfusion.repository.StockRepository;
import com.marketfusion.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private StockService stockService;

    private Seller currentSeller;

    @BeforeEach
    void setUp() {
        currentSeller = Seller.builder().id(7L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);
    }

    @Test
    void getStockSummaryCsv_buildsCsvWithEscaping() {
        when(stockRepository.summarizeBySellerAndShop(eq(7L), eq(null)))
                .thenReturn(List.of(
                        new Object[]{10L, "Белая, рубашка", "SKU-1", 120L},
                        new Object[]{11L, "Гаджет \"Pro\"", null, 5L}
                ));

        when(saleRepository.sumQuantityByProductAfter(eq(7L), any(LocalDateTime.class)))
                .thenReturn(List.of(
                        new Object[]{10L, 60L},
                        new Object[]{11L, 0L}
                ));

        String csv = stockService.getStockSummaryCsv(null);

        assertTrue(csv.startsWith("productId,name,sku,totalQuantity,daysUntilOos\n"));
        assertTrue(csv.contains("10,\"Белая, рубашка\",SKU-1,120,60.0\n"));
        assertTrue(csv.contains("11,\"Гаджет \"\"Pro\"\"\",,5,\n"));
    }
}
