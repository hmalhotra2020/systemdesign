package com.example.demo.config;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.dao.AccountDao;
import com.example.demo.dao.AdminAnalyticsDao;
import com.example.demo.dao.CreditAssessmentDao;
import com.example.demo.dao.CustomerDao;
import com.example.demo.dao.CustomerKycVaultDao;
import com.example.demo.dao.IntentDao;
import com.example.demo.dao.TransactionDao;
import com.example.demo.model.Account;
import com.example.demo.model.CreditAssessment;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerActivity;
import com.example.demo.model.CustomerKycVault;
import com.example.demo.model.CustomerRiskProfile;
import com.example.demo.model.IdempotencyIntent;
import com.example.demo.model.LedgerEntry;
import com.example.demo.model.LedgerMismatch;
import com.example.demo.model.SystemHealthStatus;
import com.example.demo.model.TransactionRecord;
import com.example.demo.model.TransactionSummary;
import com.example.demo.model.TransactionTrendPoint;

@Configuration
public class JdbiConfiguration {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource)
                .installPlugin(new SqlObjectPlugin())
                .registerRowMapper(ConstructorMapper.factory(Customer.class))
                .registerRowMapper(ConstructorMapper.factory(Account.class))
                .registerRowMapper(ConstructorMapper.factory(CustomerKycVault.class))
                .registerRowMapper(ConstructorMapper.factory(CreditAssessment.class))
                .registerRowMapper(ConstructorMapper.factory(IdempotencyIntent.class))
                .registerRowMapper(ConstructorMapper.factory(LedgerEntry.class))
                .registerRowMapper(ConstructorMapper.factory(TransactionRecord.class))
                .registerRowMapper(ConstructorMapper.factory(TransactionSummary.class))
                .registerRowMapper(ConstructorMapper.factory(TransactionTrendPoint.class))
                .registerRowMapper(ConstructorMapper.factory(SystemHealthStatus.class))
                .registerRowMapper(ConstructorMapper.factory(LedgerMismatch.class))
                .registerRowMapper(ConstructorMapper.factory(CustomerActivity.class))
                .registerRowMapper(ConstructorMapper.factory(CustomerRiskProfile.class));
    }

    @Bean
    public CustomerDao customerDao(Jdbi jdbi) {
        return jdbi.onDemand(CustomerDao.class);
    }

    @Bean
    public AdminAnalyticsDao adminAnalyticsDao(Jdbi jdbi) {
        return jdbi.onDemand(AdminAnalyticsDao.class);
    }

    @Bean
    public AccountDao accountDao(Jdbi jdbi) {
        return jdbi.onDemand(AccountDao.class);
    }

    @Bean
    public TransactionDao transactionDao(Jdbi jdbi) {
        return jdbi.onDemand(TransactionDao.class);
    }

    @Bean
    public IntentDao intentDao(Jdbi jdbi) {
        return jdbi.onDemand(IntentDao.class);
    }

    @Bean
    public CustomerKycVaultDao customerKycVaultDao(Jdbi jdbi) {
        return jdbi.onDemand(CustomerKycVaultDao.class);
    }

    @Bean
    public CreditAssessmentDao creditAssessmentDao(Jdbi jdbi) {
        return jdbi.onDemand(CreditAssessmentDao.class);
    }
}
