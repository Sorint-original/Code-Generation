package com.bankapp.Backend.DTO;


public class CustomerRegistrationRequest {

    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private String bsnNumber;

    public CustomerRegistrationRequest() {}

    public CustomerRegistrationRequest(String firstName, String lastName, String userName,
                                       String email, String password, String phoneNumber, String bsnNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.bsnNumber = bsnNumber;
    }

    // Getters and setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBsnNumber() { return bsnNumber; }
    public void setBsnNumber(String bsnNumber) { this.bsnNumber = bsnNumber; }
}
