package com.marketfusion.controller;


import com.marketfusion.generator.FakeSaleGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final FakeSaleGenerator fakeSaleGenerator;

    @PostMapping("/generate-sales")
    public ResponseEntity<Void> generateSales(@RequestParam(defaultValue = "10") int perProduct) {
        fakeSaleGenerator.generate(perProduct);
        return ResponseEntity.noContent().build();
    }
}
