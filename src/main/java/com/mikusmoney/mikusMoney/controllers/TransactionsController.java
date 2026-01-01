package com.mikusmoney.mikusMoney.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mikusmoney.mikusMoney.dto.transactionsDTOs.TransactionHistoryResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.WithdrawResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.depositDTOs.DepositResponse;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendMoneyRequest;
import com.mikusmoney.mikusMoney.dto.transactionsDTOs.sendDTOs.SendResponse;
import com.mikusmoney.mikusMoney.services.TransactionsService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v0.1/transactions/")
@RequiredArgsConstructor
public class TransactionsController {
    
    private final TransactionsService transactionsService;

    @PostMapping("deposit")
    public ResponseEntity<DepositResponse> deposit(
        @RequestBody DepositRequest depositRequest,
        @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity.ok(
            transactionsService.deposit(depositRequest, idempotencyKey)
        );
    }

    @PostMapping("withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
        @RequestBody WithdrawRequest withdrawRequest,
        @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity.ok(
            transactionsService.withdraw(withdrawRequest, idempotencyKey)
        );
    }


    @PostMapping("send")
    public ResponseEntity<SendResponse> transfer(
        @RequestBody SendMoneyRequest request,
        @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity.ok(
            transactionsService.send(request, idempotencyKey)
        );
    }

    @GetMapping("history")
    public ResponseEntity<Page<TransactionHistoryResponse>> getTransactionHistory(
        @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(
            transactionsService.getTransactionHistory(page)
        );
    }

}
