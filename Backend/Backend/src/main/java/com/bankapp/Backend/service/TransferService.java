package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.TransferRequest;
import com.bankapp.Backend.model.Account;
import com.bankapp.Backend.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            throw new IllegalArgumentException("one of the accounts does not exist" + request.getFromAccountId() + request.getToAccountId());

        }

        Account from = fromOpt.get();
        Account to = toOpt.get();

        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        double amount = request.getAmount();

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }

        if (from.getBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        if (amount > from.getBalance()) {
            throw new IllegalArgumentException("Transfer exceeds absolute transfer limit.");
        }

        from.setBalance(from.getBalance());
        to.setBalance(to.getBalance());

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
