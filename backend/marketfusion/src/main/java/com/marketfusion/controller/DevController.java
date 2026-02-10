package com.marketfusion.controller;


import com.marketfusion.generator.FakeSaleGenerator;
import com.marketfusion.generator.FakeStockGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final FakeSaleGenerator fakeSaleGenerator;
    private final FakeStockGenerator fakeStockGenerator;

    @PostMapping("/generate-sales")
    public ResponseEntity<Void> generateSales(@RequestParam(defaultValue = "10") int perProduct) {
        fakeSaleGenerator.generate(perProduct);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-stocks")
    public ResponseEntity<Void> generateStocks(
            @RequestParam(defaultValue = "0") int min,
            @RequestParam(defaultValue = "200") int max,
            @RequestParam(defaultValue = "WB_MAIN,OZON_FBS") String warehouses) {

        List<String> wh = Arrays.stream(warehouses.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        fakeStockGenerator.generate(min, max, wh);
        return ResponseEntity.noContent().build();
    }
}
