
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    external_customer_id VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(32),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS customer_kyc_vault (
    kyc_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    document_type VARCHAR(64) NOT NULL,
    document_reference VARCHAR(255) NOT NULL,
    vault_path VARCHAR(512) NOT NULL,
    verification_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_kyc_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS credit_assessments (
    assessment_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    score INT NOT NULL,
    eligibility BOOLEAN NOT NULL DEFAULT FALSE,
    assessed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assessed_by VARCHAR(128),
    details JSONB,
    CONSTRAINT fk_credit_assessments_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(64) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    available_balance NUMERIC(19,4) NOT NULL DEFAULT 0,
    ledger_balance NUMERIC(19,4) NOT NULL DEFAULT 0,
    credit_limit NUMERIC(19,4) NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    UNIQUE(customer_id, account_type)
);

CREATE TABLE IF NOT EXISTS ledger_entries (
    ledger_id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    related_account_id BIGINT,
    entry_type VARCHAR(32) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    entry_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(512),
    reference_id VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_entries_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_ledger_entries_related_account FOREIGN KEY (related_account_id) REFERENCES accounts(account_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    ledger_entry_id BIGINT,
    transaction_type VARCHAR(64) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    metadata JSONB,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(account_id),
    CONSTRAINT fk_transactions_ledger_entry FOREIGN KEY (ledger_entry_id) REFERENCES ledger_entries(ledger_id)
);

CREATE TABLE IF NOT EXISTS intents (
    intent_id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    customer_id BIGINT,
    operation_type VARCHAR(128) NOT NULL,
    request_payload_hash VARCHAR(128),
    state VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_intents_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
