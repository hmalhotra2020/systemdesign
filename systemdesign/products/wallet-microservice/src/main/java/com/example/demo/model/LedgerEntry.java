package com.example.demo.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.demo.model.enums.EntryType;

public record LedgerEntry(
        Long ledgerId,
        Long accountId,
        Long relatedAccountId,
        EntryType entryType,
        BigDecimal amount,
        String currency,
        OffsetDateTime entryDate,
        String description,
        String referenceId,
        OffsetDateTime createdAt) {
}
