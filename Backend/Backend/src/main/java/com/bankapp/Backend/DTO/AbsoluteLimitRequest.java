package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.BankAccount;

import java.math.BigDecimal;

public class AbsoluteLimitRequest {
    private String bankAccount;
    private  BigDecimal limit;

    public AbsoluteLimitRequest() {
    }

    public AbsoluteLimitRequest(String bankAccount, BigDecimal limit) {
        this.bankAccount = bankAccount;
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
}
