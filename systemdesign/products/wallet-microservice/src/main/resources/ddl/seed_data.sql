-- Seed data script for Wallet Application
-- Truncates existing tables and generates 20 Indian customers, their KYC, WALLET accounts, and ~55 transactions.

TRUNCATE TABLE intents CASCADE;
TRUNCATE TABLE transactions CASCADE;
TRUNCATE TABLE ledger_entries CASCADE;
TRUNCATE TABLE accounts CASCADE;
TRUNCATE TABLE credit_assessments CASCADE;
TRUNCATE TABLE customer_kyc_vault CASCADE;
TRUNCATE TABLE customers CASCADE;

-- Reset identity sequences for all tables
ALTER SEQUENCE customers_customer_id_seq RESTART WITH 2;
ALTER SEQUENCE customer_kyc_vault_kyc_id_seq RESTART WITH 1;
ALTER SEQUENCE credit_assessments_assessment_id_seq RESTART WITH 1;
ALTER SEQUENCE accounts_account_id_seq RESTART WITH 2;
ALTER SEQUENCE ledger_entries_ledger_id_seq RESTART WITH 2;
ALTER SEQUENCE transactions_transaction_id_seq RESTART WITH 1;
ALTER SEQUENCE intents_intent_id_seq RESTART WITH 1;

