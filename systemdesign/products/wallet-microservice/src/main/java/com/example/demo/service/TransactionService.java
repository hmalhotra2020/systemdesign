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
import com.example.demo.model.IdempotencyIntent;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.EntryType;
import com.example.demo.model.enums.IntentState;
import com.example.demo.model.enums.OperationType;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.model.request.TransferRequest;
import com.example.demo.model.response.MoneyMovementResponse;

@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final IntentDao intentDao;

    public TransactionService(TransactionDao transactionDao, AccountDao accountDao, IntentDao intentDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.intentDao = intentDao;
    }

    public TransactionRecord getTransactionById(Long transactionId) {
        return transactionDao.findById(transactionId);
    }

    public List<TransactionRecord> getTransactionsByCustomer(Long customerId, int limit) {
        return transactionDao.findByCustomerId(customerId, limit);
    }

    public List<TransactionRecord> getTransactionsByAccount(Long accountId, int limit) {
        return transactionDao.findByAccountId(accountId, limit);
    }

    @Transactional
    public MoneyMovementResponse transferMoney(TransferRequest request) {
        validateRequest(request);
        Account sender = getAccountOrThrow(request.getSenderAccountId());
        Account receiver = getAccountOrThrow(request.getReceiverAccountId());
        if (!isEligibleForTransfer(sender, request.isAllowDebit(), request.isBlacklisted()) || !isEligibleForTransfer(receiver, request.isAllowCredit(), request.isBlacklisted())) {
            throw new IllegalArgumentException("one or both accounts are not eligible for transfer");
        }
        if (!sender.currency().equals(receiver.currency())) {
            throw new IllegalArgumentException("currency mismatch between accounts");
        }
        String key = resolveIdempotencyKey(request);
        Optional<IdempotencyIntent> existing = intentDao.findByKey(key);
        if (existing.isPresent()) {
            LOGGER.info("Returning existing transfer intent {}", key);
            return new MoneyMovementResponse(existing.get().intentId(), existing.get().state().name(), "duplicate request");
        }
        BigDecimal amount = request.getAmount().abs();
        if (sender.availableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("insufficient funds");
        }
        BigDecimal senderAvailable = sender.availableBalance().subtract(amount);
        BigDecimal senderLedger = sender.ledgerBalance().subtract(amount);
        BigDecimal receiverAvailable = receiver.availableBalance().add(amount);
        BigDecimal receiverLedger = receiver.ledgerBalance().add(amount);
        accountDao.updateBalances(sender.accountId(), senderAvailable, senderLedger);
        accountDao.updateBalances(receiver.accountId(), receiverAvailable, receiverLedger);
        Long senderLedgerEntryId = transactionDao.insertLedgerEntry(sender.accountId(), receiver.accountId(), EntryType.DEBIT.name(), amount, request.getCurrency(), request.getDescription(), key);
        Long receiverLedgerEntryId = transactionDao.insertLedgerEntry(receiver.accountId(), sender.accountId(), EntryType.CREDIT.name(), amount, request.getCurrency(), request.getDescription(), key);
        Long senderTransactionId = transactionDao.insertTransaction(sender.customerId(), sender.accountId(), senderLedgerEntryId, TransactionType.TRANSFER.name(), amount, request.getCurrency(), TransactionStatus.COMPLETED.name(), "{\"transfer\":true}");
        transactionDao.insertTransaction(receiver.customerId(), receiver.accountId(), receiverLedgerEntryId, TransactionType.TRANSFER.name(), amount, request.getCurrency(), TransactionStatus.COMPLETED.name(), "{\"transfer\":true}");
        intentDao.insert(key, sender.customerId(), OperationType.TRANSFER.name(), "hash", IntentState.COMPLETED.name());
        return new MoneyMovementResponse(senderTransactionId, TransactionStatus.COMPLETED.name(), "transferred");
    }

    private Account getAccountOrThrow(Long accountId) {
        return accountDao.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    private boolean isEligibleForTransfer(Account account, boolean operationAllowed, boolean blacklisted) {
        return account.status() == AccountStatus.ACTIVE
                && !blacklisted
                && operationAllowed;
    }

    private void validateRequest(TransferRequest request) {
        if (request == null || request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }
        if (request.getSenderAccountId() == null || request.getReceiverAccountId() == null) {
            throw new IllegalArgumentException("sender and receiver accounts are required");
        }
        if (request.getSenderAccountId().equals(request.getReceiverAccountId())) {
            throw new IllegalArgumentException("sender and receiver must be different");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }
    }

    private String resolveIdempotencyKey(TransferRequest request) {
        return request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()
                ? request.getIdempotencyKey()
                : "transfer:" + request.getSenderAccountId() + ":" + request.getReceiverAccountId() + ":" + request.getAmount() + ":" + request.getCurrency();
    }
}
