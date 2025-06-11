package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.BankAccount;

public class ChangeDailyLimitResponse {

    private BankAccount bankAccount;
    private boolean success;
    private String message;

    public ChangeDailyLimitResponse(BankAccount bankAccount, boolean success, String message) {
        this.bankAccount = bankAccount;
        this.success = success;
        this.message = message;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
