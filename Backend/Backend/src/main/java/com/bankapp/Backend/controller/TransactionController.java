package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionrService;

    @Autowired
    public TransactionController(TransactionService transactionrService) {
        this.transactionrService = transactionrService;
    }

    @PostMapping
    public ResponseEntity<String> transferFunds(@RequestBody TransactionRequest request) {
        try {
            transactionrService.transferFunds(request);
            return ResponseEntity.ok("✅ Transfer successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Error: " + request.getFromAccountIban() + " " + request.getToAccountIban() + " " + " " + request.getAmount() + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("⚠️ Unexpected error");
        }
    }
}
