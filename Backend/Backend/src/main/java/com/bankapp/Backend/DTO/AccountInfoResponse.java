package com.bankapp.Backend.DTO;

import java.util.List;

import java.util.List;

public class AccountInfoResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private List<BankAccountResponse> bankAccounts;

    public AccountInfoResponse() {
    }

    public AccountInfoResponse(Long userId, String firstName, String lastName, String username,
                               String email, String phone, List<BankAccountResponse> bankAccounts) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.bankAccounts = bankAccounts;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<BankAccountResponse> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccountResponse> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }
}