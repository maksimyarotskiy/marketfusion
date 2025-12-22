package com.marketfusion.service;


import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Seller create(String mail, String password) {
        sellerRepository.findByEmail(mail)
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Seller with this email already exists");
                });

        Seller seller = Seller.builder()
                .email(mail)
                .password(password)
                .createdAt(LocalDateTime.now())
                .build();

        return sellerRepository.save(seller);
    }

    public List<Seller> getAll() {
        return sellerRepository.findAll();
    }

    public Seller getById(Long id) {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
    }

}
