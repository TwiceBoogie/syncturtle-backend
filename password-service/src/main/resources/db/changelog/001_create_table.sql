CREATE TYPE domain_status AS ENUM ('EXPIRED', 'SOON', 'ACTIVE', 'DELETED');
CREATE TYPE time_period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- CREATE UNIQUE INDEX idx_unique_account_domain ON keychain (account_id, domain);
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    user_status VARCHAR(36) NOT NULL,
    role user_role DEFAULT 'USER' NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS password_expiry_policies (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    policy_name VARCHAR(50) NOT NULL,
    max_expiry_days INT,
    notification_days INT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

INSERT INTO password_expiry_policies (policy_name, max_expiry_days, notification_days)
VALUES ('Default', 90, 30);

CREATE TABLE IF NOT EXISTS keychain (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    encrypted_password BYTEA NOT NULL,
    username VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    notes TEXT,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    policy_id BIGINT NOT NULL,
    notification_sent BOOLEAN DEFAULT FALSE,
    expiry_date Date NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (policy_id) REFERENCES password_expiry_policies(id),
    PRIMARY KEY (id)
);
ALTER TABLE keychain
ADD CONSTRAINT unique_account_domain UNIQUE (account_id, domain);
CREATE INDEX idx_status ON keychain (status);
CREATE INDEX idx_expiry_combined ON keychain (expiry_date, notification_sent);
CREATE INDEX trgm_index ON keychain USING gin (domain gin_trgm_ops);

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

-- Password change logs
CREATE TABLE IF NOT EXISTS password_change_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id BIGINT NOT NULL,
    changed_by_user user_role DEFAULT 'ADMIN' NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    PRIMARY KEY (id)
);

-- Password statistics
-- This aggregates data and shows admin which policy_id is more secure.
CREATE TABLE IF NOT EXISTS password_update_stats (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    policy_id BIGINT NOT NULL,
    avg_update_count INT NOT NULL DEFAULT 0,
    avg_update_interval INTERVAL NOT NULL,
    interval_type time_period,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- WORKS
-- CREATE OR REPLACE FUNCTION insert_update_stats(
--        p_time_period time_period
-- ) RETURNS VOID AS $$
-- BEGIN
-- INSERT INTO password_update_stats (policy_id, avg_update_count, avg_update_interval, interval_type)
-- WITH ChangeIntervals AS (
--     SELECT
--         k.policy_id,
--         k.id AS keychain_id,
--         LAG(pcls.change_date) OVER (PARTITION BY k.id ORDER BY pcls.change_date) AS previous_change_date,
--             pcls.change_date AS current_change_date
--     FROM
--         keychain k
--     JOIN
--         password_change_logs pcls ON k.id = pcls.keychain_id
--     WHERE pcls.change_date >= CASE
--                                   WHEN p_time_period = 'DAILY' THEN CURRENT_TIMESTAMP - INTERVAL '1 day'
--                                   WHEN p_time_period = 'WEEKLY' THEN CURRENT_TIMESTAMP - INTERVAL '7 days'
--                                   WHEN p_time_period = 'MONTHLY' THEN CURRENT_TIMESTAMP - INTERVAL '1 month'
--                                   WHEN p_time_period = 'YEARLY' THEN CURRENT_TIMESTAMP - INTERVAL '1 year'
--                                   ELSE CURRENT_TIMESTAMP - INTERVAL '1 day'
--                               END CASE;
-- )
--
-- SELECT
--     subquery.policy_id,
--     AVG(subquery.change_log_count)::DOUBLE PRECISION AS avg_update_count,
--         COALESCE(
--             INTERVAL '1 day' * EXTRACT(DAY FROM AVG(current_change_date - previous_change_date))::INT,
--             INTERVAL '0 days'
--         ) AS avg_update_interval,
--         p_time_period AS interval_type
-- FROM (
--     SELECT
--         p.id AS policy_id,
--         COUNT(cl.id) AS change_log_count
--     FROM
--         password_expiry_policies p
--     LEFT JOIN
--         keychain k ON p.id = k.policy_id
--     LEFT JOIN
--         password_change_logs cl ON k.id = cl.keychain_id
--     GROUP BY
--         p.id
--     ) subquery
-- LEFT JOIN
--     ChangeIntervals ci ON subquery.policy_id = ci.policy_id
-- GROUP BY
--     subquery.policy_id
-- ORDER BY
--     policy_id;
-- END;
-- $$ LANGUAGE plpgsql;
