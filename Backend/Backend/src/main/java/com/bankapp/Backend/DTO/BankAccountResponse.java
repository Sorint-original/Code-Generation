package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

public class BankAccountResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private AccountType type;
    private String iban;
    private BigDecimal absoluteTransferLimit;
    private BigDecimal dailyTransferLimit;
    private AccountStatus status;

    public BankAccountResponse() {
    }

    public BankAccountResponse(Long id, Long userId, BigDecimal amount, AccountType type, String iban,
                               BigDecimal absoluteTransferLimit, BigDecimal dailyTransferLimit, AccountStatus status) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.iban = iban;
        this.absoluteTransferLimit = absoluteTransferLimit;
        this.dailyTransferLimit = dailyTransferLimit;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getAbsoluteTransferLimit() {
        return absoluteTransferLimit;
    }

    public void setAbsoluteTransferLimit(BigDecimal absoluteTransferLimit) {
        this.absoluteTransferLimit = absoluteTransferLimit;
    }

    public BigDecimal getDailyTransferLimit() {
        return dailyTransferLimit;
    }

    public void setDailyTransferLimit(BigDecimal dailyTransferLimit) {
        this.dailyTransferLimit = dailyTransferLimit;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}