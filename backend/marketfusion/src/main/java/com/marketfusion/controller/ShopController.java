package com.marketfusion.controller;


import com.marketfusion.dto.shop.ShopRequestDto;
import com.marketfusion.dto.shop.ShopResponseDto;
import com.marketfusion.entity.Shop;
import com.marketfusion.mapper.ShopMapper;
import com.marketfusion.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Tag(name = "Shops", description = "API для управления магазинами")
public class ShopController {

    private final ShopService shopService;


    @PostMapping
    @Operation(
            summary = "Создать магазин",
            description = "Создаёт новый магазин и возвращает созданный объект"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Магазин успешно создан",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShopResponseDto.class)
            )
    )
    public ResponseEntity<ShopResponseDto> createShop(@RequestBody ShopRequestDto dto) {
        Shop shop = ShopMapper.toEntity(dto);
        Shop created = shopService.create(shop);
        return ResponseEntity.status(201).body(ShopMapper.toDto(created));
    }


    @GetMapping
    @Operation(
            summary = "Получить список магазинов",
            description = "Возвращает список всех магазинов"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список успешно получен",
            content = @Content(
                    mediaType = "application/json",
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = ShopResponseDto.class)
                    )
            )
    )
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<Shop> shops = shopService.getAll();
        return  ResponseEntity.ok(
                shops.stream()
                        .map(ShopMapper::toDto)
                        .toList()
        );
    }

}
