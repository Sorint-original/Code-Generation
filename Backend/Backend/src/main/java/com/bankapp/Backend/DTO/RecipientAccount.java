package com.bankapp.Backend.DTO;

public class RecipientAccount {
    private String fullName;
    private String iban;
    private String accountType;

    public RecipientAccount(String fullName, String iban, String accountType) {
        this.fullName = fullName;
        this.iban = iban;
        this.accountType = accountType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
