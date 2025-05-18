package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);

    Optional<User> findUserByBsnNumber(String bsnNumber);

    List<User> findAllByRoleAndBankAccountsEmpty(Role role);

}
