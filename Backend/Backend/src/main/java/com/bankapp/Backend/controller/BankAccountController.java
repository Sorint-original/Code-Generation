package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.AbsoluteLimitRequest;
import com.bankapp.Backend.DTO.AccountInfoResponse;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.security.JwtProvider;
import com.bankapp.Backend.service.BankAccountService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/account")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/info")
    public ResponseEntity<AccountInfoResponse> getAccountInfo() {
        List<BankAccount> accounts = bankAccountService.getBankAccountsByUserId(bankAccountService.getCurrentUserId());
        return ResponseEntity.ok(bankAccountService.infoToResponse(accounts));
    }



}