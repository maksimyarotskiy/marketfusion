package com.marketfusion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.dto.product.ProductResponseDto;
import com.marketfusion.dto.product.ProductUpdateDto;
import com.marketfusion.entity.Product;
import com.marketfusion.entity.Shop;
import com.marketfusion.mapper.ProductMapper;
import com.marketfusion.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_success() throws Exception {
        // given
        Long shopId = 1L;
        Shop shop = new Shop();
        shop.setId(shopId);

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("Knife");
        requestDto.setPrice(800.00);

        Product product = new Product();
        product.setId(10L);
        product.setName("Knife");
        product.setPrice(800.0);
        product.setShop(shop);

        when(productService.create(
                any(ProductRequestDto.class),
                eq(shopId)
        )).thenReturn(product);

        ProductResponseDto expectedDto = ProductMapper.toDto(product);

        // when + then
        mockMvc.perform(
                post("/api/products/{shopId}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedDto.getName()))
                .andExpect(jsonPath("$.price").value(expectedDto.getPrice()))
                .andExpect(jsonPath("$.sku").value(expectedDto.getSku()))
                .andExpect(jsonPath("$.shopId").value(expectedDto.getShopId()));


    }

    @Test
    void getProductsByShop_success() throws Exception {
        // given
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("My Shop");

        Product product1 = new Product();
        product1.setId(10L);
        product1.setName("P1");
        product1.setPrice(10.0);
        product1.setShop(shop);
        product1.setSku("sku1");

        Product product2 = new Product();
        product2.setId(20L);
        product2.setName("P2");
        product2.setPrice(20.0);
        product2.setShop(shop);
        product2.setSku("sku2");

        Product product3 = new Product();
        product3.setId(30L);
        product3.setName("P3");
        product3.setPrice(30.0);
        product3.setShop(shop);
        product3.setSku("sku3");

        List<Product> productArrayList = new ArrayList<>(List.of(
                product1,
                product2,
                product3
        ));

        when(productService.getByShop(
                eq(1L)
        )).thenReturn(productArrayList);

        List<ProductResponseDto> expectedList = productArrayList.stream()
                .map(ProductMapper::toDto)
                .toList();

        // when + then
        mockMvc.perform(
                get("/api/products/shop/{shopId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedList.size()))
                .andExpect(jsonPath("$[0].id").value(expectedList.getFirst().getId()))
                .andExpect(jsonPath("$[1].name").value(expectedList.get(1).getName()))
                .andExpect(jsonPath("$[2].sku").value(expectedList.get(2).getSku()))
                .andExpect(jsonPath("$[1].price").value(expectedList.get(1).getPrice()))
                .andExpect(jsonPath("$[2].shopId").value(expectedList.get(2).getShopId()));
    }

    @Test
    void updateProduct_success() throws Exception {
        // given
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("Хозяйственник");
        shop.setPlatform("WB");
        shop.setApiKey("123575442");

        Product updatedProduct = new Product();
        updatedProduct.setId(345L);
        updatedProduct.setSku("updatedSku");
        updatedProduct.setName("updatedName");
        updatedProduct.setPrice(200.00);
        updatedProduct.setShop(shop);

        ProductUpdateDto updateDto = new ProductUpdateDto();
        updateDto.setPrice(200.00);
        updateDto.setName("updatedName");
        updateDto.setSku("updatedSku");

        when(productService.update(
                eq(345L), any(ProductUpdateDto.class)
        )).thenReturn(updatedProduct);

        ProductResponseDto expectedDto = ProductMapper.toDto(updatedProduct);

        // when + then
        mockMvc.perform(
                put("/api/products/{id}", 345L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(expectedDto.getSku()))
                .andExpect(jsonPath("$.name").value(expectedDto.getName()))
                .andExpect(jsonPath("$.price").value(expectedDto.getPrice()))
                .andExpect(jsonPath("$.id").value(expectedDto.getId()))
                .andExpect(jsonPath("$.shopId").value(expectedDto.getShopId()));
    }

    @Test
    void deleteProduct_success() throws Exception {
        // given
        Long productId = 10L;

        doNothing().when(productService).delete(eq(productId));

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(eq(productId));

    }
}