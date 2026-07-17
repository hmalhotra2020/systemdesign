package com.example.demo.dao;

import java.time.OffsetDateTime;
import java.util.List;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.example.demo.model.CustomerActivity;
import com.example.demo.model.LedgerMismatch;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.TransactionTrendPoint;
import com.example.demo.model.TransactionSummary;
import com.example.demo.model.CustomerRiskProfile;

public interface AdminAnalyticsDao {

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE occurred_at >= :from AND occurred_at < :to")
    long countTransactions(@Bind("from") OffsetDateTime from, @Bind("to") OffsetDateTime to);

    @SqlQuery("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE occurred_at >= :from AND occurred_at < :to AND currency = 'INR'")
    java.math.BigDecimal sumTransactionVolume(@Bind("from") OffsetDateTime from, @Bind("to") OffsetDateTime to);

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE occurred_at >= :from AND occurred_at < :to AND status = :status")
    long countTransactionsByStatus(@Bind("from") OffsetDateTime from, @Bind("to") OffsetDateTime to, @Bind("status") String status);

    @SqlQuery("SELECT DATE_TRUNC(:period, occurred_at)::text AS period, COUNT(*) AS count FROM transactions WHERE occurred_at >= :from AND occurred_at < :to GROUP BY 1 ORDER BY 1")
    List<TransactionTrendPoint> findTransactionTrends(@Bind("from") OffsetDateTime from, @Bind("to") OffsetDateTime to, @Bind("period") String period);

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'PENDING'")
    long countPendingTransactions();

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'PROCESSING'")
    long countProcessingTransactions();

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'COMPLETED'")
    long countCompletedTransactions();

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'FAILED'")
    long countFailedTransactions();

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'CANCELLED'")
    long countCancelledTransactions();

    @SqlQuery("SELECT COUNT(*) FROM transactions WHERE status = 'REVERSED'")
    long countReversedTransactions();

    @SqlQuery("SELECT a.account_id, a.available_balance, a.ledger_balance, COALESCE(SUM(CASE WHEN entry_type = 'CREDIT' THEN amount ELSE -amount END),0) AS expectedLedgerBalance FROM accounts a LEFT JOIN ledger_entries l ON a.account_id = l.account_id GROUP BY a.account_id, a.available_balance, a.ledger_balance HAVING COALESCE(SUM(CASE WHEN entry_type = 'CREDIT' THEN amount ELSE -amount END),0) <> a.ledger_balance")
    List<LedgerMismatch> findLedgerMismatches();

    @SqlQuery("SELECT c.customer_id, COUNT(t.transaction_id) AS transactionCount, COALESCE(SUM(t.amount),0) AS totalVolume FROM customers c JOIN transactions t ON c.customer_id = t.customer_id WHERE t.occurred_at >= :from AND t.occurred_at < :to GROUP BY c.customer_id ORDER BY COUNT(t.transaction_id) DESC LIMIT :limit")
    List<CustomerActivity> findTopActiveCustomers(@Bind("from") OffsetDateTime from, @Bind("to") OffsetDateTime to, @Bind("limit") int limit);

    @SqlQuery("SELECT c.customer_id, COUNT(t.transaction_id) AS transactionCount, COALESCE(SUM(t.amount),0) AS totalVolume FROM customers c JOIN transactions t ON c.customer_id = t.customer_id WHERE t.amount >= :threshold AND t.currency = 'INR' GROUP BY c.customer_id ORDER BY COALESCE(SUM(t.amount),0) DESC")
    List<CustomerActivity> findHighValueCustomers(@Bind("threshold") java.math.BigDecimal threshold);

    @SqlQuery("SELECT " +
              "c.customer_id AS customerId, " +
              "COALESCE(t.txn_count, 0) AS transactionCount, " +
              "COALESCE(t.total_volume, 0.0) AS totalVolume, " +
              "COALESCE(i.pending_count, 0) AS pendingIntents, " +
              "COALESCE(i.completed_count, 0) AS completedIntents, " +
              "(COALESCE(t.txn_count, 0) * 1.5 + COALESCE(i.completed_count, 0) * 0.5 + COALESCE(i.pending_count, 0) * 0.2)::float8 AS activityScore, " +
              "CASE " +
              "    WHEN (COALESCE(t.txn_count, 0) * 1.5 + COALESCE(i.completed_count, 0) * 0.5 + COALESCE(i.pending_count, 0) * 0.2) > 30 THEN 'HIGH' " +
              "    WHEN (COALESCE(t.txn_count, 0) * 1.5 + COALESCE(i.completed_count, 0) * 0.5 + COALESCE(i.pending_count, 0) * 0.2) > 10 THEN 'MEDIUM' " +
              "    ELSE 'LOW' " +
              "END AS riskLabel " +
              "FROM customers c " +
              "LEFT JOIN ( " +
              "    SELECT customer_id, COUNT(*) AS txn_count, SUM(amount) AS total_volume " +
              "    FROM transactions " +
              "    GROUP BY customer_id " +
              ") t ON c.customer_id = t.customer_id " +
              "LEFT JOIN ( " +
              "    SELECT customer_id, " +
              "           COUNT(CASE WHEN state = 'PENDING' THEN 1 END) AS pending_count, " +
              "           COUNT(CASE WHEN state = 'COMPLETED' THEN 1 END) AS completed_count " +
              "    FROM intents " +
              "    GROUP BY customer_id " +
              ") i ON c.customer_id = i.customer_id " +
              "WHERE c.customer_id = :customerId")
    CustomerRiskProfile findCustomerRiskProfile(@Bind("customerId") Long customerId);


    @SqlQuery("SELECT * FROM transactions WHERE transaction_id = :transactionId")
    TransactionRecord findTransactionById(@Bind("transactionId") Long transactionId);

    @org.jdbi.v3.sqlobject.statement.SqlUpdate("UPDATE transactions SET status = :status, updated_at = CURRENT_TIMESTAMP WHERE transaction_id = :transactionId")
    void updateTransactionStatus(@Bind("transactionId") Long transactionId, @Bind("status") String status);
}
