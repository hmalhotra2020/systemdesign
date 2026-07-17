package com.example.demo.model;

import java.math.BigDecimal;

public record TransactionSummary(
        String range,
        long totalCount,
        BigDecimal totalVolume,
        long successCount,
        long failureCount,
        double successRate,
        double failureRate) {
}
