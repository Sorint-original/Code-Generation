package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class ChangeBankAcountsRequest {

    private final String iban;
    private final BigDecimal dailyLimit;
    private final BigDecimal absoluteLimit;

    public ChangeBankAcountsRequest(String iban, BigDecimal dailyLimit, BigDecimal absoluteLimit) {
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
