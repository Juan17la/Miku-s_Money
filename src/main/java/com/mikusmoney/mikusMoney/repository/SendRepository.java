package com.mikusmoney.mikusMoney.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mikusmoney.mikusMoney.entity.Send;

@Repository
public interface SendRepository extends JpaRepository<Send, Long> {
    
    Page<Send> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);
}
