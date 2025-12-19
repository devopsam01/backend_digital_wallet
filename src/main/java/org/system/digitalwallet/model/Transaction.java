package org.system.digitalwallet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // TOP_UP, TRANSFER_IN, TRANSFER_OUT

    private String description;

    private LocalDateTime createdAt;

}
