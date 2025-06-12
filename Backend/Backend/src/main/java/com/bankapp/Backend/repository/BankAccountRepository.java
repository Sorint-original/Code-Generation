package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.BankAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    boolean existsByIban(String iban);
    Optional<BankAccount> findByIban(String iban);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.dailyTransferLimit = :newLimit WHERE b.iban = :iban")
    void updateDailyLimitByIban(@Param("iban") String iban, @Param("newLimit") BigDecimal newLimit);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.absoluteTransferLimit = :newLimit WHERE b.iban = :iban")
    void updateAbsoluteLimitByIban(@Param("iban") String iban, @Param("newLimit") BigDecimal newLimit);

    Optional<BankAccount> findByUserId(Long userId);

    List<BankAccount> findAll();

    List<BankAccount> findAllByUser_Id(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.status = :status WHERE b.iban = :iban")
    void updateStatusByIban(@Param("iban") String iban, @Param("status") AccountStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.amount = :amount WHERE b.iban = :iban")
    void updateAmountByIban(@Param("iban") String iban, @Param("amount") BigDecimal amount);
}
