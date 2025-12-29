package com.mikusmoney.mikusMoney.mapper;

import com.mikusmoney.mikusMoney.dto.TransactionHistoryResponse;
import com.mikusmoney.mikusMoney.entity.Deposit;
import com.mikusmoney.mikusMoney.entity.Send;
import com.mikusmoney.mikusMoney.entity.Transaction;
import com.mikusmoney.mikusMoney.entity.Withdraw;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    
    // Convert Deposit to TransactionHistoryResponse
    @Mapping(target = "transactionType", constant = "DEPOSIT")
    @Mapping(target = "mikuId", source = "miku.id")
    @Mapping(target = "mikuFullName", source = "miku.fullName")
    @Mapping(target = "senderMikuId", ignore = true)
    @Mapping(target = "senderFullName", ignore = true)
    @Mapping(target = "receiverMikuId", ignore = true)
    @Mapping(target = "receiverFullName", ignore = true)
    TransactionHistoryResponse depositToHistory(Deposit deposit);
    
    // Convert Withdraw to TransactionHistoryResponse
    @Mapping(target = "transactionType", constant = "WITHDRAW")
    @Mapping(target = "mikuId", source = "miku.id")
    @Mapping(target = "mikuFullName", source = "miku.fullName")
    @Mapping(target = "senderMikuId", ignore = true)
    @Mapping(target = "senderFullName", ignore = true)
    @Mapping(target = "receiverMikuId", ignore = true)
    @Mapping(target = "receiverFullName", ignore = true)
    TransactionHistoryResponse withdrawToHistory(Withdraw withdraw);
    
    // Convert Send to TransactionHistoryResponse
    @Mapping(target = "transactionType", constant = "SEND")
    @Mapping(target = "mikuId", ignore = true)
    @Mapping(target = "mikuFullName", ignore = true)
    @Mapping(target = "senderMikuId", source = "sender.id")
    @Mapping(target = "senderFullName", source = "sender.fullName")
    @Mapping(target = "receiverMikuId", source = "receiver.id")
    @Mapping(target = "receiverFullName", source = "receiver.fullName")
    TransactionHistoryResponse sendToHistory(Send send);
}
