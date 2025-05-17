package com.bankapp.Backend.controller;

import com.bankapp.Backend.dto.TransferRequest;
import com.bankapp.Backend.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequest request) {
        try {
            transferService.transferFunds(request);
            return ResponseEntity.ok("✅ Transfer successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("⚠️ Unexpected error");
        }
    }
}
