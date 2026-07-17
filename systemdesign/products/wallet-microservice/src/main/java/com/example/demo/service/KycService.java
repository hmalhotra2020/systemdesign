package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.dao.CustomerKycVaultDao;
import com.example.demo.model.CustomerKycVault;

@Service
public class KycService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KycService.class);

    private final CustomerKycVaultDao kycDao;

    public KycService(CustomerKycVaultDao kycDao) {
        this.kycDao = kycDao;
    }

    public List<CustomerKycVault> getKycRecordsForCustomer(Long customerId) {
        LOGGER.info("Fetching KYC records for customer {}", customerId);
        return kycDao.findByCustomerId(customerId);
    }

    public CustomerKycVault getKycById(Long kycId) {
        LOGGER.info("Fetching KYC record {}", kycId);
        return kycDao.findById(kycId);
    }
}
