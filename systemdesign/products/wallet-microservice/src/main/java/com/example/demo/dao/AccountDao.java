package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.example.demo.model.Account;

public interface AccountDao {

    @SqlQuery("SELECT * FROM accounts WHERE account_id = :accountId")
    Optional<Account> findById(@Bind("accountId") Long accountId);

    @SqlQuery("SELECT * FROM accounts WHERE customer_id = :customerId")
    List<Account> findByCustomerId(@Bind("customerId") Long customerId);

    @SqlQuery("SELECT * FROM accounts WHERE customer_id = :customerId AND account_type = :accountType")
    Optional<Account> findByCustomerIdAndType(@Bind("customerId") Long customerId,
                                               @Bind("accountType") String accountType);

    @SqlUpdate("UPDATE accounts SET available_balance = :availableBalance, ledger_balance = :ledgerBalance, updated_at = CURRENT_TIMESTAMP WHERE account_id = :accountId")
    void updateBalances(@Bind("accountId") Long accountId,
                        @Bind("availableBalance") java.math.BigDecimal availableBalance,
                        @Bind("ledgerBalance") java.math.BigDecimal ledgerBalance);

    @SqlUpdate("INSERT INTO accounts (customer_id, account_type, currency, available_balance, ledger_balance, credit_limit, status) VALUES (:customerId, :accountType, :currency, :availableBalance, :ledgerBalance, :creditLimit, :status)")
    @org.jdbi.v3.sqlobject.statement.GetGeneratedKeys("account_id")
    Long insert(@Bind("customerId") Long customerId,
                @Bind("accountType") String accountType,
                @Bind("currency") String currency,
                @Bind("availableBalance") java.math.BigDecimal availableBalance,
                @Bind("ledgerBalance") java.math.BigDecimal ledgerBalance,
                @Bind("creditLimit") java.math.BigDecimal creditLimit,
                @Bind("status") String status);

    @SqlUpdate("UPDATE accounts SET status = :status, updated_at = CURRENT_TIMESTAMP WHERE account_id = :accountId")
    void updateStatus(@Bind("accountId") Long accountId,
                      @Bind("status") String status);
}
