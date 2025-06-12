package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.service.AtmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AtmControllerTest {

    @Mock
    private AtmService atmService;

    @InjectMocks
    private AtmController atmController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWithdraw_ReturnsSuccessMessage() {
        // Arrange
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("100"));
        when(atmService.withdraw(request)).thenReturn("Withdrawal successful. New balance: 900");

        // Act
        String result = atmController.withdraw(request);

        // Assert
        assertEquals("Withdrawal successful. New balance: 900", result);
        verify(atmService).withdraw(request);
    }

    @Test
    void testDeposit_ReturnsSuccessMessage() {
        // Arrange
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("200"));
        when(atmService.deposit(request)).thenReturn("Deposit successful. New balance: 1200");

        // Act
        String result = atmController.deposit(request);

        // Assert
        assertEquals("Deposit successful. New balance: 1200", result);
        verify(atmService).deposit(request);
    }
}
