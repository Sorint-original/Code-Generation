package com.bankapp.Backend.config;

import com.bankapp.Backend.model.Account;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.AccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;

    public DataSeeder(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountRepository = accountRepository;
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
            user1.setDateOfBirth(LocalDate.parse("1990-01-01"));
            user1.setBsnNumber("123456789");

            User user2 = new User();
            user2.setFirstName("Bob");
            user2.setLastName("Jones");
            user2.setUserName("BobJones");
            user2.setEmail("bob@example.com");
            user2.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user2.setRole(Role.EMPLOYEE);
            user2.setPhoneNumber("0623456789");
            user2.setDateOfBirth(LocalDate.parse("1985-05-15"));
            user2.setBsnNumber("987654321");

            userRepository.save(user1);
            userRepository.save(user2);

            BigDecimal bd1 =
                    new BigDecimal("124567890.0987654321");
            BigDecimal bd2 =
                    new BigDecimal("987654321.123456789");


            Account account = new Account(user1, "21345643211", 1000, "Checking", 1000, 10000, "active");
            Account account2 = new Account(user2, "2134564321", 1000, "Checking", 1000, 10000, "active");

            accountRepository.save(account);
            accountRepository.save(account2);


            System.out.println("Seeded test users!");
        }
    }
}


