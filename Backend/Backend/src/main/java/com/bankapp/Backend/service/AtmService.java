package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.security.JwtProvider;
import com.bankapp.Backend.security.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bankapp.Backend.security.AuthUtils.getCurrentUserId;

@Service
public class AtmService {
    private final BankAccountRepository bankAccountRepository;

    public AtmService(BankAccountRepository bankAccountRepository, JwtProvider jwtProvider) {
        this.bankAccountRepository = bankAccountRepository;
    }


    @Transactional
    public String withdraw(AtmRequest request) {
        Long userId = getCurrentUserId();
        BankAccount account = bankAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAmount().compareTo(request.getAmount()) < 0) {
            return "Insufficient funds";
        }

        account.setAmount(account.getAmount().subtract(request.getAmount()));
        bankAccountRepository.save(account);

        return "Withdrawal successful. New balance: " + account.getAmount();
    }

    @Transactional
    public String deposit(AtmRequest request) {
        Long userId = getCurrentUserId();
        BankAccount account = bankAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));


        account.setAmount(account.getAmount().add(request.getAmount()));
        bankAccountRepository.save(account);

        return "Deposit successful. New balance: " + account.getAmount();
    }
}
