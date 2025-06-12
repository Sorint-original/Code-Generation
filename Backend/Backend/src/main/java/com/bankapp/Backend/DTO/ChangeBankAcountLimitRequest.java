package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class ChangeBankAcountLimitRequest {

    private final String iban;
    private final BigDecimal dailyLimit;
    private final BigDecimal absoluteLimit;

    public ChangeBankAcountLimitRequest(String iban, BigDecimal dailyLimit, BigDecimal absoluteLimit) {
        this.iban = iban;
        this.dailyLimit = dailyLimit;
        this.absoluteLimit = absoluteLimit;
    }
    public String getIban() {
        return iban;

    }
    public BigDecimal getDailyLimit() {
        return dailyLimit;

    }
    public BigDecimal getAbsoluteLimit() {
        return absoluteLimit;
    }



}
