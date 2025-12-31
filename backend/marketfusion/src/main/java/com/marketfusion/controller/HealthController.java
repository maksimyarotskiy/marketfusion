package com.marketfusion.controller;

import com.marketfusion.config.SecurityConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "Backend OK";
    }

    @GetMapping("/test-auth")
    public String testAuth() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
