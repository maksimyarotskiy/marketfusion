package com.marketfusion.controller;

import com.marketfusion.security.JwtService;
import com.marketfusion.security.SecurityUtil;
import com.marketfusion.security.SellerDetailsService;
import com.marketfusion.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private SecurityUtil securityUtil;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SellerDetailsService sellerDetailsService;

    @Test
    void getSummaryCsv_returnsCsvWithAttachmentHeader() throws Exception {
        String csv = "productId,name,sku,totalQuantity,daysUntilOos\n1,Test,SKU-1,10,5.0\n";
        when(stockService.getStockSummaryCsv(null)).thenReturn(csv);

        mockMvc.perform(get("/api/stocks/summary.csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"stock-summary.csv\""))
                .andExpect(content().string(csv));
    }
}
