package com.mikusmoney.mikusMoney.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mikusmoney.mikusMoney.entity.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    
    Page<Deposit> findByMikuIdOrderByCreatedAtDesc(Long mikuId, Pageable pageable);
}
