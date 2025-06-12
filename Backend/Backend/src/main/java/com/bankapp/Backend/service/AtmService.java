package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.TransactionRepository;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.bankapp.Backend.security.AuthUtils.getCurrentUserId;

@Service
public class AtmService {
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    public AtmService(BankAccountRepository bankAccountRepository, JwtProvider jwtProvider, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public String withdraw(AtmRequest request) {
        Long userId = getCurrentUserId();
        BankAccount account = bankAccountRepository.findByIban(request.getIban())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getUser().getId() != (userId)) {
            throw new RuntimeException("Unauthorized access to this account");
        }

        if (account.getAmount().compareTo(request.getAmount()) < 0) {
            return "Insufficient funds";
        }

        account.setAmount(account.getAmount().subtract(request.getAmount()));
        bankAccountRepository.save(account);


        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Transaction txn = new Transaction();
        txn.setAmount(request.getAmount());
        txn.setDate(LocalDateTime.now());
        txn.setFromAccount(account);
        txn.setToAccount(null); // ATM withdrawal, so no destination account
        txn.setInitiatingUser(user);
        transactionRepository.save(txn);

        return "Withdrawal successful. New balance: " + account.getAmount();
    }

    @Transactional
    public String deposit(AtmRequest request) {
        Long userId = getCurrentUserId();
        BankAccount account = bankAccountRepository.findByIban(request.getIban())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getUser().getId() != (userId)) {
            throw new RuntimeException("Unauthorized access to this account");
        }

        account.setAmount(account.getAmount().add(request.getAmount()));
        bankAccountRepository.save(account);

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Transaction txn = new Transaction();
        txn.setAmount(request.getAmount());
        txn.setDate(LocalDateTime.now());
        txn.setFromAccount(null);
        txn.setToAccount(account);
        txn.setInitiatingUser(user);
        transactionRepository.save(txn);

        return "Deposit successful. New balance: " + account.getAmount();
    }
}
