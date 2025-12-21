package com.mikusmoney.mikusMoney.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendResponse {

    private Long id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Long senderMikuId;
    private String senderFullName;
    private Long receiverMikuId;
    private String receiverFullName;
    private String transactionType;
}
