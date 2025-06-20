package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.AbsoluteLimitRequest;
import com.bankapp.Backend.DTO.AccountInfoResponse;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.exception.*;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.security.MyUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IBANGenerator ibanGenerator;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.ibanGenerator = new IBANGenerator();
    }

    public BankAccount GetBankAccount(String iban) {
        return bankAccountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account with IBAN " + iban + " not found."));
    }

    public void validateLimits(BigDecimal dailyLimit, BigDecimal absoluteLimit){
        if (absoluteLimit == null || absoluteLimit.compareTo(BigDecimal.ZERO) <= 0 || dailyLimit == null || dailyLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferLimitException("Daily limit must be greater than 0 and not null.");
        }

        if (absoluteLimit.compareTo(dailyLimit) >= 0) {
            throw new InvalidTransferLimitException("Daily limit must be greater than the absolute transfer limit.");
        }
    }

    public void ChangeBankAccountLimits(BankAccount bankAccount, BigDecimal dailyLimit, BigDecimal absoluteLimit) {
        validateLimits(dailyLimit, absoluteLimit);
        bankAccountRepository.updateDailyLimitByIban(bankAccount.getIban(), dailyLimit);
        bankAccountRepository.updateAbsoluteLimitByIban(bankAccount.getIban(), absoluteLimit);
    }


    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAllBankAccounts();
    }

    public List<BankAccount> getBankAccountsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<BankAccount> accounts = bankAccountRepository.findBankAccountsByUserId(userId);

        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for user ID " + userId);
        }

        return accounts;
    }

    public void updateAccountStatus(String iban, AccountStatus status) {
        if (!bankAccountRepository.existsByIban(iban)) {
            throw new AccountNotFoundException("Cannot update status. Account with IBAN " + iban + " not found.");
        }
        bankAccountRepository.updateStatusByIban(iban, status);
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof MyUserDetails) {
            return ((MyUserDetails) auth.getPrincipal()).getUserId();
        }

        throw new UnauthorizedActionException("You must be logged in to perform this action.");
    }

    public List<BankAccountResponse> accountsToResponses(List<BankAccount> accounts) {
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

    public AccountInfoResponse infoToResponse(List<BankAccount> accounts) {
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

