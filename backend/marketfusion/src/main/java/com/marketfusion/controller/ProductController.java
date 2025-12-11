package com.marketfusion.controller;

import com.marketfusion.dto.product.ProductRequestDto;
import com.marketfusion.dto.product.ProductResponseDto;
import com.marketfusion.dto.product.ProductUpdateDto;
import com.marketfusion.entity.Product;
import com.marketfusion.mapper.ProductMapper;
import com.marketfusion.repository.ProductRepository;
import com.marketfusion.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API для управления товарами")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{shopId}")
    @Operation(summary = "Создать товар", description = "Создаёт новый товар и привязывает его к магазину")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Товар создан успешно",
                    content = @Content (
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин с указанным ID не найден",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный запрос",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponseDto> createProduct(
            @PathVariable Long shopId,
            @RequestBody ProductRequestDto requestDto
    ) {
        Product created = productService.create(requestDto, shopId);
        return ResponseEntity.status(201).body(ProductMapper.toDto(created));
    }


    @GetMapping("/shop/{shopId}")
    @Operation(summary = "Получить товары магазина", description = "Возвращает товары по id магазина")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список товаров успешно получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Магазин с указанным ID не найден",
                    content = @Content
            )
    })
    public ResponseEntity<List<ProductResponseDto>> getProductsByShop(@PathVariable Long shopId) {
        List<Product> products = productService.getByShop(shopId);
        return ResponseEntity.ok(
                products.stream()
                        .map(ProductMapper::toDto)
                        .toList()
        );
    }


    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет данные товара по ID")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateDto dto
            ) {
        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(ProductMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();// почему пустой возвращаем и еще билдим в конце
    }
}
