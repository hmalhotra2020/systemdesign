package com.example.demo.config;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import com.example.demo.dao.AccountDao;
import com.example.demo.dao.CustomerDao;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;
    private final CustomerDao customerDao;
    private final AccountDao accountDao;

    public DatabaseInitializer(DataSource dataSource, CustomerDao customerDao, AccountDao accountDao) {
        this.dataSource = dataSource;
        this.customerDao = customerDao;
        this.accountDao = accountDao;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run only when explicit init-db arg is passed
        if (args != null && Arrays.stream(args).anyMatch(a -> "init-db".equals(a) || "--init-db".equals(a))) {
            LOGGER.info("init-db arg detected, initializing database");
            initializeDatabase();
        }
        if (args != null && Arrays.stream(args).anyMatch(a -> "seed-db".equals(a) || "--seed-db".equals(a))) {
            LOGGER.info("seed-db arg detected, seeding database");
            seedDatabase();
        }
    }

    public synchronized void initializeDatabase() {
        LOGGER.info("Running database schema script");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("ddl/schema.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);

        ensureSystemCustomerAndAccount();
    }

    public synchronized void seedDatabase() {
        LOGGER.info("Running database seed script");
        try (java.sql.Connection conn = dataSource.getConnection()) {
            ClassPathResource resource = new ClassPathResource("ddl/seed_data.sql");
            try (java.io.InputStream is = resource.getInputStream()) {
                String sql = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    LOGGER.info("Database seeding completed successfully");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to seed database", e);
            throw new RuntimeException("Failed to seed database", e);
        }
    }



    private void ensureSystemCustomerAndAccount() {
        try {
            var maybe = customerDao.findByExternalId("system.customer");
            Long systemCustomerId;
            if (maybe.isPresent()) {
                systemCustomerId = maybe.get().customerId();
                LOGGER.info("System customer already exists: {}", systemCustomerId);
            } else {
                systemCustomerId = customerDao.insert("system.customer", "system@localhost", "0000", "System", "Account", "ACTIVE");
                LOGGER.info("Created system customer id={}", systemCustomerId);
            }

            // create or ensure system account
            var maybeAccount = accountDao.findByCustomerId(systemCustomerId).stream().findFirst();
            if (maybeAccount.isPresent()) {
                LOGGER.info("System account already exists: {}", maybeAccount.get().accountId());
            } else {
                BigDecimal huge = new BigDecimal("1000000000000.00");
                Long accountId = accountDao.insert(systemCustomerId, "SYSTEM", "USD", huge, huge, BigDecimal.ZERO, "OPEN");
                LOGGER.info("Created system account id={}", accountId);
            }
        } catch (Exception e) {
            LOGGER.error("Error ensuring system customer/account", e);
        }
    }
}
