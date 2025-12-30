package com.mikusmoney.mikusMoney.services;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.DepositRequest;
import com.mikusmoney.mikusMoney.dto.DepositResponse;
import com.mikusmoney.mikusMoney.entity.Account;
import com.mikusmoney.mikusMoney.entity.Credential;
import com.mikusmoney.mikusMoney.entity.Deposit;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.mapper.DepositMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.CredentialRepository;
import com.mikusmoney.mikusMoney.repository.DepositRepository;
import com.mikusmoney.mikusMoney.repository.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionsService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final CredentialRepository credentialRepository;

    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;


    @Transactional
    public DepositResponse deposit(DepositRequest request, String idempotencyKey) {

        // IDEMPOTENCY CHECK
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            throw new IllegalArgumentException("Duplicate transaction");
        }

        // GETTING AUTH USER DATA
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if(authentication == null || !authentication.isAuthenticated()){
            throw new IllegalStateException("User is not authenticated");
        }

        Miku miku = (Miku) authentication.getPrincipal();
        
        // VALIDATING VERSION AND LOCKING, FETCHING AUTH USER ACCOUNT
        Account account = accountRepository.findByMikuId(miku.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        
        
        // VALIDATE PIN CODE
        Credential credential = credentialRepository.findByMikuId(miku.getId())
                .orElseThrow(() -> new IllegalArgumentException("Credentials not found"));
        
        if (!credential.getPinCode().equals(request.getPinCode())) {
            throw new IllegalArgumentException("Invalid PIN code");
        }

        // VALIDATE AMOUNT
        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0 ||
            amount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Amount out of bounds");
        }

        // UPDATE ACCOUNT BALANCE / TOTALMONEY
        account.setTotalMoney(amount);
        accountRepository.save(account);

        // SAVE NEW DEPOSIT TRANSACTION
        Deposit deposit = depositMapper.toEntity(request);
        deposit.setMiku(miku);
        deposit.setIdempotencyKey(idempotencyKey);

        Deposit savedDeposit = depositRepository.save(deposit);

        return depositMapper.toResponse(savedDeposit);
    }

    

}
