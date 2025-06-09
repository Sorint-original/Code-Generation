package com.bankapp.Backend.exception;

public class InvalidTransferLimitException extends RuntimeException {
    public InvalidTransferLimitException(String message) {
        super(message);
    }
}
