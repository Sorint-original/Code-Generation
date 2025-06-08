package com.bankapp.Backend.DTO;

import java.math.BigDecimal;

public class ApproveCustomerRequest {
    private Long customerId;
    private BigDecimal absoluteTransferLimit;
    private BigDecimal dailyTransferLimit;

    public ApproveCustomerRequest(long id, BigDecimal absoluteTransferLimit, BigDecimal dailyTransferLimit) {
        this.customerId = id;
        this.absoluteTransferLimit = absoluteTransferLimit;
        this.dailyTransferLimit = dailyTransferLimit;

    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAbsoluteTransferLimit() {
        return absoluteTransferLimit;
    }

    public BigDecimal getDailyTransferLimit() {
        return dailyTransferLimit;
    }
}
