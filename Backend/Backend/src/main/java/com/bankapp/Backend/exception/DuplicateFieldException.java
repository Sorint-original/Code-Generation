package com.bankapp.Backend.exception;

public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String field, String value) {
        super(field + " '" + value + "' is already in use.");
    }
}