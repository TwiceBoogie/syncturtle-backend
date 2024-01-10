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
CREATE TABLE IF NOT EXISTS encryption_key (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    vector BYTEA NOT NULL,
    dek VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
ALTER TABLE encryption_key
ADD CONSTRAINT unique_vector_dek UNIQUE (vector, dek);
CREATE TABLE IF NOT EXISTS keychain (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    encrypted_password BYTEA NOT NULL,
    domain VARCHAR(255) NOT NULL,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    update_date DATE NOT NULL,
    encryption_key_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (encryption_key_id) REFERENCES encryption_key(id)
);
ALTER TABLE keychain
ADD CONSTRAINT unique_account_domain UNIQUE (account_id, domain);

-- Statistics
-- Password history for each account
CREATE TABLE IF NOT EXISTS password_history (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    domain VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Metrics related to password complexity
CREATE TABLE IF NOT EXISTS password_complexity_metrics (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    password_length INT NOT NULL,
    character_types_used INT NOT NULL,
    entropy FLOAT NOT NULL,
    check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Password reuse statistics
CREATE TABLE IF NOT EXISTS password_reuse_statistics (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    reuse_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Password change logs
CREATE TABLE IF NOT EXISTS password_change_logs (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    changed_by_user_id BIGINT NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    device_info VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (changed_by_user_id) REFERENCES accounts(id)
);

-- Password expiry notifications
CREATE TABLE IF NOT EXISTS password_expiry_notifications (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    notification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Password policy violations
CREATE TABLE IF NOT EXISTS password_policy_violations (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    violation_reason TEXT,
    violation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Password reset requests
CREATE TABLE IF NOT EXISTS password_reset_requests (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
