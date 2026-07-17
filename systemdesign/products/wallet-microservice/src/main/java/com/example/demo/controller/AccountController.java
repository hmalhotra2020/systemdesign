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

import com.example.demo.model.Account;
import com.example.demo.model.request.AccountMovementRequest;
import com.example.demo.model.response.MoneyMovementResponse;
import com.example.demo.service.AccountService;
import com.example.demo.model.request.AccountCreateRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountCreateRequest request) {
        Account created = accountService.createAccount(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<java.util.Map<String, Object>> getBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.balance(accountId));
    }

    @GetMapping("/{accountId}/ledger-check")
    public ResponseEntity<java.util.Map<String, Object>> ledgerCheck(@PathVariable Long accountId,
                                                                      @RequestParam(required = false) String from,
                                                                      @RequestParam(required = false) String to) {
        OffsetDateTime f = from != null ? OffsetDateTime.parse(from) : OffsetDateTime.now().minusDays(1);
        OffsetDateTime t = to != null ? OffsetDateTime.parse(to) : OffsetDateTime.now();
        return ResponseEntity.ok(accountService.ledgerCheck(accountId, f, t));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        return accountService.getAccountById(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getCustomerAccounts(@PathVariable Long customerId,
                                                             @RequestParam(required = false) String type) {
        if (type != null) {
            return accountService.getAccountByCustomerAndType(customerId, type)
                    .map(account -> ResponseEntity.ok(List.of(account)))
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(accountService.getAccountsByCustomer(customerId));
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<MoneyMovementResponse> creditAccount(@PathVariable Long accountId,
                                                               @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                               @RequestBody AccountMovementRequest request) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            request.setDescription(request.getDescription() + " [idempotency:" + idempotencyKey + "]");
        }
        return ResponseEntity.ok(accountService.creditAccount(accountId, request));
    }

    @PostMapping("/{accountId}/debit")
    public ResponseEntity<MoneyMovementResponse> debitAccount(@PathVariable Long accountId,
                                                              @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                              @RequestBody AccountMovementRequest request) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            request.setDescription(request.getDescription() + " [idempotency:" + idempotencyKey + "]");
        }
        return ResponseEntity.ok(accountService.debitAccount(accountId, request));
    }
}
