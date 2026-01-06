package com.marketfusion.security;

import com.marketfusion.entity.Seller;
import com.marketfusion.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerDetailsService implements UserDetailsService {

    private final SellerRepository sellerRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found with email: " + email));
    }
}
