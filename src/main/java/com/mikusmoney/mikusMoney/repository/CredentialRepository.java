package com.mikusmoney.mikusMoney.repository;

import com.mikusmoney.mikusMoney.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    
    Optional<Credential> findByEmail(String email);
    
    Optional<Credential> findByMikuId(Long mikuId);

    Optional<Credential> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
}
