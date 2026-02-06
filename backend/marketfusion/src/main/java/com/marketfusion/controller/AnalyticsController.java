package com.marketfusion.controller;

import com.marketfusion.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<BigDecimal> getRevenueLast30Days() {
        return ResponseEntity.ok(analyticsService.getRevenueLast30Days());
    }

    @Operation(summary = "Выручка по дням за последние 30 дней")
    @GetMapping("/daily-revenue")
    public ResponseEntity<Map<LocalDateTime, BigDecimal>> getDailyRevenueLast30Days() {
        return ResponseEntity.ok(analyticsService.getDailyRevenueLast30Days());
    }

    @Operation(summary = "Топ-N товаров по выручке")
    @GetMapping("/top-products")
    public ResponseEntity<List<AnalyticsService.TopProduct>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopProducts(limit));
    }

    @Operation(summary = "Топ-N товаров по выручке за период")
    @GetMapping("/top-products/custom")
    public ResponseEntity<List<AnalyticsService.TopProduct>> getTopProductsCustom(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(analyticsService.getTopProductsBetween(from, to, limit));
    }

    @Operation(summary = "Выручка за произвольный период")
    @GetMapping("/revenue/custom")
    public ResponseEntity<BigDecimal> getRevenueCustom(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getRevenueBetween(from, to));
    }

    @Operation(summary = "Выручка по дням за произвольный период")
    @GetMapping("/daily-revenue/custom")
    public ResponseEntity<Map<LocalDateTime, BigDecimal>> getDailyRevenueCustom(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getDailyRevenueBetween(from, to));
    }

    @Operation(summary = "Средний чек за произвольный период")
    @GetMapping("/average-check")
    public ResponseEntity<BigDecimal> getAverageCheck(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getAverageCheckBetween(from, to));
    }

    @Operation(summary = "Общее количество проданных товаров за период")
    @GetMapping("/total-items")
    public ResponseEntity<Long> getTotalItemsSold(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getTotalItemsSoldBetween(from, to));
    }

    @Operation(summary = "Выручка по платформам за произвольный период")
    @GetMapping("/revenue-by-platform")
    public ResponseEntity<Map<String, BigDecimal>> getRevenueByPlatform(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(analyticsService.getRevenueByPlatformBetween(from, to));
    }
}
