package com.example.demo.model;

import java.time.OffsetDateTime;

import com.example.demo.model.enums.CustomerStatus;

public record Customer(
        Long customerId,
        String externalCustomerId,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        CustomerStatus status) {
}
