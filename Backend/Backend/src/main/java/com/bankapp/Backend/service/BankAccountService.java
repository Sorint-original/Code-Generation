package com.bankapp.Backend.service;

import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IBANGenerator ibanGenerator;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.ibanGenerator = new IBANGenerator();
    }

    public BankAccount createDefaultBankAccount(User user) {
        BankAccount bankAccount = new BankAccount(user);
        bankAccount.setIban(ibanGenerator.generateDutchIBAN());
        return bankAccountRepository.save(bankAccount);
    }


}
