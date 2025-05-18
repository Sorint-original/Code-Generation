package com.bankapp.Backend.model;

import jakarta.persistence.*;

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
    private double balance;

    @Column(name = "account_type", nullable=false)
    private String accountType; // e.g., "checking", "savings", "debit", "credit"

    @Column(name = "daily_tansfer_limit", nullable = false)
    private double dailyTransferLimit;

    @Column(name = "absolute_transfer_limit", nullable = false)
    private  double absoluteTransferLimit;

    @Column(nullable = false)
    private String status; // e.g., "active", "unapproved", "frozen"

    public Account() {}

    public Account(User user, String iban, double balance, String accountType,
                   double dailyTransferLimit, double absoluteTransferLimit, String status) {
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

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public double getDailyTransferLimit() { return dailyTransferLimit; }
    public void setDailyTransferLimit(double dailyTransferLimit) { this.dailyTransferLimit = dailyTransferLimit; }

    public double getAbsoluteTransferLimit() { return absoluteTransferLimit; }
    public void setAbsoluteTransferLimit(double absoluteTransferLimit) { this.absoluteTransferLimit = absoluteTransferLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}