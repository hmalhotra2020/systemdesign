package com.example.demo.dao;

import java.util.Optional;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.example.demo.model.IdempotencyIntent;

public interface IntentDao {

    @SqlQuery("SELECT * FROM intents WHERE idempotency_key = :key")
    Optional<IdempotencyIntent> findByKey(@Bind("key") String key);

    @SqlUpdate("INSERT INTO intents (idempotency_key, customer_id, operation_type, request_payload_hash, state, created_at, updated_at) VALUES (:key, :customerId, :operationType, :payloadHash, :state, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @GetGeneratedKeys("intent_id")
    Long insert(@Bind("key") String key,
                @Bind("customerId") Long customerId,
                @Bind("operationType") String operationType,
                @Bind("payloadHash") String payloadHash,
                @Bind("state") String state);

    @SqlUpdate("UPDATE intents SET state = :state, updated_at = CURRENT_TIMESTAMP WHERE intent_id = :intentId")
    void updateState(@Bind("intentId") Long intentId,
                     @Bind("state") String state);
}
