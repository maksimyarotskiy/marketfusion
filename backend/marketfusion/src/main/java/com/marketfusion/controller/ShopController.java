package com.marketfusion.controller;


import com.marketfusion.dto.shop.ShopRequestDto;
import com.marketfusion.dto.shop.ShopResponseDto;
import com.marketfusion.entity.Shop;
import com.marketfusion.mapper.ShopMapper;
import com.marketfusion.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<ShopResponseDto> createShop(@RequestBody ShopRequestDto dto) {
        Shop shop = ShopMapper.toEntity(dto);
        Shop created = shopService.create(shop);
        return ResponseEntity.status(201).body(ShopMapper.toDto(created));
    }

    @GetMapping
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<Shop> shops = shopService.getAllForCurrentSeller();
        return ResponseEntity.ok(shops.stream()
                .map(ShopMapper::toDto)
                .toList());
    }

}
