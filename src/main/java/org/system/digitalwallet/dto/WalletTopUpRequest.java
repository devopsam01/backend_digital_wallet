package org.system.digitalwallet.dto;

import lombok.Data;

@Data
public class WalletTopUpRequest {
    private Long walletId;
    private Double amount;
}
