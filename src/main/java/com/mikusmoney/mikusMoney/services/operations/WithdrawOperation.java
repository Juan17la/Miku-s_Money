package com.mikusmoney.mikusMoney.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawResponse;
import com.mikusmoney.mikusMoney.entity.Withdraw;
import com.mikusmoney.mikusMoney.mapper.WithdrawMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.WithdrawRepository;
import com.mikusmoney.mikusMoney.services.AuthContextService;
import com.mikusmoney.mikusMoney.services.AuthContextService.AuthContext;
import com.mikusmoney.mikusMoney.services.IdempotencyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WithdrawOperation implements TransactionOperation<WithdrawRequest, WithdrawResponse> {

    private final IdempotencyService idempotencyService;
    private final AuthContextService authContextService;
    private final AccountRepository accountRepository;
    private final WithdrawRepository withdrawRepository;
    private final WithdrawMapper withdrawMapper;

    @Override
    public WithdrawResponse execute(WithdrawRequest request, String idempotencyKey) {
        // Validate idempotency
        idempotencyService.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authContextService.validateAuth(request.getPinCode());
        
        BigDecimal amount = request.getAmount();
        
        // Execute business logic: update account balance
        context.account().withdraw(amount);
        accountRepository.save(context.account());

        // Persist transaction
        Withdraw withdraw = withdrawMapper.toEntity(request);
        withdraw.setMiku(context.miku());
        withdraw.setIdempotencyKey(idempotencyKey);
        Withdraw savedWithdraw = withdrawRepository.save(withdraw);

        return withdrawMapper.toResponse(savedWithdraw);
    }
    
}
