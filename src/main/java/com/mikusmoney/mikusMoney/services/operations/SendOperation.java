package com.mikusmoney.mikusMoney.services.operations;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendMoneyRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendResponse;
import com.mikusmoney.mikusMoney.entity.Account;
import com.mikusmoney.mikusMoney.entity.Send;
import com.mikusmoney.mikusMoney.mapper.SendMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.SendRepository;
import com.mikusmoney.mikusMoney.services.AuthContextService;
import com.mikusmoney.mikusMoney.services.AuthContextService.AuthContext;
import com.mikusmoney.mikusMoney.services.IdempotencyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SendOperation implements TransactionOperation<SendMoneyRequest, SendResponse> {
    
    private final IdempotencyService idempotencyService;
    private final AuthContextService authContextService;
    private final AccountRepository accountRepository;
    private final SendMapper sendMapper;
    private final SendRepository sendRepository;

    @Override
    public SendResponse execute(SendMoneyRequest request, String idempotencyKey) {
         // Validate idempotency
        idempotencyService.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authContextService.validateAuth(request.getPinCode());

        // Get and validate receiver account
        Account receiverAccount = accountRepository.findByMiku_PublicCode(request.getReceiverPublicCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver account not found"));
        
        // Execute business logic: transfer money
        BigDecimal amount = request.getAmount();
        context.account().transfer(receiverAccount, amount);
        accountRepository.save(context.account());
        accountRepository.save(receiverAccount);

        // Persist transaction
        Send send = sendMapper.toEntity(request);
        send.setSender(context.miku());
        send.setReceiver(receiverAccount.getMiku());
        send.setIdempotencyKey(idempotencyKey);
        Send savedSend = sendRepository.save(send);

        return sendMapper.toResponse(savedSend);
    }
    
}
