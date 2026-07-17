package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.AccountDao;
import com.example.demo.dao.IntentDao;
import com.example.demo.dao.TransactionDao;
import com.example.demo.model.Account;
import com.example.demo.model.request.AccountCreateRequest;
import com.example.demo.model.LedgerEntry;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.enums.EntryType;
import com.example.demo.model.IdempotencyIntent;
import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.EntryType;
import com.example.demo.model.enums.IntentState;
import com.example.demo.model.enums.OperationType;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.model.request.AccountMovementRequest;
import com.example.demo.model.response.MoneyMovementResponse;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final IntentDao intentDao;

    public AccountService(AccountDao accountDao, TransactionDao transactionDao, IntentDao intentDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.intentDao = intentDao;
    }

    public Account createAccount(AccountCreateRequest request) {
        Long id = accountDao.insert(request.customerId(), request.accountType(), request.currency(), request.initialAvailable() == null ? java.math.BigDecimal.ZERO : request.initialAvailable(), request.initialLedger() == null ? java.math.BigDecimal.ZERO : request.initialLedger(), request.creditLimit() == null ? java.math.BigDecimal.ZERO : request.creditLimit(), "OPEN");
        return accountDao.findById(id).orElseThrow(() -> new IllegalStateException("failed to load created account"));
    }

    public java.util.Map<String, Object> balance(Long accountId) {
        Account account = getAccountOrThrow(accountId);
        var m = new java.util.HashMap<String, Object>();
        m.put("accountId", account.accountId());
        m.put("availableBalance", account.availableBalance());
        m.put("ledgerBalance", account.ledgerBalance());
        return m;
    }

    public java.util.Map<String, Object> ledgerCheck(Long accountId, java.time.OffsetDateTime from, java.time.OffsetDateTime to) {
        java.util.List<LedgerEntry> entries = transactionDao.findLedgerEntriesBetween(accountId, from, to);
        java.math.BigDecimal net = java.math.BigDecimal.ZERO;
        for (LedgerEntry e : entries) {
            if (e.entryType() == EntryType.CREDIT) {
                net = net.add(e.amount());
            } else if (e.entryType() == EntryType.DEBIT) {
                net = net.subtract(e.amount());
            }
        }
        Account account = getAccountOrThrow(accountId);
        var out = new java.util.HashMap<String, Object>();
        out.put("accountId", accountId);
        out.put("netMovement", net);
        out.put("currentLedgerBalance", account.ledgerBalance());
        out.put("entries", entries);
        return out;
    }

    @Transactional
    public MoneyMovementResponse creditAccount(Long accountId, AccountMovementRequest request) {
        validateRequest(request);
        Account account = getAccountOrThrow(accountId);
        if (!isAccountEligible(account, request.isAllowCredit(), request.isBlacklisted())) {
            throw new IllegalArgumentException("Account is not eligible for credit");
        }
        return executeMovement(account, request, true, OperationType.LOAD, TransactionType.CREDIT, EntryType.CREDIT);
    }

    @Transactional
    public MoneyMovementResponse debitAccount(Long accountId, AccountMovementRequest request) {
        validateRequest(request);
        Account account = getAccountOrThrow(accountId);
        if (!isAccountEligible(account, request.isAllowDebit(), request.isBlacklisted())) {
            throw new IllegalArgumentException("Account is not eligible for debit");
        }
        return executeMovement(account, request, false, OperationType.WITHDRAWAL, TransactionType.WITHDRAWAL, EntryType.DEBIT);
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accountDao.findById(accountId);
    }

    public List<Account> getAccountsByCustomer(Long customerId) {
        return accountDao.findByCustomerId(customerId);
    }

    public Optional<Account> getAccountByCustomerAndType(Long customerId, String accountType) {
        return accountDao.findByCustomerIdAndType(customerId, accountType);
    }

    private MoneyMovementResponse executeMovement(Account account, AccountMovementRequest request, boolean credit, OperationType operationType, TransactionType transactionType, EntryType entryType) {
        String idempotencyKey = buildIdempotencyKey(account.accountId(), operationType, request.getAmount(), request.getCurrency());
        Optional<IdempotencyIntent> existing = intentDao.findByKey(idempotencyKey);
        if (existing.isPresent()) {
            LOGGER.info("Returning existing intent {} for account {}", idempotencyKey, account.accountId());
            return new MoneyMovementResponse(existing.get().intentId(), existing.get().state().name(), "duplicate request");
        }
        LOGGER.info("Processing {} for account {} amount {} {}", operationType, account.accountId(), request.getAmount(), request.getCurrency());
        BigDecimal amount = request.getAmount().abs();
        BigDecimal newAvailableBalance = credit
                ? account.availableBalance().add(amount)
                : account.availableBalance().subtract(amount);
        BigDecimal newLedgerBalance = credit
                ? account.ledgerBalance().add(amount)
                : account.ledgerBalance().subtract(amount);
        accountDao.updateBalances(account.accountId(), newAvailableBalance, newLedgerBalance);
        Long ledgerEntryId = transactionDao.insertLedgerEntry(account.accountId(), null, entryType.name(), amount, request.getCurrency(), request.getDescription(), idempotencyKey);
        Long transactionId = transactionDao.insertTransaction(account.customerId(), account.accountId(), ledgerEntryId, transactionType.name(), amount, request.getCurrency(), TransactionStatus.COMPLETED.name(), "{\"idempotencyKey\":\"" + idempotencyKey + "\"}");
        intentDao.insert(idempotencyKey, account.customerId(), operationType.name(), "hash", IntentState.COMPLETED.name());
        return new MoneyMovementResponse(transactionId, TransactionStatus.COMPLETED.name(), credit ? "credited" : "debited");
    }

    private Account getAccountOrThrow(Long accountId) {
        return accountDao.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private boolean isAccountEligible(Account account, boolean operationAllowed, boolean blacklisted) {
        return account.status() == AccountStatus.ACTIVE
                && !blacklisted
                && operationAllowed;
    }

    private void validateRequest(AccountMovementRequest request) {
        if (request == null || request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }
    }

    private String buildIdempotencyKey(Long accountId, OperationType operationType, BigDecimal amount, String currency) {
        return operationType.name() + ":" + accountId + ":" + amount + ":" + currency;
    }
}
