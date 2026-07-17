package com.example.demo.model;

import java.time.OffsetDateTime;

public record CreditAssessment(
        Long assessmentId,
        Long customerId,
        Integer score,
        Boolean eligibility,
        OffsetDateTime assessedAt,
        String assessedBy,
        String details) {
}
