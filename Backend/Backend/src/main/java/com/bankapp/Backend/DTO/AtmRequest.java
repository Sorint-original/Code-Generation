package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class AtmRequest {
    public AtmRequest(String iban, BigDecimal amount) {
        this.iban = iban;
        this.amount = amount;
    }

    private String iban;
    private BigDecimal amount;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    // Getters and setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
