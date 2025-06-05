package com.bankapp.Backend.service;

import com.bankapp.Backend.exception.*;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.security.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public void approveCustomer(User user) {
        if (user.getRole() != Role.CUSTOMER) {
            throw new InvalidUserRoleException("Only customers can be approved.");
        }

        if (!user.getBankAccounts().isEmpty()) {
            throw new AccountAlreadyExistsException("Customer already has existing accounts.");
        }

        BankAccount checking = new BankAccount(user, AccountType.CHECKING, ibanGenerator.generateDutchIBAN());
        BankAccount savings = new BankAccount(user, AccountType.SAVINGS, ibanGenerator.generateDutchIBAN());

        bankAccountRepository.save(checking);
        bankAccountRepository.save(savings);
    }

    public void changeDailyLimit(BankAccount bankAccount, BigDecimal newLimit) {
        bankAccountRepository.updateDailyLimitByIban(bankAccount.getIban(), newLimit);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAllBankAccounts();
    }

    public List<BankAccount> getBankAccountsByUserId(Long userId) {
        return bankAccountRepository.findBankAccountsByUserId(userId);
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
}
