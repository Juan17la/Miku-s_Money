package com.mikusmoney.mikusMoney.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Long mikuId;
    private String mikuFullName;
    private String transactionType;
}
