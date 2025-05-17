package com.bankapp.Backend.service;

import com.bankapp.Backend.dto.TransferRequest;
import com.bankapp.Backend.model.Account;
import com.bankapp.Backend.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    @Autowired
    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transferFunds(TransferRequest request) {
        Optional<Account> fromOpt = accountRepository.findById(request.getFromAccountId());
        Optional<Account> toOpt = accountRepository.findById(request.getToAccountId());

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            throw new IllegalArgumentException("One or more accounts are not found");
        }

        Account from = fromOpt.get();
        Account to = toOpt.get();

        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        if (!"checking".equalsIgnoreCase(from.getAccountType()) ||
                !"checking".equalsIgnoreCase(to.getAccountType())) {
            throw new IllegalArgumentException("Transfers only allowed between checking accounts.");
        }

        if (amount.compareTo(from.getAbsoluteTransferLimit()) > 0) {
            throw new IllegalArgumentException("Transfer exceeds absolute transfer limit.");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
