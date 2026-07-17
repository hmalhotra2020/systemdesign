package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.TransactionRecord;
import com.example.demo.model.request.TransferRequest;
import com.example.demo.model.response.MoneyMovementResponse;
import com.example.demo.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionRecord> getTransaction(@PathVariable("transactionId") Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionRecord>> getCustomerTransactions(@PathVariable Long customerId,
                                                                           @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(transactionService.getTransactionsByCustomer(customerId, limit));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionRecord>> getAccountTransactions(@PathVariable Long accountId,
                                                                          @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId, limit));
    }

    @PostMapping("/transfer")
    public ResponseEntity<MoneyMovementResponse> transferMoney(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                               @RequestBody TransferRequest request) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            request.setIdempotencyKey(idempotencyKey);
        }
        return ResponseEntity.ok(transactionService.transferMoney(request));
    }
}
