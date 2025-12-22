package com.marketfusion.mapper;

import com.marketfusion.dto.seller.SellerRequestDto;
import com.marketfusion.dto.seller.SellerResponseDto;
import com.marketfusion.entity.Seller;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class SellerMapper {

    public Seller toEntity(SellerRequestDto dto) {
        Seller seller = new Seller();
        seller.setEmail(dto.getEmail());
        seller.setPassword(dto.getPassword());
        seller.setCreatedAt(LocalDateTime.now());
        return seller;
    }

    public SellerResponseDto toDto(Seller seller) {
        SellerResponseDto dto = new SellerResponseDto();
        dto.setId(seller.getId());
        dto.setEmail(seller.getEmail());
        dto.setCreatedAt(seller.getCreatedAt());
        return dto;
    }
}
