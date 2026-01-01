package com.mikusmoney.mikusMoney.validators;

import org.springframework.stereotype.Component;

import com.mikusmoney.mikusMoney.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

/**
 * Validator responsible for checking idempotency keys to prevent duplicate transactions.
 * Ensures that a transaction with the same idempotency key has not been processed before.
 */
@Component
@RequiredArgsConstructor
public class IdempotencyValidator {

    private final TransactionRepository transactionRepository;

    /**
     * Validates that the given idempotency key has not been used before.
     * 
     * @param idempotencyKey The unique key to validate
     * @throws IllegalArgumentException if the idempotency key has already been used
     */
    public void validate(String idempotencyKey) {
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            throw new IllegalArgumentException("Duplicate transaction");
        }
    }
}
