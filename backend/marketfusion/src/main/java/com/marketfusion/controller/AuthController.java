package com.marketfusion.controller;

import com.marketfusion.dto.auth.AuthResponseDto;
import com.marketfusion.dto.auth.LoginRequestDto;
import com.marketfusion.dto.auth.RegisterRequestDto;
import com.marketfusion.entity.Seller;
import com.marketfusion.security.JwtAuthenticationFilter;
import com.marketfusion.security.JwtService;
import com.marketfusion.service.AuthService;
import com.marketfusion.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SellerService sellerService;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto dto) {
        Seller seller = sellerService.create(dto.getEmail(), dto.getPassword());
        String token = jwtService.generateToken(dto.getEmail());
        return new AuthResponseDto(token);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        return authService.login(dto.getEmail(), dto.getPassword());
    }

}
