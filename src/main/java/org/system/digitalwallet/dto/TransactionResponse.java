package org.system.digitalwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String type;          // e.g., "TOP_UP", "TRANSFER_IN", "TRANSFER_OUT"
    private String description;
    private LocalDateTime createdAt;

    // Wallet info
    private Long walletId;
    private String currency;
}
