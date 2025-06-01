package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.BankAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query("SELECT b FROM BankAccount b")
    List<BankAccount> findAllBankAccounts();

    @Query("SELECT b FROM BankAccount b WHERE b.user.id = :userId")
    List<BankAccount> findBankAccountsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.status = :status WHERE b.iban = :iban")
    void updateStatusByIban(@Param("iban") String iban, @Param("status") AccountStatus status);
}
