package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.ChangeDailyLimitRequest;
import com.bankapp.Backend.DTO.ChangeDailyLimitResponse;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.service.BankAccountService;
import com.bankapp.Backend.service.EmployeeService;
import com.bankapp.Backend.service.TransactionService;
import com.bankapp.Backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/employee")
@PreAuthorize("hasRole('EMPLOYEE')")

public class EmployeeController {

    private final UserService userService;
    private final EmployeeService employeeService;

    private final TransactionService transactionService;
    private final BankAccountService bankAccountService;
  


    public EmployeeController(UserService userService, EmployeeService employeeService, TransactionService transactionService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;

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

    @PostMapping("/change-limit")
    public ResponseEntity<ChangeDailyLimitResponse> changeDailyLimit(@RequestBody ChangeDailyLimitRequest request) {
        try {
            BankAccount bankAccount = bankAccountService.GetBankAccount(request.getIban())
                    .orElseThrow(() -> new RuntimeException("Bank account not found"));

            bankAccountService.changeDailyLimit(bankAccount, request.getDailyLimit());
            ChangeDailyLimitResponse response = new ChangeDailyLimitResponse(bankAccount, true, "Daily limit changes successfully");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ChangeDailyLimitResponse response = new ChangeDailyLimitResponse(null, false, e.getMessage() + "Failed to change daily limit");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/account/all")
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts() {
        List<BankAccount> accounts = bankAccountService.getAllBankAccounts();
        return ResponseEntity.ok(bankAccountService.accountsToResponses(accounts));
    }

<<<<<<< HEAD
    @GetMapping("/account/all")
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts() {
        List<BankAccount> accounts = bankAccountService.getAllBankAccounts();
        return ResponseEntity.ok(bankAccountService.accountsToResponses(accounts));
    }

=======
>>>>>>> 5d8c4f0 (accounts refactor)
    @PutMapping("/account/close/{iban}")
    public ResponseEntity<Void> updateAccountStatus(@PathVariable String iban) {
        bankAccountService.updateAccountStatus(iban, AccountStatus.BLOCKED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferFunds(@RequestBody TransactionRequest transactionRequest) {
        transactionService.transferFundsEmployee(transactionRequest);
        return ResponseEntity.ok().build();
    }
}
