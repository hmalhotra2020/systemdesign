package com.example.demo.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.AccountType;

public record Account(
        Long accountId,
        Long customerId,
        AccountType accountType,
        String currency,
        BigDecimal availableBalance,
        BigDecimal ledgerBalance,
        BigDecimal creditLimit,
        AccountStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
