package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class ChangeDailyLimitRequest {

    private  String iban;
    private  BigDecimal DailyLimit;

    public ChangeDailyLimitRequest(String iban, BigDecimal DailyLimit) {
        this.iban = iban;
        this.DailyLimit = DailyLimit;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getDailyLimit() {
        return DailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        DailyLimit = dailyLimit;
    }
}
