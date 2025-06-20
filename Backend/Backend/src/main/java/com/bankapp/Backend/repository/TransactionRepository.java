package com.bankapp.Backend.repository;

import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount = :account AND t.date >= CURRENT_DATE")
    BigDecimal getTotalTransferredToday(@Param("account") BankAccount account);

    // Query to get all transactions (optionally with sorting)
    @Query("SELECT t FROM Transaction t ORDER BY t.date DESC")
    List<Transaction> findAllTransactions();

    // Query that fetches all transactions a customer was part of (based on fromAccount, toAccount and initiatingUser)
    @Query("""
    SELECT t FROM Transaction t
    LEFT JOIN t.fromAccount fa
    LEFT JOIN fa.user fromUser
    LEFT JOIN t.toAccount ta
    LEFT JOIN ta.user toUser
    WHERE 
        fromUser.id = :userId
        OR toUser.id = :userId
        OR t.initiatingUser.id = :userId
    ORDER BY t.date DESC
""")
    List<Transaction> findAllUserRelatedTransactions(@Param("userId") Long userId);





}
