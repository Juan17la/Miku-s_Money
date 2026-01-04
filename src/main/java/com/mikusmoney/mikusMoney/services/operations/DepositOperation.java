package com.mikusmoney.mikusMoney.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositResponse;
import com.mikusmoney.mikusMoney.entity.Deposit;
import com.mikusmoney.mikusMoney.mapper.DepositMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.DepositRepository;
import com.mikusmoney.mikusMoney.services.AuthContextService;
import com.mikusmoney.mikusMoney.services.AuthContextService.AuthContext;
import com.mikusmoney.mikusMoney.services.IdempotencyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepositOperation implements TransactionOperation<DepositRequest, DepositResponse> {

    private final IdempotencyService idempotencyService;
    private final AuthContextService authContextService;
    private final AccountRepository accountRepository;
    private final DepositMapper depositMapper;
    private final DepositRepository depositRepository;

    @Override
    public DepositResponse execute(DepositRequest request, String idempotencyKey) {
        // Validate idempotency
        idempotencyService.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authContextService.validateAuth(request.getPinCode());

        BigDecimal amount = request.getAmount();

        // Execute business logic: update account balance and validates amount
        context.account().deposit(amount);
        accountRepository.save(context.account());

        // Persist transaction
        Deposit deposit = depositMapper.toEntity(request);
        deposit.setMiku(context.miku());
        deposit.setIdempotencyKey(idempotencyKey);
        Deposit savedDeposit = depositRepository.save(deposit);
        
        return depositMapper.toResponse(savedDeposit);
    }
    
}
