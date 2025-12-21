package com.marketfusion.service;

import com.marketfusion.entity.Sale;
import com.marketfusion.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;

    public Sale sale(Sale sale) {
        return saleRepository.save(sale);
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }
}
