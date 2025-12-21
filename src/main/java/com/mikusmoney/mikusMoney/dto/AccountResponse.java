package com.mikusmoney.mikusMoney.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long id;
    private BigDecimal totalMoney;
    private LocalDateTime createdAt;
    private Long mikuId;
    private String mikuFullName;
}
