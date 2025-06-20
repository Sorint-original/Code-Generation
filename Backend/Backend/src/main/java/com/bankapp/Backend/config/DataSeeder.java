package com.bankapp.Backend.config;

import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.TransactionRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public DataSeeder(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }



    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setFirstName("Alice");
            user1.setLastName("Smith");
            user1.setUserName("AliceSmith");
            user1.setEmail("alice@example.com");
            user1.setPassword(bCryptPasswordEncoder.encode("password123"));
            user1.setRole(Role.CUSTOMER);
            user1.setPhoneNumber("0612345678");
            user1.setBsnNumber("123456789");
            user1.setStatus(CustomerStatus.Approved);

            User user2 = new User();
            user2.setFirstName("Bob");
            user2.setLastName("Jones");
            user2.setUserName("BobJones");
            user2.setEmail("bob@example.com");
            user2.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user2.setRole(Role.EMPLOYEE);
            user2.setPhoneNumber("0623456789");
            user2.setBsnNumber("987654321");

            User user3 = new User();
            user3.setFirstName("Jane");
            user3.setLastName("Smith");
            user3.setUserName("JaneSmith");
            user3.setEmail("jane@example.com");
            user3.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user3.setRole(Role.CUSTOMER);
            user3.setPhoneNumber("062344356789");
            user3.setBsnNumber("987689080954321");
            user3.setStatus(CustomerStatus.Pending);

            User user4 = new User();
            user4.setFirstName("zozo");
            user4.setLastName("Smith");
            user4.setUserName("zozoSmith");
            user4.setEmail("zozo@example.com");
            user4.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user4.setRole(Role.CUSTOMER);
            user4.setPhoneNumber("06234589078096789");
            user4.setBsnNumber("987654389080921");
            user4.setStatus(CustomerStatus.Pending);

            User user5 = new User();
            user5.setFirstName("user5");
            user5.setLastName("test");
            user5.setUserName("user5");
            user5.setEmail("user5@example.com");
            user5.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user5.setRole(Role.CUSTOMER);
            user5.setPhoneNumber("062345893443078096789");
            user5.setBsnNumber("9876543894343080921");
            user5.setStatus(CustomerStatus.Pending);

            User user6 = new User();
            user6.setFirstName("user6");
            user6.setLastName("test");
            user6.setUserName("user6");
            user6.setEmail("user6@example.com");
            user6.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user6.setRole(Role.CUSTOMER);
            user6.setPhoneNumber("0893443078096789");
            user6.setBsnNumber("9876543080921");
            user6.setStatus(CustomerStatus.Pending);



            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);
            userRepository.save(user5);
            userRepository.save(user6);

            BigDecimal bd1 =
                    new BigDecimal("124567890.0987654321");
            BigDecimal bd2 =
                    new BigDecimal("987654321.123456789");


            BankAccount bankAccount1 = new BankAccount(user1, new BigDecimal("10000.00"), AccountType.CHECKING, "21345643211", new BigDecimal("1.00"), new BigDecimal("1000.00"), AccountStatus.APPROVED);
            BankAccount bankAccount2 = new BankAccount(user2, new BigDecimal("10000.00"), AccountType.CHECKING, "2134564321", new BigDecimal("1.00"), new BigDecimal("1000.00"), AccountStatus.APPROVED);

            bankAccountRepository.save(bankAccount1);
            bankAccountRepository.save(bankAccount2);

            // Create test transactions
            Transaction t1 = new Transaction(bankAccount1, bankAccount2, new BigDecimal("500.00"), bankAccount1.getUser());
            t1.setDate(LocalDateTime.of(2024, 12, 13, 21, 45));

            Transaction t2 = new Transaction(bankAccount2, bankAccount1, new BigDecimal("250.00"), bankAccount2.getUser());
            t2.setDate(LocalDateTime.of(2025, 6, 2, 20, 45));

            Transaction t3 = new Transaction(bankAccount1, bankAccount2, new BigDecimal("100.00"), bankAccount1.getUser());
            t3.setDate(LocalDateTime.of(2023, 1, 1, 12, 0));

            Transaction t4 = new Transaction(bankAccount2, bankAccount1, new BigDecimal("750.00"), bankAccount2.getUser());
            t4.setDate(LocalDateTime.of(2025, 5, 4, 16, 45));

            Transaction t5 = new Transaction(bankAccount1, bankAccount2, new BigDecimal("300.00"), bankAccount1.getUser());
            t5.setDate(LocalDateTime.of(2025, 5, 23, 9, 33));

            Transaction t6 = new Transaction(bankAccount1, null, new BigDecimal("300.00"), bankAccount1.getUser());
            t6.setDate(LocalDateTime.of(2025, 5, 23, 9, 33));

            transactionRepository.save(t1);
            transactionRepository.save(t2);
            transactionRepository.save(t3);
            transactionRepository.save(t4);
            transactionRepository.save(t5);
            transactionRepository.save(t6);

            System.out.println("Seeded test users!");
        }
    }
}


