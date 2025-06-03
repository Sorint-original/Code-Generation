package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);

    Optional<User> findUserByBsnNumber(String bsnNumber);

    Optional<User> findUserByFirstNameAndLastName(String firstName , String lastName );

    List<User> findAllByRoleAndBankAccountsEmpty(Role role);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    void updateUserStatusById(@Param("id") Long id, @Param("status") CustomerStatus status);

    List<User> findAllByRoleAndStatus(Role role, CustomerStatus status);



}
