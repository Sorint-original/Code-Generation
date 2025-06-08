package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.CustomerIbanRequest;
import com.bankapp.Backend.DTO.CustomerIbanResponse;
import com.bankapp.Backend.DTO.RecipientAccount;
import com.bankapp.Backend.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  // ✅ REQUIRED
@RequestMapping("/api/customer")  // ✅ REQUIRED
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/search-ibans")
    public ResponseEntity<List<CustomerIbanResponse>> getIbansByCustomerName(@RequestBody CustomerIbanRequest request) {
        List<CustomerIbanResponse> ibans = customerService.getIbansByName(request);
        return ResponseEntity.ok(ibans);
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecipientAccount>> searchRecipients(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        return ResponseEntity.ok(customerService.searchRecipientAccounts(query));
    }
}
