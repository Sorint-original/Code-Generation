package com.bankapp.Backend.DTO;

import com.bankapp.Backend.model.User;

public class CustomerRegistrationResponse {

    private User user;
    private boolean success;
    private String message;

    public CustomerRegistrationResponse() {}

    public CustomerRegistrationResponse(User user, boolean success, String message) {
        this.user = user;
        this.success = success;
        this.message = message;
    }

    // Getters and setters


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
