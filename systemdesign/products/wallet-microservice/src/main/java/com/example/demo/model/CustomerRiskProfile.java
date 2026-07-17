package com.example.demo.model;

import java.math.BigDecimal;

public record CustomerRiskProfile(
        Long customerId,
        long transactionCount,
        BigDecimal totalVolume,
        long pendingIntents,
        long completedIntents,
        double activityScore,
        String riskLabel) {
}
