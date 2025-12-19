package org.system.digitalwallet.repository;

import org.system.digitalwallet.model.Transaction;
import org.system.digitalwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    // 🔢 Total number of transactions
    long countBy();

    // 💰 Total transaction volume
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
    Double getTotalTransactionVolume();
}
