-- Seed data: default templates for wallet microservice
-- WELCOME Template
MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WELCOME', 'EMAIL', 'Welcome to Web Wallet!', 'Hello {{username}}, welcome to Web Wallet! Your account has been successfully created.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WELCOME', 'SMS', NULL, 'Welcome to Web Wallet! Your account has been created.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WELCOME', 'IN_APP', 'Welcome', 'Welcome! Your web wallet is active. Explore our features.');

-- WALLET_CREDITED Template
MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_CREDITED', 'EMAIL', 'Alert: Wallet Credited', 'Hello {{username}}, your wallet has been credited with {{amount}} by {{senderName}}. New balance: {{balance}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_CREDITED', 'SMS', NULL, 'Wallet credited with {{amount}} by {{senderName}}. New balance: {{balance}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_CREDITED', 'IN_APP', 'Account Credited', 'Your wallet has been credited with {{amount}} by {{senderName}}.');

-- WALLET_DEBITED Template
MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_DEBITED', 'EMAIL', 'Alert: Wallet Debited', 'Hello {{username}}, your wallet has been debited by {{amount}} for {{description}}. New balance: {{balance}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_DEBITED', 'SMS', NULL, 'Wallet debited by {{amount}} for {{description}}. New balance: {{balance}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('WALLET_DEBITED', 'IN_APP', 'Account Debited', 'Wallet debited by {{amount}} for {{description}}.');

-- TRANSACTION_FAILED Template
MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('TRANSACTION_FAILED', 'EMAIL', 'Alert: Transaction Failed', 'Dear {{username}}, transaction of {{amount}} to {{recipient}} failed. Reason: {{reason}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('TRANSACTION_FAILED', 'SMS', NULL, 'Transaction of {{amount}} to {{recipient}} failed. Reason: {{reason}}.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('TRANSACTION_FAILED', 'IN_APP', 'Transaction Failed', 'Transaction of {{amount}} to {{recipient}} failed: {{reason}}.');

-- OTP_VERIFICATION Template
MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('OTP_VERIFICATION', 'EMAIL', 'One-Time Password (OTP) Verification', 'Your verification OTP is {{otp}}. This code is valid for 5 minutes.');

MERGE INTO notification_templates (template_key, channel, subject_template, body_template)
KEY(template_key, channel)
VALUES ('OTP_VERIFICATION', 'SMS', NULL, 'Your Web Wallet verification code is {{otp}}.');
