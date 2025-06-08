package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.RecipientAccount;
import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.TransactionRepository;
import com.bankapp.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(BankAccountRepository bankAccountRepository,
                              TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void transferFunds(TransactionRequest request) {
        User initiator = userRepository.findUserByEmail(request.getInitiatorEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BankAccount from;
        BankAccount to;

        if (initiator.getRole() == Role.CUSTOMER) {
            from = getCustomerAccountByType(initiator, request.getAccountType());
            to = findAccountByIban(request.getToAccountIban(), "To account not found");

            if (from.getIban().equals(to.getIban())) {
                throw new IllegalArgumentException("Customer cannot transfer to their own account.");
            }

        } else if (initiator.getRole() == Role.EMPLOYEE) {
            from = findAccountByIban(request.getFromAccountIban(), "From account not found");
            to = findAccountByIban(request.getToAccountIban(), "To account not found");
        } else {
            throw new IllegalArgumentException("Unknown role.");
        }

        validateTransfer(from, to, request.getAmount());
        performTransfer(from, to, request.getAmount());
        saveTransaction(from, to, request.getAmount(), initiator);
    }

    @Transactional
    public void transferFundsEmployee(TransactionRequest request) {
        BankAccount from = findAccountByIban(request.getFromAccountIban(), "From account not found");
        BankAccount to = findAccountByIban(request.getToAccountIban(), "To account not found");

        if (from.getType() == AccountType.CHECKING && to.getType() == AccountType.CHECKING) {
            validateTransfer(from, to, request.getAmount());
            performTransfer(from, to, request.getAmount());
            saveTransaction(from, to, request.getAmount(), userRepository.findUserByEmail(request.getInitiatorEmail()).get());
        } else {
            throw new IllegalArgumentException("Cannot transfer to or from Saving account.");
        }
    }
    private BankAccount findAccountByIban(String iban, String errorMessage) {
        return bankAccountRepository.findByIban(iban)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage + ": " + iban));
    }

    private BankAccount getCustomerAccountByType(User user, AccountType type) {
        return user.getBankAccounts().stream()
                .filter(account -> account.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No account of type " + type + " found for user."));
    }

    private void validateTransfer(BankAccount from, BankAccount to, BigDecimal amount) {
        if (from.getIban().equals(to.getIban())) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        if (amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("Transfer amount must be at least €0.01.");
        }

        if (from.getAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        if (amount.compareTo(from.getAbsoluteTransferLimit()) > 0) {
            throw new IllegalArgumentException("Transfer exceeds absolute transfer limit.");
        }

        BigDecimal totalToday = transactionRepository.getTotalTransferredToday(from);
        if (totalToday.add(amount).compareTo(from.getDailyTransferLimit()) > 0) {
            throw new IllegalArgumentException("Transfer exceeds daily limit. Already sent €" + totalToday + " today.");
        }
    }

    private void performTransfer(BankAccount from, BankAccount to, BigDecimal amount) {
        from.setAmount(from.getAmount().subtract(amount));
        to.setAmount(to.getAmount().add(amount));
        bankAccountRepository.save(from);
        bankAccountRepository.save(to);
    }

    private void saveTransaction(BankAccount from, BankAccount to, BigDecimal amount, User initiator) {
        Transaction txn = new Transaction(from, to, amount, initiator);
        transactionRepository.save(txn);
    }

    public List<Transaction> fetchTransactionHistory() {
        return transactionRepository.findAllTransactions();
    }

    public List<Transaction> fetchUserTransactionHistory(long userId) {
        return transactionRepository.findAllUserRelatedTransactions(userId);
    }
}
