package com.marketfusion.generator;

import com.marketfusion.entity.Product;
import com.marketfusion.entity.Sale;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class FakeSaleGenerator {

    private final ProductRepository productRepository;
    private final SaleService saleService;

    private final Random random = new Random();

    public void generate(int salesPerProduct) {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            for (int i = 0; i < salesPerProduct; i++) {
                Sale sale = new Sale();
                sale.setProduct(product);

                int quantity = random.nextInt(1,5);
                double price =  product.getPrice() != null
                        ? product.getPrice()
                        : random.nextDouble(100, 2000);

                sale.setPrice(price);
                sale.setQuantity(quantity);
                sale.setRevenue(price * quantity);
                sale.setSoldAt(
                        LocalDateTime.now()
                                .minusDays(random.nextInt(30))
                                .minusHours(random.nextInt(24))
                );

                saleService.save(sale);
            }
        }
    }

}
