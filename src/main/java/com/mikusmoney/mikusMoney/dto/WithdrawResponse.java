package com.mikusmoney.mikusMoney.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawResponse {

    private Long id;
    private BigDecimal amount;
    private Long mikuId;
}
