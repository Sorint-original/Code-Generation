package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.ChangeDailyLimitRequest;
import com.bankapp.Backend.DTO.ChangeDailyLimitResponse;
import com.bankapp.Backend.exception.ResourceNotFoundException;
import com.bankapp.Backend.exception.UserNotFoundException;
import com.bankapp.Backend.model.BankAccount;
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
    private final BankAccountService bankAccountService;

    public EmployeeController(UserService userService,
                              EmployeeService employeeService,
                              TransactionService transactionService,
                              BankAccountService bankAccountService) {
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
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/customers/{id}/approve")
    public ResponseEntity<String> approveCustomer(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }

        employeeService.approveCustomer(user);
        return ResponseEntity.ok("Customer approved and accounts created.");
    }

    @PostMapping("/customers/{id}/decline")
    public ResponseEntity<String> declineCustomerStatus(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }

        employeeService.updateUserStatus(user, CustomerStatus.Denied);
        return ResponseEntity.ok("User status updated to " + CustomerStatus.Denied);
    }

    @PostMapping("/change-limit")
    public ResponseEntity<ChangeDailyLimitResponse> changeDailyLimit(@RequestBody ChangeDailyLimitRequest request) {
        BankAccount account = bankAccountService
                .GetBankAccount(request.getIban());

        bankAccountService.changeDailyLimit(account, request.getDailyLimit());

        return ResponseEntity.ok(new ChangeDailyLimitResponse(account, true, "Daily limit changed successfully."));
    }
}
