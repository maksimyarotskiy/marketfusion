package com.marketfusion.generator;

import com.marketfusion.entity.Product;
import com.marketfusion.entity.Seller;
import com.marketfusion.entity.Stock;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.repository.StockRepository;
import com.marketfusion.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class FakeStockGenerator {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final SecurityUtil securityUtil;

    private final Random random = new Random();

    public void generate(int min, int max, List<String> warehouses) {
        Seller currentSeller = securityUtil.getCurrentSeller();
        List<Product> products = productRepository.findAllByShopSellerId(currentSeller.getId());

        if (products.isEmpty()) {
            throw new IllegalStateException("No products found for current seller");
        }

        for (Product product : products) {
            for (String wh : warehouses) {
                Stock stock = Stock.builder()
                        .product(product)
                        .warehouse(wh)
                        .quantity((long) random.nextInt(min, max + 1))
                        .updatedAt(LocalDateTime.now())
                        .build();
                stockRepository.save(stock);
            }
        }
    }
}
