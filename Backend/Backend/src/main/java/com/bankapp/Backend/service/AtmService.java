package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.WithdrawRequest;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtmService {
    private final BankAccountRepository bankAccountRepository;
    private final JwtProvider jwtProvider;

    public AtmService(BankAccountRepository bankAccountRepository, JwtProvider jwtProvider) {
        this.bankAccountRepository = bankAccountRepository;
        this.jwtProvider = jwtProvider;
    }


    @Transactional
    public String withdraw(WithdrawRequest request) {
        Long userId = jwtProvider.getIdFromToken(request.getToken());
        BankAccount account = bankAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAmount().compareTo(request.getAmount()) < 0) {
            return "Insufficient funds";
        }

        account.setAmount(account.getAmount().subtract(request.getAmount()));
        bankAccountRepository.save(account);

        return "Withdrawal successful. New balance: " + account.getAmount();
    }
}
