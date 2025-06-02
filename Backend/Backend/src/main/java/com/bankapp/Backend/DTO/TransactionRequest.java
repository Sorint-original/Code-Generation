package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.AccountType;
import java.math.BigDecimal;

public class TransactionRequest {

    private String fromAccountIban;
    private String toAccountIban;
    private BigDecimal amount;
    private String initiatorEmail;
    private AccountType accountType;

    public TransactionRequest() {}

    public TransactionRequest(String fromAccountIban, String toAccountIban, BigDecimal amount) {
        this.fromAccountIban = fromAccountIban;
        this.toAccountIban = toAccountIban;
        this.amount = amount;
    }

    public String getFromAccountIban() {
        return fromAccountIban;
    }

    public void setFromAccountIban(String fromAccountIban) {
        this.fromAccountIban = fromAccountIban;
    }

    public String getToAccountIban() {
        return toAccountIban;
    }

    public void setToAccountIban(String toAccountIban) {
        this.toAccountIban = toAccountIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getInitiatorEmail() {
        return initiatorEmail;
    }

    public void setInitiatorEmail(String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
