package com.bankapp.Backend.model;



import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "bsn_number", unique = true)
    private String bsnNumber;

    @Enumerated(EnumType.STRING)

    @Column(name = "status")
    private CustomerStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts = new ArrayList<>();


    public User() {}

    public User(String firstName, String lastName, String userName, Role role, LocalDate dateOfBirth,
                String email, String password, String phoneNumber, String bsnNumber, CustomerStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.role = role;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.bsnNumber = bsnNumber;
        this.status = status;
    }
    public boolean isEmployee() {
        return role == Role.EMPLOYEE;
    }
    public long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBsnNumber() { return bsnNumber; }
    public void setBsnNumber(String bsnNumber) { this.bsnNumber = bsnNumber; }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }
    public void setId(long id) { this.id = id; }
}

