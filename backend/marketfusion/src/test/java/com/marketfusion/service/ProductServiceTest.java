package com.marketfusion.service;

import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.dto.product.ProductUpdateDto;
import com.marketfusion.entity.Product;
import com.marketfusion.entity.Role;
import com.marketfusion.entity.Seller;
import com.marketfusion.entity.Shop;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.repository.ShopRepository;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_ownShop_success() {
        Seller currentSeller = Seller.builder().id(1L).email("test@seller.com").role(Role.SELLER).build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);

        Shop shop = Shop.builder().id(10L).name("My store").platform("WB").apiKey("1111").seller(currentSeller).build();
        when(shopRepository.findByIdAndSellerId(10L, 1L)).thenReturn(Optional.of(shop));
        when(shopRepository.findById(10L)).thenReturn(Optional.of(shop));

        Product savedProduct = Product.builder()
                .id(100L)
                .sku("R300")
                .name("Soup")
                .price(new BigDecimal("300.00"))
                .shop(shop)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductRequestDto dto = new ProductRequestDto();
        Product result = productService.create(dto, 10L);

        assertEquals(100L, result.getId());
        assertEquals(10L, result.getShop().getId());
        assertEquals("R300", result.getSku());
        assertEquals(0, result.getPrice().compareTo(new BigDecimal("300.00"))); // ← правильное сравнение BigDecimal

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_foreignShop_throwsException() {
        Seller currentSeller = Seller.builder().id(1L).email("test@seller.com").role(Role.SELLER).build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);

        when(shopRepository.findByIdAndSellerId(999L, 1L)).thenReturn(Optional.empty());

        ProductRequestDto dto = new ProductRequestDto();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.create(dto, 999L));

        assertEquals("Shop not found or access denied", ex.getMessage());
    }

    @Test
    void updateProduct_ownShop_success() {
        Seller seller = Seller.builder().id(1L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(seller);

        Shop shop = Shop.builder().id(10L).seller(seller).build();
        Product product = Product.builder()
                .id(100L)
                .sku("R300")
                .name("Soup")
                .price(new BigDecimal("300.00"))
                .shop(shop)
                .build();

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(shopRepository.findByIdAndSellerId(10L, seller.getId())).thenReturn(Optional.of(shop));

        Product updated = Product.builder()
                .id(100L)
                .name("New Name")
                .price(new BigDecimal("999.99"))
                .shop(shop)
                .build();
        when(productRepository.save(any())).thenReturn(updated);

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName("New Name");
        dto.setPrice(new BigDecimal("999.99"));

        Product result = productService.update(100L, dto);

        assertEquals("New Name", result.getName());
        assertEquals(0, result.getPrice().compareTo(new BigDecimal("999.99")));
        assertEquals(100L, result.getId());

        verify(shopRepository).findByIdAndSellerId(10L, seller.getId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_foreignShop_throwsException() {
        Seller seller = Seller.builder().id(1L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(seller);

        Shop foreignShop = Shop.builder().id(10L).seller(Seller.builder().id(2L).build()).build();
        Product product = Product.builder()
                .id(100L)
                .name("New Name")
                .price(new BigDecimal("999.99"))
                .shop(foreignShop)
                .build();

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(shopRepository.findByIdAndSellerId(10L, seller.getId())).thenReturn(Optional.empty());

        ProductUpdateDto dto = new ProductUpdateDto();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.update(100L, dto));

        assertEquals("Shop not found or access denied", ex.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_ownShop_success() {
        Seller seller = Seller.builder().id(1L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(seller);

        Shop shop = Shop.builder().id(10L).seller(seller).build();
        Product product = Product.builder().id(100L).shop(shop).build();

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(shopRepository.findByIdAndSellerId(10L, seller.getId())).thenReturn(Optional.of(shop));

        productService.delete(100L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_foreignShop_throwsException() {
        Seller seller = Seller.builder().id(1L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(seller);

        Shop foreignShop = Shop.builder().id(999L).seller(Seller.builder().id(2L).build()).build();
        Product product = Product.builder().id(100L).shop(foreignShop).build();

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(shopRepository.findByIdAndSellerId(999L, seller.getId())).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productService.delete(100L));  // ← исправлено на delete

        assertEquals("Shop not found or access denied", ex.getMessage());
        verify(productRepository, never()).delete(any());
    }
}