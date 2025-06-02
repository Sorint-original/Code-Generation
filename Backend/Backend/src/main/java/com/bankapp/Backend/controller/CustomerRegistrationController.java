package com.bankapp.Backend.controller;


import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/register")
public class CustomerRegistrationController {

    private final UserService userService;

    public CustomerRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CustomerRegistrationResponse> registerCustomer(
            @RequestBody CustomerRegistrationRequest request) {
        CustomerRegistrationResponse response = userService.registerCustomer(request);
        return ResponseEntity.status(201).body(response);
    }
}

