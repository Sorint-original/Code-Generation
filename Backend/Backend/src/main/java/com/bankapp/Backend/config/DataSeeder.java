package com.bankapp.Backend.config;

import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DataSeeder(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

            System.out.println("Seeded test users!");
        }
    }
}


