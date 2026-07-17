package com.example.demo.model.request;

import java.math.BigDecimal;

public record AccountCreateRequest(
        Long customerId,
        String accountType,
        String currency,
        BigDecimal initialAvailable,
        BigDecimal initialLedger,
        BigDecimal creditLimit
) {}
