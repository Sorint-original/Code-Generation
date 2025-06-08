package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.CustomerIbanRequest;
import com.bankapp.Backend.DTO.CustomerIbanResponse;
import com.bankapp.Backend.DTO.DashboardStatusResponse;
import com.bankapp.Backend.service.CustomerService;
import com.bankapp.Backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // ✅ REQUIRED
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    public CustomerController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
    }

    @PostMapping("/search-ibans")
    public ResponseEntity<List<CustomerIbanResponse>> getIbansByCustomerName(@RequestBody CustomerIbanRequest request) {
        List<CustomerIbanResponse> ibans = customerService.getIbansByName(request);
        return ResponseEntity.ok(ibans);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatusResponse> getCustomerDashboardStatus() {
        DashboardStatusResponse response = userService.getCustomerDashboardStatus();
        return ResponseEntity.ok(response);
    }
}
