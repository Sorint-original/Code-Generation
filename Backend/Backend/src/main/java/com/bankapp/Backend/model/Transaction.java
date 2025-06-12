package com.bankapp.Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who is sending the money
    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id")
    private BankAccount fromAccount;

    // Who is receiving the money
    @ManyToOne(optional = true)
    @JoinColumn(name = "to_account_id", nullable = true)
    private BankAccount toAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    // Who initiated the transaction (employee or customer)
    @ManyToOne(optional = false)
    @JoinColumn(name = "initiating_user_id")
    private User initiatingUser;

    public Transaction() {
    }

    public Transaction(BankAccount fromAccount, BankAccount toAccount, BigDecimal amount, User initiatingUser) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.initiatingUser = initiatingUser;
        this.date = LocalDateTime.now(); // default to now
    }

    public Long getId() {
        return id;
    }

    public BankAccount getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(BankAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public BankAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(BankAccount toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getInitiatingUser() {
        return initiatingUser;
    }

    public void setInitiatingUser(User initiatingUser) {
        this.initiatingUser = initiatingUser;
    }
}
