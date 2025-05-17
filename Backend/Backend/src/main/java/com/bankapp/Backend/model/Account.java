package com.bankapp.Backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name= "accounts")

public class Account
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String iban;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "account_type", nullable=false)
    private String accountType; // e.g., "checking", "savings", "debit", "credit"

    @Column(name = "daily_tansfer_limit", nullable = false)
    private BigDecimal dailyTransferLimit;

    @Column(name = "absolute_transfer_limit", nullable = false)
    private  BigDecimal absoluteTransferLimit;

    @Column(nullable = false)
    private String status; // e.g., "active", "unapproved", "frozen"

    public Account() {}

    public Account(User user, String iban, BigDecimal balance, String accountType,
                   BigDecimal dailyTransferLimit, BigDecimal absoluteTransferLimit, String status) {
        this.user = user;
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.dailyTransferLimit = dailyTransferLimit;
        this.absoluteTransferLimit = absoluteTransferLimit;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getDailyTransferLimit() { return dailyTransferLimit; }
    public void setDailyTransferLimit(BigDecimal dailyTransferLimit) { this.dailyTransferLimit = dailyTransferLimit; }

    public BigDecimal getAbsoluteTransferLimit() { return absoluteTransferLimit; }
    public void setAbsoluteTransferLimit(BigDecimal absoluteTransferLimit) { this.absoluteTransferLimit = absoluteTransferLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}