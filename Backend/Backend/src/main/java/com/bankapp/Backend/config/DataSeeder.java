package com.bankapp.Backend.config;

import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
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

    public DataSeeder(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.bankAccountRepository = bankAccountRepository;
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
            user1.setStatus(CustomerStatus.Pending);

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

            User user3 = new User();
            user3.setFirstName("Jane");
            user3.setLastName("Smith");
            user3.setUserName("JaneSmith");
            user3.setEmail("jane@example.com");
            user3.setPassword(bCryptPasswordEncoder.encode("secureaccess"));
            user3.setRole(Role.CUSTOMER);
            user3.setPhoneNumber("062344356789");
            user3.setDateOfBirth(LocalDate.parse("1985-05-15"));
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
            user4.setDateOfBirth(LocalDate.parse("1985-05-15"));
            user4.setBsnNumber("987654389080921");
            user4.setStatus(CustomerStatus.Pending);



            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            userRepository.save(user4);

            BigDecimal bd1 =
                    new BigDecimal("124567890.0987654321");
            BigDecimal bd2 =
                    new BigDecimal("987654321.123456789");


            BankAccount bankAccount1 = new BankAccount(user1, new BigDecimal("10000.00"), AccountType.CHECKING, "21345643211", new BigDecimal("0.01"), new BigDecimal("1000.00"), AccountStatus.APPROVED);
            BankAccount bankAccount2 = new BankAccount(user2, new BigDecimal("10000.00"), AccountType.CHECKING, "2134564321", new BigDecimal("0.01"), new BigDecimal("1000.00"), AccountStatus.APPROVED);

            bankAccountRepository.save(bankAccount1);
            bankAccountRepository.save(bankAccount2);

            System.out.println("Seeded test users!");
        }
    }
}


