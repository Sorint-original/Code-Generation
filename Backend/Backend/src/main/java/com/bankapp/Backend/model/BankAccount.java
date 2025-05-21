package com.bankapp.Backend.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private double amount = 0.0;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(unique = true, nullable = false)
    private String iban;

    private double absoluteTransferLimit;

    private double dailyTransferLimit;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public BankAccount() {
    }

    public BankAccount(User user, double amount, AccountType type, String iban,
                       double absoluteTransferLimit, double dailyTransferLimit, AccountStatus status) {
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
        this.amount = 0.0;
        this.type = accountType;
        this.iban = iban; //i have it not so i can set it later using the iban generator
        this.absoluteTransferLimit = 1000.0;
        this.dailyTransferLimit = 5000.0;
        this.status = AccountStatus.APPROVED;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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

    public double getAbsoluteTransferLimit() {
        return absoluteTransferLimit;
    }

    public void setAbsoluteTransferLimit(double absoluteTransferLimit) {
        this.absoluteTransferLimit = absoluteTransferLimit;
    }

    public double getDailyTransferLimit() {
        return dailyTransferLimit;
    }

    public void setDailyTransferLimit(double dailyTransferLimit) {
        this.dailyTransferLimit = dailyTransferLimit;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
