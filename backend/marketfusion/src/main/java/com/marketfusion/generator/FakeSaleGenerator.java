package com.marketfusion.generator;

import com.marketfusion.entity.Product;
import com.marketfusion.entity.Sale;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class FakeSaleGenerator {

    private final ProductRepository productRepository;
    private final SaleService saleService;
    private final SecurityUtil securityUtil;

    private final Random random = new Random();

    public void generate(int salesPerProduct) {
        Seller currentSeller = securityUtil.getCurrentSeller();

        List<Product> products = productRepository.findAllByShopSellerId(currentSeller.getId());

        if (products.isEmpty()) {
            throw new IllegalStateException("No products found for current seller");
        }

        for (Product product : products) {
            for (int i = 0; i < salesPerProduct; i++) {
                Sale sale = new Sale();
                sale.setProduct(product);

                int quantity = random.nextInt(1,11);

                double basePrice = product.getPrice() != null
                        ? product.getPrice().doubleValue()
                        : random.nextDouble(100, 500);

                BigDecimal price = BigDecimal.valueOf(basePrice)
                        .setScale(2, RoundingMode.HALF_UP);

                BigDecimal revenue = price.multiply(BigDecimal.valueOf(quantity));

                sale.setPrice(price);
                sale.setQuantity(quantity);
                sale.setRevenue(revenue);
                sale.setSoldAt(
                        LocalDateTime.now()
                                .minusDays(random.nextInt(90))
                                .minusHours(random.nextInt(24))
                                .minusMinutes(random.nextInt(60))
                );

                saleService.save(sale);
            }
        }
    }

}
