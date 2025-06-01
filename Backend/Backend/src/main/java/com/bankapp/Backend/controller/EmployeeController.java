package com.bankapp.Backend.controller;

import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.BankAccountService;
import com.bankapp.Backend.service.EmployeeService;
import com.bankapp.Backend.service.TransactionService;
import com.bankapp.Backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private final UserService userService;
    private final EmployeeService employeeService;
    private final TransactionService transactionService;


    public EmployeeController(UserService userService, EmployeeService employeeService, TransactionService transactionService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.transactionService = transactionService;
    }

    @GetMapping("/unapproved-customers")
    public ResponseEntity<List<User>> getUnapprovedCustomers() {
        return ResponseEntity.ok(userService.findUnapprovedUsers(Role.CUSTOMER));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        return ResponseEntity.ok(user);
    }


    @PostMapping("/customers/{id}/approve")
    public ResponseEntity<?> approveCustomer(@PathVariable Long id) {
        User user = userService.findById(id);
        employeeService.approveCustomer(user);
        return ResponseEntity.ok("Customer approved and accounts created.");
        //hello
    }

    @PostMapping("/customers/{id}/decline")
    public ResponseEntity<?> declineCustomerStatus(@PathVariable Long id) {

        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        employeeService.updateUserStatus(user, CustomerStatus.Denied);
        return ResponseEntity.ok("User status updated to " + CustomerStatus.Denied);
    }

    // Code for the employee transaction record
    @GetMapping("/transaction-history")
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        return ResponseEntity.ok(this.transactionService.fetchTransactionHistory());
    }


}
