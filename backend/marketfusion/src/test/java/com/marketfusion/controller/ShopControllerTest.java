package com.marketfusion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketfusion.dto.shop.ShopRequestDto;
import com.marketfusion.entity.Platform;
import com.marketfusion.entity.Seller;
import com.marketfusion.entity.Shop;
import com.marketfusion.security.JwtService;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.security.SellerDetailsService;
import com.marketfusion.service.ShopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopService shopService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityUtil securityUtil;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SellerDetailsService sellerDetailsService;


    @Test
    void createShop_success() throws Exception {
        // given
        ShopRequestDto requestDto = new ShopRequestDto();
        requestDto.setName("Wildberries Main Store");
        requestDto.setPlatform(Platform.WB);
        requestDto.setApiKey("super-secret-key-123");
        Seller currentSeller = Seller.builder().id(10L).email("seller@test.com").build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);

        Shop createdShop = Shop.builder()
                .id(1L)
                .name(requestDto.getName())
                .apiKey(requestDto.getApiKey())
                .platform(requestDto.getPlatform())
                .seller(currentSeller)
                .build();

        when(shopService.create(any(Shop.class))).thenReturn(createdShop);

        // when + then
        mockMvc.perform(post("/api/shops")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Wildberries Main Store"))
                .andExpect(jsonPath("$.platform").value("WB"));
    }

    @Test
    void getAllShops_success() throws Exception {
        // given
        Seller currentSeller = Seller.builder().id(10L).build();
        when(securityUtil.getCurrentSeller()).thenReturn(currentSeller);

        Shop shop1 = Shop.builder()
                .id(1L)
                .name("WB Store")
                .platform(Platform.WB)
                .seller(currentSeller)
                .build();

        Shop shop2 = Shop.builder()
                .id(2L)
                .name("Ozon Store")
                .platform(Platform.OZON)
                .seller(currentSeller)
                .build();

        when(shopService.getAllForCurrentSeller()).thenReturn(List.of(shop1, shop2));

        // when + then
        mockMvc.perform(get("/api/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("WB Store"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].platform").value("OZON"));
    }
}
