package com.example.demo.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.AdminAnalyticsDao;
import com.example.demo.model.CustomerActivity;
import com.example.demo.model.CustomerRiskProfile;
import com.example.demo.model.LedgerMismatch;
import com.example.demo.model.SystemHealthStatus;
import com.example.demo.model.TransactionSummary;
import com.example.demo.model.TransactionTrendPoint;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.enums.TransactionStatus;

@Service
public class AdminAnalyticsService {

    private final AdminAnalyticsDao analyticsDao;

    public AdminAnalyticsService(AdminAnalyticsDao analyticsDao) {
        this.analyticsDao = analyticsDao;
    }

    public TransactionSummary getTransactionSummary(String range) {
        OffsetDateTime to = OffsetDateTime.now();
        OffsetDateTime from = switch (range.toLowerCase()) {
            case "daily" -> to.minus(1, ChronoUnit.DAYS);
            case "weekly" -> to.minus(7, ChronoUnit.DAYS);
            case "monthly" -> to.minus(30, ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException("range must be daily, weekly or monthly");
        };
        long totalCount = analyticsDao.countTransactions(from, to);
        BigDecimal totalVolume = analyticsDao.sumTransactionVolume(from, to);
        long successCount = analyticsDao.countTransactionsByStatus(from, to, TransactionStatus.COMPLETED.name());
        long failureCount = analyticsDao.countTransactionsByStatus(from, to, TransactionStatus.FAILED.name());
        double successRate = totalCount == 0 ? 0.0 : successCount * 100.0 / totalCount;
        double failureRate = totalCount == 0 ? 0.0 : failureCount * 100.0 / totalCount;
        return new TransactionSummary(range.toLowerCase(), totalCount, totalVolume, successCount, failureCount, successRate, failureRate);
    }

    public List<TransactionTrendPoint> getTransactionVolumeTrends(String range) {
        OffsetDateTime to = OffsetDateTime.now();
        OffsetDateTime from = switch (range.toLowerCase()) {
            case "daily" -> to.minus(1, ChronoUnit.DAYS);
            case "weekly" -> to.minus(7, ChronoUnit.DAYS);
            case "monthly" -> to.minus(30, ChronoUnit.DAYS);
            default -> throw new IllegalArgumentException("range must be daily, weekly or monthly");
        };
        String period = switch (range.toLowerCase()) {
            case "daily" -> "hour";
            case "weekly", "monthly" -> "day";
            default -> "day";
        };
        return analyticsDao.findTransactionTrends(from, to, period);
    }

    public SystemHealthStatus getSystemHealth() {
        long pendingCount = analyticsDao.countPendingTransactions();
        long processingCount = analyticsDao.countProcessingTransactions();
        long completedCount = analyticsDao.countCompletedTransactions();
        long failedCount = analyticsDao.countFailedTransactions();
        long cancelledCount = analyticsDao.countCancelledTransactions();
        long reversedCount = analyticsDao.countReversedTransactions();
        long totalCount = pendingCount + processingCount + completedCount + failedCount + cancelledCount + reversedCount;
        return new SystemHealthStatus(pendingCount, processingCount, completedCount, failedCount, cancelledCount, reversedCount, totalCount);
    }

    @Transactional(readOnly = true)
    public List<LedgerMismatch> getLedgerMismatches() {
        return analyticsDao.findLedgerMismatches();
    }

    @Transactional
    public TransactionRecord reconcileTransaction(Long transactionId) {
        TransactionRecord transaction = analyticsDao.findTransactionById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("transaction not found");
        }
        if (transaction.status() == TransactionStatus.PENDING) {
            analyticsDao.updateTransactionStatus(transactionId, TransactionStatus.COMPLETED.name());
            return analyticsDao.findTransactionById(transactionId);
        }
        return transaction;
    }

    public List<CustomerActivity> getTopActiveCustomers(int limit) {
        OffsetDateTime to = OffsetDateTime.now();
        OffsetDateTime from = to.minus(30, ChronoUnit.DAYS);
        return analyticsDao.findTopActiveCustomers(from, to, limit);
    }

    public List<CustomerActivity> getHighValueCustomers(BigDecimal threshold) {
        return analyticsDao.findHighValueCustomers(threshold);
    }

    public CustomerRiskProfile getCustomerRiskProfile(Long customerId) {
        return analyticsDao.findCustomerRiskProfile(customerId);
    }
}
