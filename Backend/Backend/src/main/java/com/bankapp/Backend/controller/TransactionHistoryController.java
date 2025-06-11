package com.bankapp.Backend.controller;

import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.EmployeeService;
import com.bankapp.Backend.service.TransactionService;
import com.bankapp.Backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactionHistory")
public class TransactionHistoryController {
    private final UserService userService;
    private final TransactionService transactionService;


    public TransactionHistoryController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    // Code for the employee transaction record
    @GetMapping("/fetchAllTransactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        return ResponseEntity.ok(this.transactionService.fetchTransactionHistory());
    }

    //code for current user transactions
    @GetMapping("/fetchLoggedUserTransactions")
    public ResponseEntity<List<Transaction>> getUnapprovedCustomers() {
        long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(transactionService.fetchUserTransactionHistory(userId));
    }

}
