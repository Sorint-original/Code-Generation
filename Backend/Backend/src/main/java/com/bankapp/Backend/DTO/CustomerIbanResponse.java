package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.BankAccount;

public class CustomerIbanResponse {

    private String iban;
    private String accountType;

    public CustomerIbanResponse(String iban, String accountType) {
        this.iban = iban;
        this.accountType = accountType;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
