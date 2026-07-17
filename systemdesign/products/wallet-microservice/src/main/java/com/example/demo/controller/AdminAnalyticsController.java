package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CustomerActivity;
import com.example.demo.model.CustomerRiskProfile;
import com.example.demo.model.LedgerMismatch;
import com.example.demo.model.SystemHealthStatus;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.TransactionSummary;
import com.example.demo.model.TransactionTrendPoint;
import com.example.demo.service.AdminAnalyticsService;

@RestController
@RequestMapping("/admin")
public class AdminAnalyticsController {

    private final AdminAnalyticsService analyticsService;

    public AdminAnalyticsController(AdminAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics/txns/summary")
    public ResponseEntity<TransactionSummary> getTxnSummary(@RequestParam(defaultValue = "daily") String range) {
        return ResponseEntity.ok(analyticsService.getTransactionSummary(range));
    }

    @GetMapping("/analytics/txns/volume-trends")
    public ResponseEntity<List<TransactionTrendPoint>> getVolumeTrends(@RequestParam(defaultValue = "weekly") String range) {
        return ResponseEntity.ok(analyticsService.getTransactionVolumeTrends(range));
    }

    @GetMapping("/analytics/system-health")
    public ResponseEntity<SystemHealthStatus> getSystemHealth() {
        return ResponseEntity.ok(analyticsService.getSystemHealth());
    }

    @PostMapping("/audit/ledger/verify")
    public ResponseEntity<Map<String, Object>> verifyLedger() {
        List<LedgerMismatch> mismatches = analyticsService.getLedgerMismatches();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "COMPLETED");
        response.put("verifiedAt", java.time.OffsetDateTime.now());
        response.put("mismatchCount", mismatches.size());
        response.put("mismatches", mismatches);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audit/ledger/mismatches")
    public ResponseEntity<List<LedgerMismatch>> getLedgerMismatches() {
        return ResponseEntity.ok(analyticsService.getLedgerMismatches());
    }

    @PostMapping("/transactions/{transactionId}/reconcile")
    public ResponseEntity<TransactionRecord> reconcileTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(analyticsService.reconcileTransaction(transactionId));
    }

    @GetMapping("/customers/top-active")
    public ResponseEntity<List<CustomerActivity>> getTopActiveCustomers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopActiveCustomers(limit));
    }

    @GetMapping("/customers/high-value")
    public ResponseEntity<List<CustomerActivity>> getHighValueCustomers(@RequestParam(defaultValue = "10000") BigDecimal threshold) {
        return ResponseEntity.ok(analyticsService.getHighValueCustomers(threshold));
    }

    @GetMapping("/customers/{customerId}/risk-profile")
    public ResponseEntity<CustomerRiskProfile> getCustomerRiskProfile(@PathVariable Long customerId) {
        return ResponseEntity.ok(analyticsService.getCustomerRiskProfile(customerId));
    }
}
