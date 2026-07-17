package com.example.demo.model.request;

public record CustomerRequest(
        String externalCustomerId,
        String email,
        String phoneNumber,
        String firstName,
        String lastName
) {}
