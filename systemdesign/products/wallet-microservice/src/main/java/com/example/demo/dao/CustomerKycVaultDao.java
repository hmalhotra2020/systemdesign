package com.example.demo.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.example.demo.model.CustomerKycVault;

public interface CustomerKycVaultDao {

    @SqlQuery("SELECT * FROM customer_kyc_vault WHERE customer_id = :customerId")
    List<CustomerKycVault> findByCustomerId(@Bind("customerId") Long customerId);

    @SqlQuery("SELECT * FROM customer_kyc_vault WHERE kyc_id = :kycId")
    CustomerKycVault findById(@Bind("kycId") Long kycId);
}
