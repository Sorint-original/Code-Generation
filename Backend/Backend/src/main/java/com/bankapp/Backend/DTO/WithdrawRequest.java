package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class WithdrawRequest {

    private String token;
    private BigDecimal amount;

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
