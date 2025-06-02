package com.bankapp.Backend.controller;


import com.bankapp.Backend.DTO.WithdrawRequest;
import com.bankapp.Backend.service.AtmService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody WithdrawRequest request) {
        return atmService.withdraw(request);
    }
}
