## Digital Wallet, BNPL & More

> This is a practice project but can be upgraded to a full-fledged Digital Wallet just like **PayTm**, **GooglePay** or any other.
>
> > Anyone who wants to start a **STARTUP** with **Digital Wallet/BNPL/Credit Cards/Personal Loans** can have a look. This page itself describes what all will be required to be done.
>
> > Technically once you have bikt **LEDGER**, everything else is built as a layer on top of that

### Full requirements

### 1. All features with Extended Product Suite

- **Ledger & Wallet:** Core of the system - handles all transactions, balance checks, and history.
- **BNPL (Postpaid):** Credit Line (Postpaid) - One-click payments for selected merchants (similar exp. to UPI Lite).
- **Credit Card**: Another credit line
- **Credit Engine:** Custom scoring logic for BNPL limits.
- **Financial Tools:** Invoicing, Easy EMIs & Interest calculation, Waivers, and Debt Recovery/Collections, Lender Banks/NBFC settlements.
- **Marketing Engine:** Promotions/Campaigns, Coupons, Gift Cards (Advertisers), Rewards, Loyalty Points, and Discounts with Redemtion.
- **Value Adds:** Gift Cards, BBPS (Bill Payments) and FastTag
- **Mandates:** Auto-Debit via AutoPay like UPI mandate or e-NACH for subscriptions/EMIs.
- **Lending:** Personal Loans and EMI handling (involving Platform+Merchant+Creditor/NBFC+PaymentGateway+Customer) through integrated Lenders/Banks.
- **Integrations** Various merchants integrations like FastTag, Airtel, Hotstar, Airlines with discounts
- **Others** Mobile Recharges, Hotel/Flight/Cylinder Bookings, Movie Tickets, Insurance
- **Intl.** International payments, currencies conversions, Govt rules, Fees, Commissions
- **Advance Cases** Partial payment from Wallet and remaining fom credit line

> The biggest earnings comes from CREDIT lines (BNPL/Personal Loans) and various integrations with offers

### 2. Wallet Core

- **Ledger:** Immutable "Source of Truth" using append-only and read-only operations.
- **Important Features:**
  - Debit / Credit / Transfer operations.
  - Real-time Balance check.
  - Transaction History (Passbook).
    - Alternate Views
      - **P2P (Internal):** Instant money transfer between users within the wallet ecosystem.
      - **P2P (External):** Money transfer via Payment Gateways (PG) to external bank accounts.
      - **P2M (Merchant):** Purchases from registered merchants on the platform.
  - **QR Handling:** Generator for receiving payments and Scanner for initiating them.

### 3. Wallet Only Database Schema (Core Tables)

- `customers`: Primary user profiles.
- `customer_kyc_vault`: Secure storage for identity documents.
- `credit_assessments`: Scoring data and BNPL eligibility.
- `accounts`: Current balance snapshots for different account types (Wallet, Postpaid).
- `ledger_entries`: Double-entry bookkeeping records.
- `transactions`: Human-readable history with metadata.
- `intents`: Avoid double/dulicate requests - Idempotency

### 4. Identity & Risk Management

- **KYC Modules:**
  - **Tiers:** Min-KYC, Full-KYC (Aadhaar).
  - **Methods:** eKYC, CKYC, vKYC, DigiLocker integration.
  - **Verification & Credit Scoring:**
    - ML based ID parsing & verification (Aadhaar/PAN/Address).
    - Financial health checks via Bank Statements.
    - Bank statement parsing & Penny drop verification like Perfios/Karza
    - ML based Credit scoring engine along with CIBIL/Experian for **Postpaid & Loans**
  - **Fraud Checks:** Real-time velocity checks and anomaly detection.

### 5. Non-Functional Requirements (NFR)

- **Performance:** High DB/Transaction throughput with low API latency.
- **Integrity:** Atomic Transactions (ACID) and absolute Idempotency. You cannot have a "debit" succeed and a "credit" fail.
- **High Availability**: The system must be available 24/7, especially during peak Indian festival sales.
- **Security:** 2-Factor Authentication(2FA) & End-to-End encryption for all API calls.
- **Compliance** with **PCI DSS** (Card data) and **PII** (Personal data) protection.
- **Availability:** Scaling architecture with high availability and strong consistency.
- **Operations:** Comprehensive Monitoring, Metrics, and Logging.
- More....
  - **Data Localization:** Storing this data only in India.
  - **Aadhaar Masking:** You cannot store the full 12-digit Aadhaar number in plain text.
  - **Audit Logs:** Every time a staff member views a customer's KYC, you must log it.

