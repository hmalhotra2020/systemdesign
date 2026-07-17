package com.example.demo.model;

import java.time.OffsetDateTime;

import com.example.demo.model.enums.VerificationStatus;

public record CustomerKycVault(
        Long kycId,
        Long customerId,
        String documentType,
        String documentReference,
        String vaultPath,
        VerificationStatus verificationStatus,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
