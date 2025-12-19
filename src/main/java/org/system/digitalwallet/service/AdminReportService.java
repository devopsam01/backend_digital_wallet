package org.system.digitalwallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.system.digitalwallet.dto.AdminDashboardDTO;
import org.system.digitalwallet.repository.AuditLogRepository;
import org.system.digitalwallet.repository.TransactionRepository;
import org.system.digitalwallet.repository.WalletRepository;
import org.system.digitalwallet.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardDTO getDashboardMetrics() {

        long totalUsers = userRepository.count();
        long totalWallets = walletRepository.count();
        long totalTransactions = transactionRepository.countBy();

        Double totalVolume = transactionRepository.getTotalTransactionVolume();
        if (totalVolume == null) {
            totalVolume = 0.0;
        }

        long recentAudits =
                auditLogRepository.countRecentAudits(LocalDateTime.now().minusDays(7));

        return new AdminDashboardDTO(
                totalUsers,
                totalWallets,
                totalTransactions,
                totalVolume,
                recentAudits
        );
    }
}