### 6. Microservice Architecture

- **`customers`**: Handles User Profiles, Authentication, and Global `customer_id`.
- **`wallet`**: The **"Money Engine."** Orchestrates Wallet, BNPL, Ledger, and Transactions.
- **`kyc`**: Gateway for 3rd party integrations (Aadhaar, PAN, CIBIL) and document storage.
- **`notifications`**: Async alerts for SMS, WhatsApp, Push, and payment reminders.
- **`reconciliation`**: Batch jobs for auditing:
  - **Daily (AM/PM):** Verifies ledger net-zero status.
  - **Hourly:** Checks for stuck/pending transactions (>15 mins) to retry or fail.
- **`coupons`**: Coupons creation, distribution, management and redemption.
- **analytics**: Various data ingestion techniques like CDC, ETL pipelines etc to Warehouse/BigData Dump. Companies also use Smartlook or FullStory like third parties to record customer sesions on app.

##### Run commands via sbt shell

- runMain Main init-db
- runMain Main create-customer id name email phone
- runMain Main customers
- runMain Main create-account accountId customerId balance
- runMain Main credit accountId amount
- runMain Main debit accountId amount
- runMain Main transfer fromAccountId toAccountId amount
- runMain Main balance accountId
- runMain Main ledger
- runMain Main transactions
- runMain Main transactions-account accId

### Architectural Concerns for scale

1. Account Balances can be checked from a hot cache.
2. Intents are updated in sync and rest all (ledger, balances, transactions history) in async form in parallel. Transactin Mgmt can be difficult.
3. Sharding by user IDs
4. Partitioning for ledger_entries and transactions, for last 1-3 months only kept in hot DB, rest in cold DBs.
5. External settlements happen in T+2.
6. Reliable Transactions -
   - For each txn, start by locking UserID
   - Ledger and Balance updates happen in same transaction boundary
   - Deterministic Locking Order : For transfers and while locking always sort the userIds in ascending order first and then lock. This is needed for race conditions.
7. Fast Transactions - Use select for update and skip locked when appropriate
8. Big Intl. Banks use Oracle/Mssql/Sybase for a reason too. These works reliably even on peak load and do not just crash.
9. Async flows through Kafka/Some other Queues
10. Mechanisms/Apps needed to cover edge cases like PG partial success, Refunds, Adjustments etc with AAA security (Authentication, Authorization and Auditing).
11. Partial Indexing: Few tables like ledger_entries might become quite large. Prefer to use The Technique: CREATE INDEX idx_pending_txns ON ledger_entries (status) WHERE status = 'PENDING';

### Credit Scoring

1. Interesting problem - Most entrepreneurs initially learned these techniques from big investment banks and then started thr NBFC startups.
2. Scoring relies on CIBIL/Experian scroes alongside custom inhouse detailed AI/ML engine. Scoring is based typically on

- Transactional Data
  - Income patterns: Is the salary credited on the 1st or the 10th? Is it consistent?
  - Spending Velocity: Do you spend your whole salary in 5 days, or do you save?
  - Utility Behaviors: Do you pay electricity, gas, and postpaid bills on time? (This is a massive indicator of "Intent to Pay").
  - Bounced Cheques/Charges: Are there many "insufficient funds" penalty charges in the statement?
- Devices and App Metadata
  - Installed Apps: Do you have 3 different betting apps, or do you have 5 investment apps like Zerodha/Groww?
  - Device Type: An iPhone 15 Pro user is statistically a different credit risk than a 4-year-old budget Android user.
  - Location Stability: Does the device stay at a "Home" location at night and an "Office" location during the day? (Stability = Lower Risk).
- Social & Behavioral Signals
  - Typing Biometrics: Some advanced SDKs measure how you fill out the loan form. Do you copy-paste your name/address (suspicious)? Or do you type it out with a specific cadence?
  - Contact Analysis: (Privacy-sensitive, but used) Do you have contacts of people who have already defaulted on loans from the same company?
  - Email Scans: Looking for flight bookings, premium subscriptions, or e-commerce receipts.
