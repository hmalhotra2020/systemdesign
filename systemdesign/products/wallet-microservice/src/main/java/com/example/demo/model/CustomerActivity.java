package com.example.demo.model;

import java.math.BigDecimal;

public record CustomerActivity(
        Long customerId,
        long transactionCount,
        BigDecimal totalVolume) {
}
