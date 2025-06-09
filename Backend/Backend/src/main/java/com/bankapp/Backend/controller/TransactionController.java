package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.RecipientAccount;
import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.exception.CustomerTransactionException;
import com.bankapp.Backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> transferFunds(@RequestBody TransactionRequest request) {
        transactionService.transferFunds(request);
        return ResponseEntity.ok("Transaction transferred successfully");
    }

    @PostMapping("/employee")
    public ResponseEntity<String> transferFundsEmployee(@RequestBody TransactionRequest request) {
        try {
            transactionService.transferFundsEmployee(request);
            return ResponseEntity.ok("✅ Employee transfer successful");
        } catch (CustomerTransactionException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("⚠️ Unexpected error");
        }
    }
}
