package com.marketfusion.controller;

import com.marketfusion.dto.stock.StockSummaryDto;
import com.marketfusion.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "Сводка остатков по товарам")
    @GetMapping("/summary")
    public ResponseEntity<List<StockSummaryDto>> getSummary(
            @RequestParam(required = false) Long shopId) {
        return ResponseEntity.ok(stockService.getStockSummary(shopId));
    }

    @Operation(summary = "Сводка остатков по товарам (CSV)")
    @GetMapping(value = "/summary.csv", produces = "text/csv")
    public ResponseEntity<String> getSummaryCsv(
            @RequestParam(required = false) Long shopId) {
        String csv = stockService.getStockSummaryCsv(shopId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"stock-summary.csv\"")
                .body(csv);
    }
}
