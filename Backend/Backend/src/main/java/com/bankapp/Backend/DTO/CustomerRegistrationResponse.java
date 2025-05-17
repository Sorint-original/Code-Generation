package com.bankapp.Backend.DTO;

public class CustomerRegistrationResponse {

    private String email;
    private String username;
    private boolean success;
    private String message;

    public CustomerRegistrationResponse() {}

    public CustomerRegistrationResponse(String email, String username, boolean success, String message) {
        this.email = email;
        this.username = username;
        this.success = success;
        this.message = message;
    }

    // Getters and setters

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return username; }
    public void setFullName(String fullName) { this.username = fullName; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
