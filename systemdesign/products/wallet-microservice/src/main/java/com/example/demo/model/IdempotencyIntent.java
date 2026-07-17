package com.example.demo.model;

import java.time.OffsetDateTime;

import com.example.demo.model.enums.IntentState;
import com.example.demo.model.enums.OperationType;

public record IdempotencyIntent(
        Long intentId,
        String idempotencyKey,
        Long customerId,
        OperationType operationType,
        String requestPayloadHash,
        IntentState state,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
