package com.marketfusion.dto.seller;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerResponseDto {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
