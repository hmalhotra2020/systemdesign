package com.example.demo.model;

import java.math.BigDecimal;

public record LedgerMismatch(
        Long accountId,
        BigDecimal availableBalance,
        BigDecimal ledgerBalance,
        BigDecimal expectedLedgerBalance) {
}
