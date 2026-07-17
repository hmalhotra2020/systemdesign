package com.example.demo.dao;

import java.util.List;

import java.math.BigDecimal;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.example.demo.model.TransactionRecord;

public interface TransactionDao {

    @SqlQuery("SELECT * FROM transactions WHERE transaction_id = :transactionId")
    TransactionRecord findById(@Bind("transactionId") Long transactionId);

    @SqlQuery("SELECT * FROM transactions WHERE customer_id = :customerId ORDER BY occurred_at DESC LIMIT :limit")
    List<TransactionRecord> findByCustomerId(@Bind("customerId") Long customerId, @Bind("limit") int limit);

    @SqlQuery("SELECT * FROM transactions WHERE account_id = :accountId ORDER BY occurred_at DESC LIMIT :limit")
    List<TransactionRecord> findByAccountId(@Bind("accountId") Long accountId, @Bind("limit") int limit);

    @SqlUpdate("INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, description, reference_id) VALUES (:accountId, :relatedAccountId, :entryType, :amount, :currency, :description, :referenceId)")
    @GetGeneratedKeys("ledger_id")
    Long insertLedgerEntry(@Bind("accountId") Long accountId,
                           @Bind("relatedAccountId") Long relatedAccountId,
                           @Bind("entryType") String entryType,
                           @Bind("amount") BigDecimal amount,
                           @Bind("currency") String currency,
                           @Bind("description") String description,
                           @Bind("referenceId") String referenceId);

    @SqlUpdate("INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, metadata, occurred_at, created_at) VALUES (:customerId, :accountId, :ledgerEntryId, :transactionType, :amount, :currency, :status, :metadata, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys("transaction_id")
    Long insertTransaction(@Bind("customerId") Long customerId,
                           @Bind("accountId") Long accountId,
                           @Bind("ledgerEntryId") Long ledgerEntryId,
                           @Bind("transactionType") String transactionType,
                           @Bind("amount") BigDecimal amount,
                           @Bind("currency") String currency,
                           @Bind("status") String status,
                           @Bind("metadata") String metadata);

    @SqlQuery("SELECT ledger_id, account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at FROM ledger_entries WHERE account_id = :accountId AND entry_date BETWEEN :from AND :to ORDER BY entry_date")
    List<com.example.demo.model.LedgerEntry> findLedgerEntriesBetween(@Bind("accountId") Long accountId,
                                                                      @Bind("from") java.time.OffsetDateTime from,
                                                                      @Bind("to") java.time.OffsetDateTime to);

    @SqlQuery("SELECT * FROM transactions WHERE account_id = :accountId AND occurred_at BETWEEN :from AND :to ORDER BY occurred_at")
    List<com.example.demo.model.TransactionRecord> findTransactionsBetween(@Bind("accountId") Long accountId,
                                                                            @Bind("from") java.time.OffsetDateTime from,
                                                                            @Bind("to") java.time.OffsetDateTime to);
}
