package com.bankapp.Backend.exception;

public class CustomerTransactionException extends RuntimeException {
    public CustomerTransactionException(String message) {
        super(message);
    }
}
