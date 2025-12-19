package org.system.digitalwallet.dto;

import lombok.Data;

@Data
public class WalletTransferRequest {
    private Long fromWalletId;
    private Long toWalletId;
    private Double amount;
}
