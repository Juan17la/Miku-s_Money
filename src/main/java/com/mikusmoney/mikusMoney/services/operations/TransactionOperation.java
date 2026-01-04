package com.mikusmoney.mikusMoney.services.operations;

public interface TransactionOperation<RQ, RS> {
    RS execute(RQ request, String idempotencyKey);
} 
