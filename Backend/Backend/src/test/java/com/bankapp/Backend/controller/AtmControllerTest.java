package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.service.AtmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AtmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtmService atmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void withdraw_shouldReturnSuccessMessage() throws Exception {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("100"));
        String expectedResponse = "Withdrawal successful. New balance: 900";

        Mockito.when(atmService.withdraw(Mockito.any(AtmRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/atm/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void deposit_shouldReturnSuccessMessage() throws Exception {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("200"));
        String expectedResponse = "Deposit successful. New balance: 1200";

        Mockito.when(atmService.deposit(Mockito.any(AtmRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/atm/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}
