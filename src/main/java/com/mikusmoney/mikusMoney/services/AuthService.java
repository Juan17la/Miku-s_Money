package com.mikusmoney.mikusMoney.services;

import com.mikusmoney.mikusMoney.dto.AuthResponse;
import com.mikusmoney.mikusMoney.dto.MikuCreateRequest;
import com.mikusmoney.mikusMoney.dto.MikuResponse;
import com.mikusmoney.mikusMoney.entity.Account;
import com.mikusmoney.mikusMoney.entity.Credential;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.mapper.MikuMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.CredentialRepository;
import com.mikusmoney.mikusMoney.repository.MikuRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MikuMapper mikuMapper;
    private final MikuRepository mikuRepository;
    private final CredentialRepository credentialRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse signUp(MikuCreateRequest mikuRequest) {

        if (credentialRepository.existsByEmail(mikuRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (credentialRepository.existsByPhoneNumber(mikuRequest.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        
        if(!mikuRequest.getPassword().equals(mikuRequest.getPasswordConfirmation())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (!mikuRequest.getPinCode().equals(mikuRequest.getPinCodeConfirmation())) {
            throw new IllegalArgumentException("PIN codes do not match");
        }

        Miku miku = mikuMapper.toEntity(mikuRequest);
        Miku savedMiku = mikuRepository.save(miku);
        
        Credential credential = Credential.builder()
                .email(mikuRequest.getEmail())
                .phoneNumber(mikuRequest.getPhoneNumber())
                .password(passwordEncoder.encode(mikuRequest.getPassword()))
                .pinCode(passwordEncoder.encode(mikuRequest.getPinCode()))
                .miku(savedMiku)
                .build();
        
        Credential savedCredential = credentialRepository.save(credential);
        
        Account account = Account.builder()
                .totalMoney(BigDecimal.ZERO)
                .miku(savedMiku)
                .build();
        
        accountRepository.save(account);
        
        savedMiku.setCredential(savedCredential);
        savedMiku.setAccount(account);
        
        
        return AuthResponse.builder()
                .token(jwtService.getToken(savedMiku))
                .build();
    }

    @Transactional
    public AuthResponse login(String email, String pinCode){
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        if (!passwordEncoder.matches(pinCode, credential.getPinCode())) {
            throw new IllegalArgumentException("Invalid password");
        }
        
        Miku miku = credential.getMiku();
        
        return AuthResponse.builder()
                .token(jwtService.getToken(miku))
                .build();
    }

    public MikuResponse getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new IllegalStateException("User is not authenticated");
        }

        Miku miku = (Miku) authentication.getPrincipal();
        return mikuMapper.toResponse(miku);
    }

}

