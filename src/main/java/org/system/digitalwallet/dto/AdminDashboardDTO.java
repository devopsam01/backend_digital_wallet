package org.system.digitalwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {

    private long totalUsers;
    private long totalWallets;
    private long totalTransactions;
    private double totalTransactionVolume;
    private long recentAuditLogs;
}
