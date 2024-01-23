CREATE TYPE domain_status AS ENUM ('EXPIRED', 'SOON', 'ACTIVE', 'DELETED');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
-- CREATE UNIQUE INDEX idx_unique_account_domain ON keychain (account_id, domain);
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    user_status VARCHAR(36) NOT NULL,
    role user_role DEFAULT 'USER' NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS keychain (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    encrypted_password BYTEA NOT NULL,
    username VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    notes TEXT,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    update_date DATE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
ALTER TABLE keychain
ADD CONSTRAINT unique_account_domain UNIQUE (account_id, domain);
CREATE INDEX idx_expiry_combined ON keychain (expiry_timestamp, expiry_notification_sent);

CREATE TABLE IF NOT EXISTS encryption_key (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    vector BYTEA NOT NULL,
    dek VARCHAR(255) NOT NULL,
    keychain_id BIGINT NOT NULL,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    PRIMARY KEY (id)
);
ALTER TABLE encryption_key
ADD CONSTRAINT unique_vector_dek UNIQUE (vector, dek);

-- Metrics related to password complexity
CREATE TABLE IF NOT EXISTS password_complexity_metrics (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id BIGINT NOT NULL,
    password_length INT NOT NULL,
    character_types_used INT NOT NULL,
    entropy DOUBLE PRECISION NOT NULL,
    check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    PRIMARY KEY (id)
);
-- Password reuse statistics
CREATE TABLE IF NOT EXISTS password_reuse_statistics (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    reuse_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    PRIMARY KEY (id)
);

-- CREATE TABLE IF NOT EXISTS user_device_info (
--     id BIGINT GENERATED ALWAYS AS IDENTITY,
--     account_id BIGINT NOT NULL,
--     device_info VARCHAR(255) NOT NULL,
--     ip_address VARCHAR(15), -- IPv4 or IPv6 address
--     action_type VARCHAR(50) NOT NULL, -- e.g., 'CREATE_PASSWORD', 'PASSWORD_CHANGE', etc.
--     action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (account_id) REFERENCES accounts(id),
--     PRIMARY KEY (id)
-- );

-- Password change logs
CREATE TABLE IF NOT EXISTS password_change_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id BIGINT NOT NULL,
    changed_by_user user_role DEFAULT 'ADMIN' NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    PRIMARY KEY (id)
);
-- Password expiry notifications
-- CREATE TABLE IF NOT EXISTS password_expiry_notifications (
--     id BIGINT GENERATED ALWAYS AS IDENTITY,
--     account_id BIGINT NOT NULL,
--     notification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (account_id) REFERENCES accounts(id),
--     PRIMARY KEY (id)
-- );

-- CREATE TABLE IF NOT EXISTS password_import_export_history (
--     id BIGINT GENERATED ALWAYS AS IDENTITY,
--     account_id BIGINT NOT NULL,
--     operation_type VARCHAR(20) NOT NULL, -- 'IMPORT' or 'EXPORT'
--     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (account_id) REFERENCES accounts(id),
--     PRIMARY KEY (id)
-- );

-- 9. Password Expiry Policies
CREATE TABLE IF NOT EXISTS password_expiry_policies (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    policy_name VARCHAR(50) NOT NULL,
    max_expiry_days INT,
    notification_days INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

INSERT INTO password_expiry_policies (policy_name, max_expiry_days, notification_days)
VALUES ("Default", 90, 30);

-- 10. User Password Expiry Settings
CREATE TABLE IF NOT EXISTS user_password_expiry_settings (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    expiry_notification_sent BOOLEAN DEFAULT FALSE,
    expiry_timestamp Date NOT NULL,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    FOREIGN KEY (policy_id) REFERENCES password_expiry_policies(id),
    PRIMARY KEY (id)
);

-- 11. Password Security Questions
-- CREATE TABLE IF NOT EXISTS password_security_questions (
--     id BIGINT GENERATED ALWAYS AS IDENTITY,
--     account_id BIGINT NOT NULL,
--     question_text VARCHAR(255) NOT NULL,
--     answer_hash VARCHAR(255) NOT NULL,
--     FOREIGN KEY (account_id) REFERENCES accounts(id),
--     PRIMARY KEY (id)
-- );