package com.bankapp.Backend.DTO;

public class WithdrawRequest {

    private String token;
    private double amount;

    // Getters and setters
    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
