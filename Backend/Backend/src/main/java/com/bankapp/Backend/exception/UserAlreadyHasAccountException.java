package com.bankapp.Backend.exception;

public class UserAlreadyHasAccountException extends RuntimeException {
    public UserAlreadyHasAccountException() {
        super("Customer already has accounts.");
    }
}