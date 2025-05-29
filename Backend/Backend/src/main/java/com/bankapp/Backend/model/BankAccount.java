package com.bankapp.Backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal amount = BigDecimal.ZERO.setScale(2);

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(unique = true, nullable = false)
    private String iban;

    private BigDecimal absoluteTransferLimit;

    private BigDecimal dailyTransferLimit;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public BankAccount() {
    }

    public BankAccount(User user, BigDecimal amount, AccountType type, String iban,
                       BigDecimal absoluteTransferLimit, BigDecimal dailyTransferLimit, AccountStatus status) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.iban = iban;
        this.absoluteTransferLimit = absoluteTransferLimit;
        this.dailyTransferLimit = dailyTransferLimit;
        this.status = status;
    }
    public BankAccount(User user, AccountType accountType, String iban) {
        this.user = user;
        this.amount = BigDecimal.ZERO.setScale(2);
        this.type = accountType;
        this.iban = iban; //i have it not so i can set it later using the iban generator
        this.absoluteTransferLimit = new BigDecimal("0.01");
        this.dailyTransferLimit = new BigDecimal("1000.00");
        this.status = AccountStatus.UNAPPROVED;
    }



    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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


    public AccountType getAccountType() {
        return type;
    }
}
