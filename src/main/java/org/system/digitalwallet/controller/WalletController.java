package org.system.digitalwallet.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.system.digitalwallet.dto.*;
import org.system.digitalwallet.model.User;
import org.system.digitalwallet.model.Wallet;
import org.system.digitalwallet.model.Transaction;
import org.system.digitalwallet.service.WalletService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponse> createWallet(
            @AuthenticationPrincipal User user,
            @RequestBody WalletCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        Wallet wallet = walletService.createWallet(user, request.getCurrency(), httpRequest);
        WalletResponse response = new WalletResponse(wallet.getId(), wallet.getCurrency(), wallet.getBalance());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<WalletResponse>> getWallets(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        List<Wallet> wallets = walletService.getUserWallets(user, httpRequest);
        List<WalletResponse> response = wallets.stream()
                .map(w -> new WalletResponse(w.getId(), w.getCurrency(), w.getBalance()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(
            @AuthenticationPrincipal User user,
            @PathVariable Long walletId,
            HttpServletRequest httpRequest
    ) {
        Wallet wallet = walletService.getWallet(user, walletId, httpRequest);
        WalletResponse response = new WalletResponse(wallet.getId(), wallet.getCurrency(), wallet.getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/topup")
    public ResponseEntity<WalletResponse> topUpWallet(
            @AuthenticationPrincipal User user,
            @RequestBody WalletTopUpRequest request,
            HttpServletRequest httpRequest
    ) {
        Wallet wallet = walletService.topUpWallet(user, request.getWalletId(), request.getAmount(), httpRequest);
        WalletResponse response = new WalletResponse(wallet.getId(), wallet.getCurrency(), wallet.getBalance());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<WalletResponse> transfer(
            @AuthenticationPrincipal User user,
            @RequestBody WalletTransferRequest request,
            HttpServletRequest httpRequest
    ) {
        Wallet wallet = walletService.transfer(
                user,
                request.getFromWalletId(),
                request.getToWalletId(),
                request.getAmount(),
                httpRequest
        );
        WalletResponse response = new WalletResponse(wallet.getId(), wallet.getCurrency(), wallet.getBalance());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getWalletTransactions(
            @AuthenticationPrincipal User user,
            @PathVariable Long walletId,
            HttpServletRequest httpRequest
    ) {
        List<Transaction> transactions = walletService.getTransactions(user, walletId, httpRequest);
        List<TransactionResponse> response = transactions.stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getAmount(),
                        t.getType().name(),
                        t.getDescription(),
                        t.getCreatedAt(),
                        t.getWallet().getId(),
                        t.getWallet().getCurrency()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions/all")
    public ResponseEntity<List<TransactionResponse>> getAllUserTransactions(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        List<TransactionResponse> response = walletService.getAllTransactions(user, httpRequest);
        return ResponseEntity.ok(response);
    }
}
