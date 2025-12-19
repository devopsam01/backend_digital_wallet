package org.system.digitalwallet.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.digitalwallet.dto.TransactionResponse;
import org.system.digitalwallet.exception.*;
import org.system.digitalwallet.model.*;
import org.system.digitalwallet.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    @Transactional
    public Wallet createWallet(User user, String currency, HttpServletRequest request) {
        Wallet wallet = Wallet.builder()
                .owner(user)
                .currency(currency)
                .balance(0.0)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);

        // Audit log
        auditService.log(
                user,
                "CREATE_WALLET",
                "WALLET",
                savedWallet.getId(),
                "SUCCESS",
                "Wallet created with currency " + currency,
                request
        );

        return savedWallet;
    }

    public List<Wallet> getUserWallets(User user, HttpServletRequest request) {
        List<Wallet> wallets = walletRepository.findByOwner(user);

        auditService.log(
                user,
                "VIEW_WALLETS",
                "WALLET",
                null,
                "SUCCESS",
                "Fetched " + wallets.size() + " wallets",
                request
        );

        return wallets;
    }

    public Wallet getWallet(User user, Long walletId, HttpServletRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (!wallet.getOwner().getId().equals(user.getId())) {
            auditService.log(
                    user,
                    "VIEW_WALLET",
                    "WALLET",
                    walletId,
                    "FAILED",
                    "Unauthorized wallet access attempt",
                    request
            );
            throw new UnauthorizedException("Not your wallet");
        }

        auditService.log(
                user,
                "VIEW_WALLET",
                "WALLET",
                walletId,
                "SUCCESS",
                "Wallet retrieved successfully",
                request
        );

        return wallet;
    }

    @Transactional
    public Wallet topUpWallet(User user, Long walletId, Double amount, HttpServletRequest request) {
        if (amount <= 0) throw new InvalidTransactionException("Amount must be positive");

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (!wallet.getOwner().getId().equals(user.getId())) {
            auditService.log(
                    user,
                    "TOP_UP",
                    "WALLET",
                    walletId,
                    "FAILED",
                    "Unauthorized top-up attempt",
                    request
            );
            throw new UnauthorizedException("Not your wallet");
        }

        wallet.setBalance(wallet.getBalance() + amount);
        Wallet updatedWallet = walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .wallet(updatedWallet)
                .amount(amount)
                .type(TransactionType.TOP_UP)
                .description("Wallet top-up")
                .createdAt(LocalDateTime.now())
                .build());

        auditService.log(
                user,
                "TOP_UP",
                "WALLET",
                walletId,
                "SUCCESS",
                "Wallet topped up with amount " + amount,
                request
        );

        return updatedWallet;
    }

    @Transactional
    public Wallet transfer(User user, Long fromWalletId, Long toWalletId, Double amount, HttpServletRequest request) {
        if (amount <= 0) throw new InvalidTransactionException("Amount must be positive");
        if (fromWalletId.equals(toWalletId)) throw new InvalidTransactionException("Cannot transfer to same wallet");

        Wallet fromWallet = walletRepository.findByIdForUpdate(fromWalletId)
                .orElseThrow(() -> new ResourceNotFoundException("Source wallet not found"));

        Wallet toWallet = walletRepository.findByIdForUpdate(toWalletId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination wallet not found"));

        if (!fromWallet.getOwner().getId().equals(user.getId())) {
            auditService.log(
                    user,
                    "TRANSFER",
                    "WALLET",
                    fromWalletId,
                    "FAILED",
                    "Unauthorized transfer attempt",
                    request
            );
            throw new UnauthorizedException("Not your wallet");
        }

        if (fromWallet.getBalance() < amount) {
            auditService.log(
                    user,
                    "TRANSFER",
                    "WALLET",
                    fromWalletId,
                    "FAILED",
                    "Insufficient balance",
                    request
            );
            throw new InsufficientBalanceException("Insufficient funds");
        }

        fromWallet.setBalance(fromWallet.getBalance() - amount);
        toWallet.setBalance(toWallet.getBalance() + amount);

        walletRepository.save(toWallet);
        Wallet updatedFromWallet = walletRepository.save(fromWallet);

        transactionRepository.save(Transaction.builder()
                .wallet(updatedFromWallet)
                .amount(amount)
                .type(TransactionType.TRANSFER_OUT)
                .description("Transfer to wallet " + toWalletId)
                .createdAt(LocalDateTime.now())
                .build());

        transactionRepository.save(Transaction.builder()
                .wallet(toWallet)
                .amount(amount)
                .type(TransactionType.TRANSFER_IN)
                .description("Transfer from wallet " + fromWalletId)
                .createdAt(LocalDateTime.now())
                .build());

        auditService.log(
                user,
                "TRANSFER",
                "WALLET",
                fromWalletId,
                "SUCCESS",
                "Transferred " + amount + " to wallet " + toWalletId,
                request
        );

        return updatedFromWallet;
    }

    public List<Transaction> getTransactions(User user, Long walletId, HttpServletRequest request) {
        Wallet wallet = getWallet(user, walletId, request);
        List<Transaction> transactions = transactionRepository.findByWalletOrderByCreatedAtDesc(wallet);

        auditService.log(
                user,
                "VIEW_TRANSACTIONS",
                "TRANSACTION",
                walletId,
                "SUCCESS",
                "Fetched " + transactions.size() + " transactions",
                request
        );

        return transactions;
    }

    public List<TransactionResponse> getAllTransactions(User user, HttpServletRequest request) {
        List<Wallet> wallets = walletRepository.findByOwner(user);

        List<TransactionResponse> responses = wallets.stream()
                .flatMap(wallet ->
                        transactionRepository.findByWalletOrderByCreatedAtDesc(wallet).stream()
                                .map(t -> new TransactionResponse(
                                        t.getId(),
                                        t.getAmount(),
                                        t.getType().name(),
                                        t.getDescription(),
                                        t.getCreatedAt(),
                                        wallet.getId(),
                                        wallet.getCurrency()
                                ))
                )
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        auditService.log(
                user,
                "VIEW_ALL_TRANSACTIONS",
                "TRANSACTION",
                null,
                "SUCCESS",
                "Fetched all transactions count=" + responses.size(),
                request
        );

        return responses;
    }
}
