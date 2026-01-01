package com.mikusmoney.mikusMoney.validators;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mikusmoney.mikusMoney.entity.Account;
import com.mikusmoney.mikusMoney.entity.Credential;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.CredentialRepository;

import lombok.RequiredArgsConstructor;

/**
 * Validator responsible for authentication-related operations.
 * Handles authentication verification, user retrieval, account fetching, and PIN validation.
 */
@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final AccountRepository accountRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves the authenticated Miku from the security context.
     * 
     * @return The authenticated Miku
     * @throws IllegalStateException if user is not authenticated
     */
    public Miku getAuthenticatedMiku() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        return (Miku) authentication.getPrincipal();
    }

    /**
     * Retrieves the account associated with the given Miku.
     * 
     * @param miku The Miku whose account to retrieve
     * @return The Account associated with the Miku
     * @throws IllegalArgumentException if account is not found
     */
    public Account getAccountByMiku(Miku miku) {
        return accountRepository.findByMikuId(miku.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Validates the PIN code for the given Miku.
     * 
     * @param miku The Miku whose PIN to validate
     * @param pinCode The PIN code to validate
     * @throws IllegalArgumentException if credentials are not found or PIN is invalid
     */
    public void validatePin(Miku miku, String pinCode) {
        Credential credential = credentialRepository.findByMikuId(miku.getId())
                .orElseThrow(() -> new IllegalArgumentException("Credentials not found"));

        if (!passwordEncoder.matches(pinCode, credential.getPinCode())) {
            throw new IllegalArgumentException("Invalid PIN code");
        }
    }

    /**
     * Complete authentication context including Miku, Account, and PIN validation.
     * 
     * @param pinCode The PIN code to validate (optional, can be null)
     * @return AuthContext with Miku and Account
     */
    public AuthContext validateAuth(String pinCode) {
        Miku miku = getAuthenticatedMiku();
        Account account = getAccountByMiku(miku);
        
        if (pinCode != null && !pinCode.isEmpty()) {
            validatePin(miku, pinCode);
        }
        
        return new AuthContext(miku, account);
    }

    /**
     * Record to hold the authentication context.
     */
    public record AuthContext(Miku miku, Account account) {}
}
