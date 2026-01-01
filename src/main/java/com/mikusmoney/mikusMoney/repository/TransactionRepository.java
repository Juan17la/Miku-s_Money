package com.mikusmoney.mikusMoney.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mikusmoney.mikusMoney.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);
    
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN Deposit d ON t.id = d.id " +
           "LEFT JOIN Withdraw w ON t.id = w.id " +
           "LEFT JOIN Send s ON t.id = s.id " +
           "WHERE d.miku.id = :mikuId " +
           "OR w.miku.id = :mikuId " +
           "OR s.sender.id = :mikuId " +
           "OR s.receiver.id = :mikuId " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByMikuId(@Param("mikuId") Long mikuId, Pageable pageable);
}