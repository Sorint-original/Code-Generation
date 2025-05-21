package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.BankAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    boolean existsByIban(String iban);
    Optional<BankAccount> findByIban(String iban);

    @Modifying
    @Transactional
    @Query("UPDATE BankAccount b SET b.dailyTransferLimit = :newLimit WHERE b.iban = :iban")
    void updateDailyLimitByIban(@Param("iban") String iban, @Param("newLimit") BigDecimal newLimit);
}
