package com.mikusmoney.mikusMoney.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "withdrawals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Withdraw extends Transaction {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "miku_id", nullable = false)
    private Miku miku;

    @PrePersist
    protected void onCreate() {
        setTransactionType("WITHDRAW");
        validateAmount();
    }

    private void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }
    }

    public boolean isLargeWithdrawal() {
        return amount.compareTo(new BigDecimal("10000")) >= 0;
    }
}
