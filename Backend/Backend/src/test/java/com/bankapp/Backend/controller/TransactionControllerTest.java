package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.exception.CustomerTransactionException;
import com.bankapp.Backend.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransferFunds_Success() {
        TransactionRequest request = new TransactionRequest();

        ResponseEntity<String> response = transactionController.transferFunds(request);

        verify(transactionService, times(1)).transferFunds(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Transaction transferred successfully", response.getBody());
    }

    @Test
    void testTransferFundsEmployee_Success() {
        TransactionRequest request = new TransactionRequest();

        ResponseEntity<String> response = transactionController.transferFundsEmployee(request);

        verify(transactionService, times(1)).transferFundsEmployee(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("✅ Employee transfer successful", response.getBody());
    }

    @Test
    void testTransferFundsEmployee_CustomerTransactionException() {
        TransactionRequest request = new TransactionRequest();

        doThrow(new CustomerTransactionException("Insufficient funds"))
                .when(transactionService).transferFundsEmployee(request);

        ResponseEntity<String> response = transactionController.transferFundsEmployee(request);

        verify(transactionService, times(1)).transferFundsEmployee(request);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("❌ Insufficient funds", response.getBody());
    }

    @Test
    void testTransferFundsEmployee_UnexpectedException() {
        TransactionRequest request = new TransactionRequest();

        doThrow(new RuntimeException("Unexpected"))
                .when(transactionService).transferFundsEmployee(request);

        ResponseEntity<String> response = transactionController.transferFundsEmployee(request);

        verify(transactionService, times(1)).transferFundsEmployee(request);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("⚠️ Unexpected error", response.getBody());
    }
}
