package com.marketfusion.security;

import com.marketfusion.entity.Seller;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Seller getCurrentSeller() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof Seller seller) {
            return seller;
        }

        throw new IllegalStateException("No authenticated seller found");
    }

    public String getCurrentSellerEmail() {
        return getCurrentSeller().getEmail();
    }

    public Long getCurrentSellerId() {
        return getCurrentSeller().getId();
    }

}
