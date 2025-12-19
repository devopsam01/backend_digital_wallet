package org.system.digitalwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // <-- this adds the constructor WalletResponse(Long id, String currency, Double balance)
public class WalletResponse {
    private Long id;
    private String currency;
    private Double balance;
}
