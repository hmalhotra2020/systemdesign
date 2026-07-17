package com.example.demo.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;

public record TransactionRecord(
        Long transactionId,
        Long customerId,
        Long accountId,
        Long ledgerEntryId,
        TransactionType transactionType,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        String metadata,
        OffsetDateTime occurredAt,
        OffsetDateTime createdAt) {
}
