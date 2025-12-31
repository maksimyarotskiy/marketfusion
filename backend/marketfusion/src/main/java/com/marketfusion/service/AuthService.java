package com.marketfusion.service;

import com.marketfusion.dto.auth.AuthResponseDto;
import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SellerRepository;
import com.marketfusion.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto login(String email, String password) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, seller.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(seller.getEmail());
        return new AuthResponseDto(token);
    }
}
