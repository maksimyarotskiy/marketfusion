package com.marketfusion.controller;

import com.marketfusion.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Выручка за последние 30 дней")
    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenueLast30Days() {
        return ResponseEntity.ok(analyticsService.getRevenueLast30Days());
    }

    @Operation(summary = "Выручка по дням за последние 30 дней")
    @GetMapping("/daily-revenue")
    public ResponseEntity<Map<LocalDateTime, Double>> getDailyRevenueLast30Days() {
        return ResponseEntity.ok(analyticsService.getDailyRevenueLast30Days());
    }

    @Operation(summary = "Топ-N товаров по выручке")
    @GetMapping("/top-products")
    public ResponseEntity<List<AnalyticsService.TopProduct>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopProducts(limit));
    }
}
