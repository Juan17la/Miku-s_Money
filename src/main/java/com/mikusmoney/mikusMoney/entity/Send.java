package com.mikusmoney.mikusMoney.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sends")
@DiscriminatorValue("SEND")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Send extends Transaction {

    @ManyToOne
    @JoinColumn(name = "sender_miku_id", nullable = false)
    private Miku sender;

    @ManyToOne
    @JoinColumn(name = "receiver_miku_id", nullable = false)
    private Miku receiver;
}
