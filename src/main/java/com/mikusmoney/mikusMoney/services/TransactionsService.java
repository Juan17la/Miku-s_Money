package com.mikusmoney.mikusMoney.services;

import java.math.BigDecimal;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendMoneyRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.TransactionHistoryResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawRequest;
import com.mikusmoney.mikusMoney.entity.Account;
import com.mikusmoney.mikusMoney.entity.Deposit;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.entity.Send;
import com.mikusmoney.mikusMoney.entity.Transaction;
import com.mikusmoney.mikusMoney.entity.Withdraw;
import com.mikusmoney.mikusMoney.mapper.DepositMapper;
import com.mikusmoney.mikusMoney.mapper.SendMapper;
import com.mikusmoney.mikusMoney.mapper.TransactionMapper;
import com.mikusmoney.mikusMoney.mapper.WithdrawMapper;
import com.mikusmoney.mikusMoney.repository.AccountRepository;
import com.mikusmoney.mikusMoney.repository.DepositRepository;
import com.mikusmoney.mikusMoney.repository.SendRepository;
import com.mikusmoney.mikusMoney.repository.TransactionRepository;
import com.mikusmoney.mikusMoney.repository.WithdrawRepository;
import com.mikusmoney.mikusMoney.validators.AuthValidator;
import com.mikusmoney.mikusMoney.validators.AuthValidator.AuthContext;
import com.mikusmoney.mikusMoney.validators.IdempotencyValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionsService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;

    private final WithdrawRepository withdrawRepository;
    private final WithdrawMapper withdrawMapper;

    private final SendMapper sendMapper;
    private final SendRepository sendRepository;
    
    // Validators
    private final IdempotencyValidator idempotencyValidator;
    private final AuthValidator authValidator;


    @Transactional
    public DepositResponse deposit(DepositRequest request, String idempotencyKey) {
        // Validate idempotency
        idempotencyValidator.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authValidator.validateAuth(request.getPinCode());

        // Validate amount
        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(MAX_AMOUNT) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount out of bounds");
        }

        // Execute business logic: update account balance
        context.account().deposit(amount);
        accountRepository.save(context.account());

        // Persist transaction
        Deposit deposit = depositMapper.toEntity(request);
        deposit.setMiku(context.miku());
        deposit.setIdempotencyKey(idempotencyKey);
        Deposit savedDeposit = depositRepository.save(deposit);

        return depositMapper.toResponse(savedDeposit);
    }

    @Transactional
    public WithdrawResponse withdraw(WithdrawRequest request, String idempotencyKey) {
        // Validate idempotency
        idempotencyValidator.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authValidator.validateAuth(request.getPinCode());
        
        // Execute business logic: update account balance
        BigDecimal amount = request.getAmount();
        context.account().withdraw(amount);
        accountRepository.save(context.account());

        // Persist transaction
        Withdraw withdraw = withdrawMapper.toEntity(request);
        withdraw.setMiku(context.miku());
        withdraw.setIdempotencyKey(idempotencyKey);
        Withdraw savedWithdraw = withdrawRepository.save(withdraw);

        return withdrawMapper.toResponse(savedWithdraw);
    }

    @Transactional
    public SendResponse send(SendMoneyRequest request, String idempotencyKey) {
        // Validate idempotency
        idempotencyValidator.validate(idempotencyKey);
        
        // Validate authentication and PIN
        AuthContext context = authValidator.validateAuth(request.getPinCode());

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

    @Transactional
    public Page<TransactionHistoryResponse> getTransactionHistory(int page) {
        // Get authenticated user
        Miku miku = authValidator.getAuthenticatedMiku();
        
        // Create pageable with size 10 and sort by createdAt descending
        Pageable pageable = PageRequest.of(page, 10, org.springframework.data.domain.Sort.by(
            org.springframework.data.domain.Sort.Direction.DESC, "createdAt"
        ));
        
        // Get paginated transactions from database
        Page<Transaction> transactions = transactionRepository.findAllByMikuId(miku.getId(), pageable);
        
        // Map Page<Transaction> to Page<TransactionHistoryResponse>
        return transactions.map(transaction -> {
            if (transaction instanceof Deposit) {
                return transactionMapper.depositToHistory((Deposit) transaction);
            } else if (transaction instanceof Withdraw) {
                return transactionMapper.withdrawToHistory((Withdraw) transaction);
            } else if (transaction instanceof Send) {
                return transactionMapper.sendToHistory((Send) transaction);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown transaction type: " + transaction.getClass().getName());
        });
    }

}