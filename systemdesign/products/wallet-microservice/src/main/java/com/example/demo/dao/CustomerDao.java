package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.example.demo.model.Customer;

public interface CustomerDao {

    @SqlUpdate("INSERT INTO customers (external_customer_id, email, phone_number, first_name, last_name, status) VALUES (:externalCustomerId, :email, :phoneNumber, :firstName, :lastName, :status)")
    @org.jdbi.v3.sqlobject.statement.GetGeneratedKeys("customer_id")
    Long insert(@Bind("externalCustomerId") String externalCustomerId,
                @Bind("email") String email,
                @Bind("phoneNumber") String phoneNumber,
                @Bind("firstName") String firstName,
                @Bind("lastName") String lastName,
                @Bind("status") String status);

    @SqlUpdate("UPDATE customers SET email = :email, phone_number = :phoneNumber, first_name = :firstName, last_name = :lastName, updated_at = CURRENT_TIMESTAMP WHERE customer_id = :customerId")
    void update(@Bind("customerId") Long customerId,
                @Bind("email") String email,
                @Bind("phoneNumber") String phoneNumber,
                @Bind("firstName") String firstName,
                @Bind("lastName") String lastName);

    @SqlUpdate("UPDATE customers SET status = :status, updated_at = CURRENT_TIMESTAMP WHERE customer_id = :customerId")
    void updateStatus(@Bind("customerId") Long customerId,
                      @Bind("status") String status);

    @SqlQuery("SELECT * FROM customers WHERE customer_id = :customerId")
    Optional<Customer> findById(@Bind("customerId") Long customerId);

    @SqlQuery("SELECT * FROM customers WHERE external_customer_id = :externalCustomerId")
    Optional<Customer> findByExternalId(@Bind("externalCustomerId") String externalCustomerId);

    @SqlQuery("SELECT * FROM customers")
    List<Customer> findAll();
}
