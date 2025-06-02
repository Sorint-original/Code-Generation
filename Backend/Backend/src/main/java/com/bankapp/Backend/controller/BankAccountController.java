package com.bankapp.Backend.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/account")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    // ✅ Get all bank accounts
    @GetMapping("/all")
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts() {
        List<BankAccount> accounts = bankAccountService.getAllBankAccounts();
        return ResponseEntity.ok(accountsToResponses(accounts));
    }

    // ✅ Get account info (user + account list)
    @GetMapping("/info/{userId}")
    public ResponseEntity<AccountInfoResponse> getAccountInfo(@PathVariable Long userId) {
        List<BankAccount> accounts = bankAccountService.getBankAccountsByUserId(userId);
        return ResponseEntity.ok(infoToResponse(accounts));
    }

    // ✅ Update account status by IBAN
    @PutMapping("close/{iban}")
    public ResponseEntity<Void> updateAccountStatus(@PathVariable String iban) {
        bankAccountService.updateAccountStatus(iban, AccountStatus.BLOCKED);
        return ResponseEntity.ok().build();
    }

    private List<BankAccountResponse> accountsToResponses(List<BankAccount> accounts) {
        List<BankAccountResponse> responses = new ArrayList<>();

        for (BankAccount account : accounts) {
            BankAccountResponse response = new BankAccountResponse(account.getId(),
                    account.getUser().getId(),
                    account.getAmount(),
                    account.getAccountType(),
                    account.getIban(),
                    account.getAbsoluteTransferLimit(),
                    account.getDailyTransferLimit(),
                    account.getStatus());
            responses.add(response);
        }

        return responses;
    }

    private AccountInfoResponse infoToResponse(List<BankAccount> accounts) {
        User user = accounts.get(0).getUser();
        AccountInfoResponse response = new AccountInfoResponse(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                accountsToResponses(accounts));
        return response;
    }
}