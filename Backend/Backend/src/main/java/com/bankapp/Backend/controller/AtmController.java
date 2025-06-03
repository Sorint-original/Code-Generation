package com.bankapp.Backend.controller;


import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.service.AtmService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atm")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody AtmRequest request) {
        return atmService.withdraw(request);
    }

    @PostMapping("/deposit")
    public String deposit(@RequestBody AtmRequest request) {
        return atmService.deposit(request);
    }
}
