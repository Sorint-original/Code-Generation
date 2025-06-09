package com.bankapp.Backend.controller;

import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.TransactionService;
import com.bankapp.Backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionHistoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionHistoryController transactionHistoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionHistoryController).build();
    }

    @Test
    void getTransactionHistory_ReturnsAllTransactions() throws Exception {
        // Arrange
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        List<Transaction> transactions = Arrays.asList(t1, t2);

        when(transactionService.fetchTransactionHistory()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactionHistory/fetchAllTransactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(transactionService, times(1)).fetchTransactionHistory();
    }

    @Test
    void getTransactionHistory_ReturnsEmptyList() throws Exception {
        // Arrange
        when(transactionService.fetchTransactionHistory()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/transactionHistory/fetchAllTransactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getLoggedUserTransactions_ReturnsUserTransactions() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setId(1L);
        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        t1.setInitiatingUser(testUser);
        t2.setInitiatingUser(testUser);
        List<Transaction> userTransactions = Arrays.asList(t1, t2);

        when(userService.getCurrentUserId()).thenReturn(testUser.getId());
        when(transactionService.fetchUserTransactionHistory(testUser.getId())).thenReturn(userTransactions);

        // Act & Assert
        mockMvc.perform(get("/api/transactionHistory/fetchLoggedUserTransactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService, times(1)).getCurrentUserId();
        verify(transactionService, times(1)).fetchUserTransactionHistory(testUser.getId());
    }

    @Test
    void getLoggedUserTransactions_ReturnsEmptyList() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setId(1L);

        when(userService.getCurrentUserId()).thenReturn(testUser.getId());
        when(transactionService.fetchUserTransactionHistory(testUser.getId())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/transactionHistory/fetchLoggedUserTransactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}