- How the Math Handles 10,000 Variables
  - Feature Engineering: The Scala/Python backend extracts raw data (e.g., "SMS from HDFC received").
  - Vectorization: This is converted into a number (e.g., last_month_spend: 45000).
  - The Model: The model calculates a probability.
  - High spend on Zomato + iPhone User + Stable Office Location = 95% Approval.
  - Low balance + Late bill payments + Frequent small loans from other apps = 10% Approval.

### Why Scala

1. Scala is a wonderful and easy language. Best for business apps.
2. Most fancy FP promoters have unnecessarily damaged its reputation. Most FP is unnecessary in today's world.
3. If you are using pure functions anyway along side its powerful for cimprehension and at last its powerful error handling features like Try, Either, Option you have 80% power of FP
4. Code is much much smaller thn Java, pattern matching/case classes are very powerful. In fact from last 1 decade most features in JDK have been imported from SCALA only.
5. Its flexible type system and concise syntax gives you good domain modeling capability.

### Rules Implemented in Accounts, Transfer

### Account Rules

- Each customer has one or more Accounts.
- Supported account types:
  - WalletAccount
  - PostpaidAccount
  - LoanAccount
  - FastTag

#### 1.1 Balance Must Respect Account Policy

- WalletAccount
  - Balance cannot go below zero.
  - Balance cannot go below minimumBalance (if configured).
  - No overdraft allowed.
- PostpaidAccount
  - Balance may go negative.
  - Absolute negative value must not exceed creditLimit.
  - Credit limit is configurable per account.
    These rules are intrinsic to the account.
- They depend on:
  - Account type
  - Account configuration
  - Current balance

#### 1.2 Account State Restrictions

- AccountState values:
  - ACTIVE
  - INACTIVE
  - BLOCKED
  - DORMANT
- Rules:
  - ACTIVE → normal operations allowed.
  - BLOCKED → no debit allowed.
  - INACTIVE → no debit or credit allowed.
  - DORMANT → debit allowed but flagged (for prototype, treat as ACTIVE or restrict debit only — your choice).
- Optional prototype simplification:
  - BLOCKED / INACTIVE → no debit.
  - Credit allowed only if initiated by system.
- These rules belong to the account lifecycle.

#### 1.3 Transfer Limits

- For prototype:
  - Per-transaction cap (e.g., max ₹50,000).
  - Optional daily transfer limit (e.g., ₹1,00,000).
    - If limits depend only on account configuration → part of Account rules.
    - If limits depend on KYC tier or external risk systems → treat as application-layer rule.
    - For prototype: assume limits are account-configured.

#### 1.4 CanDebit / CanCredit Logic

- Account must decide:
  - Whether debit is allowed.
  - Whether credit is allowed.
  - What the resulting balance would be.
- Account must never allow a state that violates:
  - Minimum balance
  - Credit limit
  - Account state restrictions
  - Transfer limits
- Balance mutation must not be free-form.
-

### Transfer Rules (Wallet → Wallet)

- Sender account must be ACTIVE.
- Receiver account must be ACTIVE.
- Amount must be > 0.
- Sender must be allowed to debit that amount.
- Receiver must be allowed to credit that amount.
- Transfer is atomic.
- Transfer is irreversible.
- Duplicate transfer with same IntentId must not execute twice.
- If Intent already completed → return success.
- If Intent failed → may retry once (prototype).

### Ledger Rules

- Every transfer creates:
- One Debit entry.
- One Credit entry.
- Both entries must be written in the same DB transaction.
- Ledger is append-only.
- Ledger entries are immutable.
- Ledger entries must sum to zero per transaction.
- Ledger is the accounting source of truth.

### Intent Rules

- Intent lifecycle:
  - Created
    - → Validated → Posted → Completed
  - Failure path:
    - Created → Failed
- Rules:
  - Invalid transitions are not allowed.
  - Completed intents cannot transition to any other state.
  - Failed intents may retry once (prototype rule).
  - Intent must uniquely identify a transfer attempt.

### Balance Handling

- For prototype:
  - Balance is updated synchronously in same DB transaction as ledger.
  - Balance must reflect ledger state immediately.
  - Async projection can be added later.
