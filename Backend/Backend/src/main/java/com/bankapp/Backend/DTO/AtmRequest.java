package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class AtmRequest {

    private BigDecimal amount;

    // Getters and setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
