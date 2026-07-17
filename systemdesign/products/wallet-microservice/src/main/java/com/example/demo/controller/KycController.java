package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CustomerKycVault;
import com.example.demo.service.KycService;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KycController.class);

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerKycVault>> getCustomerKyc(@PathVariable Long customerId) {
        LOGGER.info("Fetching KYC records for customer {}", customerId);
        return ResponseEntity.ok(kycService.getKycRecordsForCustomer(customerId));
    }

    @GetMapping("/{kycId}")
    public ResponseEntity<CustomerKycVault> getKyc(@PathVariable Long kycId) {
        LOGGER.info("Fetching KYC record {}", kycId);
        return ResponseEntity.ok(kycService.getKycById(kycId));
    }
}