-- Ensure system customer (ID will be 1)
INSERT INTO customers (customer_id, external_customer_id, email, phone_number, first_name, last_name, status, created_at, updated_at)
VALUES (1, 'system.customer', 'system@localhost', '0000', 'System', 'Account', 'ACTIVE', NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 months');

-- Ensure system account (ID will be 1)
INSERT INTO accounts (account_id, customer_id, account_type, currency, available_balance, ledger_balance, credit_limit, status, created_at, updated_at)
VALUES (1, 1, 'SYSTEM', 'INR', 1000000000000.00, 1000000000000.00, 0, 'OPEN', NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 months');

-- Ensure system account initial ledger balance entry exists so it is consistent
INSERT INTO ledger_entries (ledger_id, account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
VALUES (1, 1, null, 'CREDIT', 1000000000000.00, 'INR', NOW() - INTERVAL '3 months', 'System Initial Funding', 'sys-init', NOW() - INTERVAL '3 months');

DO $$
DECLARE
    first_names text[] := ARRAY['Aarav', 'Vihaan', 'Aditya', 'Arjun', 'Sai', 'Ishaan', 'Ananya', 'Diya', 'Rhea', 'Aanya', 'Priya', 'Amit', 'Rajesh', 'Sunita', 'Rahul', 'Sanjay', 'Neha', 'Pooja', 'Kiran', 'Vikram'];
    last_names text[] := ARRAY['Sharma', 'Patel', 'Iyer', 'Verma', 'Reddy', 'Gupta', 'Rao', 'Nair', 'Sen', 'Joshi', 'Mishra', 'Kumar', 'Singh', 'Prasad', 'Dutt', 'Sethi', 'Hegde', 'Malhotra', 'Deshmukh', 'Pillai'];
    c_id bigint;
    a_id bigint;
    cust_idx int;
    txn_count int;
    txn_date timestamp;
    sender_cust_idx int;
    receiver_cust_idx int;
    sender_acc_id bigint;
    receiver_acc_id bigint;
    txn_amount numeric(19,4);
    ref_id varchar(128);
    ledger_deb_id bigint;
    ledger_cred_id bigint;
    tx_type varchar(64);
    tx_status varchar(32);
    sys_acc_id bigint := 1; -- System account ID is 1
BEGIN
    -- 1. Insert 20 customers (IDs will be 2 to 21)
    FOR i IN 1..20 LOOP
        INSERT INTO customers (external_customer_id, email, phone_number, first_name, last_name, status, created_at, updated_at)
        VALUES (
            'cust-' || i,
            lower(first_names[i]) || '.' || lower(last_names[i]) || i || '@example.com',
            '98765' || lpad(i::text, 5, '0'),
            first_names[i],
            last_names[i],
            'ACTIVE',
            NOW() - INTERVAL '3 months',
            NOW() - INTERVAL '3 months'
        )
        RETURNING customer_id INTO c_id;

        -- Create KYC
        INSERT INTO customer_kyc_vault (customer_id, document_type, document_reference, vault_path, verification_status, created_at, updated_at)
        VALUES (
            c_id,
            CASE WHEN i % 2 = 0 THEN 'PAN' ELSE 'AADHAAR' END,
            'REF' || (100000 + i),
            '/vault/kyc/' || c_id || '/doc.pdf',
            'VERIFIED',
            NOW() - INTERVAL '3 months',
            NOW() - INTERVAL '3 months'
        );

        -- Create Wallet Account with 10,000 INR starting balance
        INSERT INTO accounts (customer_id, account_type, currency, available_balance, ledger_balance, credit_limit, status, created_at, updated_at)
        VALUES (
            c_id,
            'WALLET',
            'INR',
            10000.00,
            10000.00,
            0,
            'OPEN',
            NOW() - INTERVAL '3 months',
            NOW() - INTERVAL '3 months'
        )
        RETURNING account_id INTO a_id;

        -- Create corresponding ledger entry for the initial credit (₹10,000)
        INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
        VALUES (a_id, sys_acc_id, 'CREDIT', 10000.00, 'INR', NOW() - INTERVAL '3 months', 'Initial Wallet Balance', 'init-' || c_id, NOW() - INTERVAL '3 months')
        RETURNING ledger_id INTO ledger_cred_id;

        -- Create corresponding transaction record
        INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, occurred_at, created_at)
        VALUES (c_id, a_id, ledger_cred_id, 'DEPOSIT', 10000.00, 'INR', 'COMPLETED', NOW() - INTERVAL '3 months', NOW() - INTERVAL '3 months');
    END LOOP;

    -- 2. Generate exactly 55 transactions spread over the last 3 months
    FOR i IN 1..55 LOOP
        -- Generate date between 90 days ago and today
        txn_date := NOW() - (random() * 90) * INTERVAL '1 day';
        
        -- Decide transaction type: CREDIT (20%), DEBIT (20%), TRANSFER (60%)
        IF random() < 0.20 THEN
            -- CREDIT (Deposit from system/external)
            cust_idx := 2 + floor(random() * 20)::int; -- customer_id between 2 and 21
            SELECT account_id INTO receiver_acc_id FROM accounts WHERE customer_id = cust_idx AND account_type = 'WALLET';
            txn_amount := round((500 + random() * 4500)::numeric, 2); -- 500 to 5000 INR
            ref_id := 'dep-' || i || '-' || floor(random()*10000);

            -- Credit to customer account
            INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
            VALUES (receiver_acc_id, sys_acc_id, 'CREDIT', txn_amount, 'INR', txn_date, 'UPI Deposit', ref_id, txn_date)
            RETURNING ledger_id INTO ledger_cred_id;

            -- Update customer account balance
            UPDATE accounts 
            SET available_balance = available_balance + txn_amount,
                ledger_balance = ledger_balance + txn_amount,
                updated_at = txn_date
            WHERE account_id = receiver_acc_id;

            -- Insert transaction record
            INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, occurred_at, created_at)
            VALUES (cust_idx, receiver_acc_id, ledger_cred_id, 'DEPOSIT', txn_amount, 'INR', 'COMPLETED', txn_date, txn_date);

        ELSIF random() < 0.40 THEN
            -- DEBIT (Withdrawal/Payment to external/system)
            cust_idx := 2 + floor(random() * 20)::int;
            SELECT account_id INTO sender_acc_id FROM accounts WHERE customer_id = cust_idx AND account_type = 'WALLET';
            txn_amount := round((100 + random() * 2000)::numeric, 2); -- 100 to 2100 INR
            ref_id := 'wth-' || i || '-' || floor(random()*10000);

            -- Debit from customer account
            INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
            VALUES (sender_acc_id, sys_acc_id, 'DEBIT', txn_amount, 'INR', txn_date, 'Merchant Payment', ref_id, txn_date)
            RETURNING ledger_id INTO ledger_deb_id;

            -- Update customer account balance
            UPDATE accounts 
            SET available_balance = available_balance - txn_amount,
                ledger_balance = ledger_balance - txn_amount,
                updated_at = txn_date
            WHERE account_id = sender_acc_id;

            -- Insert transaction record
            tx_status := 'COMPLETED';
            IF random() < 0.05 THEN
                tx_status := 'PENDING';
            ELSIF random() < 0.05 THEN
                tx_status := 'FAILED';
            END IF;

            INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, occurred_at, created_at)
            VALUES (cust_idx, sender_acc_id, ledger_deb_id, 'PAYMENT', txn_amount, 'INR', tx_status, txn_date, txn_date);

        ELSE
            -- TRANSFER (Between two random customers)
            sender_cust_idx := 2 + floor(random() * 20)::int;
            receiver_cust_idx := 2 + floor(random() * 20)::int;
            WHILE sender_cust_idx = receiver_cust_idx LOOP
                receiver_cust_idx := 2 + floor(random() * 20)::int;
            END LOOP;

            SELECT account_id INTO sender_acc_id FROM accounts WHERE customer_id = sender_cust_idx AND account_type = 'WALLET';
            SELECT account_id INTO receiver_acc_id FROM accounts WHERE customer_id = receiver_cust_idx AND account_type = 'WALLET';
            txn_amount := round((200 + random() * 3000)::numeric, 2); -- 200 to 3000 INR
            ref_id := 'trf-' || i || '-' || floor(random()*10000);

            -- Debit sender
            INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
            VALUES (sender_acc_id, receiver_acc_id, 'DEBIT', txn_amount, 'INR', txn_date, 'Transfer to Wallet', ref_id, txn_date)
            RETURNING ledger_id INTO ledger_deb_id;

            -- Credit receiver
            INSERT INTO ledger_entries (account_id, related_account_id, entry_type, amount, currency, entry_date, description, reference_id, created_at)
            VALUES (receiver_acc_id, sender_acc_id, 'CREDIT', txn_amount, 'INR', txn_date, 'Transfer from Wallet', ref_id, txn_date)
            RETURNING ledger_id INTO ledger_cred_id;

            -- Update balances
            UPDATE accounts 
            SET available_balance = available_balance - txn_amount,
                ledger_balance = ledger_balance - txn_amount,
                updated_at = txn_date
            WHERE account_id = sender_acc_id;

            UPDATE accounts 
            SET available_balance = available_balance + txn_amount,
                ledger_balance = ledger_balance + txn_amount,
                updated_at = txn_date
            WHERE account_id = receiver_acc_id;

            tx_status := 'COMPLETED';
            IF random() < 0.05 THEN
                tx_status := 'PENDING';
            END IF;

            -- Insert transaction records for both customers
            INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, occurred_at, created_at)
            VALUES (sender_cust_idx, sender_acc_id, ledger_deb_id, 'TRANSFER', txn_amount, 'INR', tx_status, txn_date, txn_date);

            INSERT INTO transactions (customer_id, account_id, ledger_entry_id, transaction_type, amount, currency, status, occurred_at, created_at)
            VALUES (receiver_cust_idx, receiver_acc_id, ledger_cred_id, 'TRANSFER', txn_amount, 'INR', tx_status, txn_date, txn_date);

            -- Create intent to match the transfer
            INSERT INTO intents (idempotency_key, customer_id, operation_type, request_payload_hash, state, created_at, updated_at)
            VALUES (ref_id, sender_cust_idx, 'TRANSFER', 'hash-' || ref_id, CASE WHEN tx_status = 'COMPLETED' THEN 'COMPLETED' ELSE 'PENDING' END, txn_date, txn_date);
        END IF;
    END LOOP;
END $$;
