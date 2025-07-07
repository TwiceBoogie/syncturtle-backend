CREATE TYPE domain_status AS ENUM ('EXPIRED', 'SOON', 'ACTIVE', 'DELETED');
CREATE TYPE time_period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE user_status AS ENUM ('SUSPENDED', 'INACTIVE', 'ACTIVE', 'ARCHIVED', 'BLOCKED', 'PENDING_DELETION', 'LOCKED', 'PENDING_USER_CONFIRMATION');
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- CREATE UNIQUE INDEX idx_unique_account_domain ON keychain (user_id, domain);
CREATE TABLE IF NOT EXISTS users (
    id uuid NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_status VARCHAR(36) NOT NULL,
    role user_role DEFAULT 'USER' NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rotation_policies (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    policy_name VARCHAR(50) NOT NULL,
    max_rotation_days INT,
    notification_days INT,
    entity_type VARCHAR(20) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

INSERT INTO rotation_policies (policy_name, max_rotation_days, notification_days, entity_type)
VALUES ('Default', 90, 30, 'encryption_key');

CREATE TABLE IF NOT EXISTS encryption_key (
    id uuid NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    dek VARCHAR(255) NOT NULL,
    user_id uuid NOT NULL,
    algorithm VARCHAR(50),
    key_size INT,
    expiration_date DATE,
    is_enabled BOOLEAN DEFAULT TRUE,
    active_since TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usage_count BIGINT DEFAULT 0,
    rotation_policy_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (rotation_policy_id) REFERENCES rotation_policies(id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS keychain (
    id uuid NOT NULL,
    username VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    website_url VARCHAR(255) NOT NULL,
    favorite BOOLEAN DEFAULT FALSE,
    notes TEXT,
    status VARCHAR(255) DEFAULT 'ACTIVE',
    notification_sent BOOLEAN DEFAULT FALSE,
    expiry_date Date NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    encrypted_password BYTEA NOT NULL,
    vector BYTEA NOT NULL,
    dek_id uuid NOT NULL,
    rotation_policy_id BIGINT NOT NULL,
    FOREIGN KEY (dek_id) REFERENCES encryption_key(id),
    FOREIGN KEY (rotation_policy_id) REFERENCES rotation_policies(id),
    PRIMARY KEY (id)
);
ALTER TABLE keychain
ADD CONSTRAINT unique_account_domain UNIQUE (dek_id, domain);
CREATE INDEX idx_status ON keychain (status);
CREATE INDEX idx_expiry_combined ON keychain (expiry_date, notification_sent);
CREATE INDEX trgm_index ON keychain USING gin (domain gin_trgm_ops);

-- Metrics related to password complexity
CREATE TABLE IF NOT EXISTS password_complexity_metrics (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id uuid NOT NULL,
    password_length INT NOT NULL,
    character_types_used INT NOT NULL,
    dictionary_word_count INT NOT NULL,
    numeric_characters_count INT NOT NULL,
    special_characters_count INT NOT NULL,
    uppercase_letters_count INT NOT NULL,
    lowercase_letters_count INT NOT NULL,
    sequential_characters_count INT NOT NULL,
    repeating_characters_count INT NOT NULL,
    password_complexity_score DOUBLE PRECISION,
    password_composition JSONB,
    password_pattern_analysis TEXT,
    common_substrings_analysis TEXT,
    check_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id),
    PRIMARY KEY (id)
);
-- Password reuse statistics
CREATE TABLE IF NOT EXISTS password_reuse_statistics (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    reuse_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);

-- Password change logs
CREATE TABLE IF NOT EXISTS password_change_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    keychain_id uuid NOT NULL,
    changed_by_user user_role DEFAULT 'ADMIN' NOT NULL,
    change_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_reason TEXT,
    change_type VARCHAR(50), -- type of change (routine change, forced)
    change_success BOOLEAN,
    change_result TEXT,
    user_device_id uuid NOT NULL,
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

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
ALTER TABLE categories
ADD CONSTRAINT unique_name_color UNIQUE (name, color);

CREATE TABLE IF NOT EXISTS keychain_categories (
    keychain_id uuid NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (keychain_id) REFERENCES keychain(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (keychain_id, category_id)
);

INSERT INTO categories (name, description, color)
VALUES
    ('Social Media', 'Websites and apps for social networking and communication', '#3b5998'), -- Facebook Blue
    ('Email', 'Email services and clients', '#0077b5'), -- LinkedIn Blue
    ('Finance', 'Financial institutions and services', '#009688'), -- Material Design Teal
    ('Entertainment', 'Streaming services, gaming platforms, and other entertainment sources', '#ff5722'), -- Material Design Deep Orange
    ('Shopping', 'Online shopping websites and marketplaces', '#e91e63'), -- Material Design Pink
    ('Work', 'Professional tools and services for work-related tasks', '#4caf50'), -- Material Design Green
    ('Health', 'Healthcare-related websites and apps', '#2196f3'), -- Material Design Blue
    ('Education', 'Educational resources and platforms', '#673ab7'), -- Material Design Deep Purple
    ('News', 'News websites and sources', '#ff9800'); -- Material Design Orange


-- WORKS
-- This function calculates and inserts statistics about password update intervals into the password_update_stats table
-- It accepts a parameter p_time_period to determine the time period for calculating the statistics (daily, weekly, monthly, or yearly)
-- CREATE OR REPLACE FUNCTION insert_update_stats(
--        p_time_period time_period
-- ) RETURNS VOID AS $$
-- BEGIN
-- Calculate the intervals between consecutive password changes for each keychain within the specified time period
-- INSERT INTO password_update_stats (policy_id, avg_update_count, avg_update_interval, interval_type)
-- WITH ChangeIntervals AS ( -- ChangeIntervals = CTE (Common Table Expression)
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
--                               END
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
