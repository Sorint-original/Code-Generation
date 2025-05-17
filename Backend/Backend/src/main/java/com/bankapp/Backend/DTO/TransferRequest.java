package com.bankapp.Backend.dto;

import java.math.BigDecimal;

public class TransferRequest {

    private long FromAccountId;
    private long ToAccountId;
    private BigDecimal Amount;

    public TransferRequest(long fromAccountId, long toAccountId, BigDecimal amount) {
        FromAccountId = fromAccountId;
        ToAccountId = toAccountId;
        Amount = amount;
    }
    public long getFromAccountId() {
        return FromAccountId;
    }
    public void setFromAccountId(long fromAccountId) {
        FromAccountId = fromAccountId;
    }
    public long getToAccountId() {
        return ToAccountId;
    }
    public void setToAccountId(long toAccountId) {
        ToAccountId = toAccountId;
    }
    public BigDecimal getAmount() {
        return Amount;
    }
    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }
}