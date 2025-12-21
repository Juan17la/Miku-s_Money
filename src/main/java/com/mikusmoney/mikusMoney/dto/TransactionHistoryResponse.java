package com.mikusmoney.mikusMoney.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {

    private Long id;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    
    // For deposits and withdrawals
    private Long mikuId;
    private String mikuFullName;
    
    // For send transactions
    private Long senderMikuId;
    private String senderFullName;
    private Long receiverMikuId;
    private String receiverFullName;
}
