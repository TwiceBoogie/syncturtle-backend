CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE time_period AS ENUM ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY');
CREATE TYPE user_status AS ENUM (
    'SUSPENDED',
    'INACTIVE',
    'ACTIVE',
    'ARCHIVED',
    'BLOCKED',
    'PENDING_DELETION',
    'LOCKED',
    'PENDING_USER_CONFIRMATION'
);
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE TABLE IF NOT EXISTS login_attempt_policy (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    policy_name VARCHAR(50) NOT NULL,
    max_attempts INT NOT NULL,
    lockout_duration INTERVAL,
    -- Duration for lockout after reaching max attempts
    reset_duration INTERVAL,
    -- Duration for resetting attempts (e.g., after successful login)
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
INSERT INTO login_attempt_policy (
        policy_name,
        max_attempts,
        lockout_duration,
        reset_duration
    )
VALUES ('Default Policy', 5, '5 minutes', '24 hours'),
    (
        'High Security Policy',
        3,
        '15 minutes',
        '48 hours'
    ),
    ('No Lockout Policy', 10, NULL, NULL);
-- user_status can be (Suspended, inactive, active, Archived, blocked, expired, pending_deletion, locked)
-- serial_number UUID DEFAULT gen_random_uuid(),
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(255) UNIQUE NOT NULL,
    about VARCHAR(255),
    first_name VARCHAR(36) NOT NULL,
    last_name VARCHAR(36) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    birthday VARCHAR(255),
    country VARCHAR(255),
    country_code VARCHAR(255),
    phone INT8,
    gender VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE,
    role user_role DEFAULT 'USER',
    user_status user_status DEFAULT 'ACTIVE',
    notification_count int8 DEFAULT 0,
    login_attempt_policy BIGINT NOT NULL,
    created_by VARCHAR(36),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(36),
    modified_date TIMESTAMP,
    FOREIGN KEY (login_attempt_policy) REFERENCES login_attempt_policy(id),
    PRIMARY KEY (id)
);
ALTER TABLE users
ADD CONSTRAINT unique_email_username UNIQUE (email, username);
CREATE INDEX idx_username ON users (username);
CREATE INDEX trgm_index ON users USING gin (username gin_trgm_ops);
CREATE INDEX trgm_email ON users USING gin (email gin_trgm_ops);
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    is_chosen BOOLEAN DEFAULT FALSE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_profile_limit (
    user_id BIGINT NOT NULL,
    profile_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (user_id)
);
CREATE TABLE IF NOT EXISTS activation_codes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code_type VARCHAR(20) NOT NULL,
    -- 'activation' or 'device_verification'
    hashed_code VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT,
    reset_count INT DEFAULT 0,
    created_by VARCHAR(36),
    created_date TIMESTAMP NOT NULL,
    modified_by VARCHAR(36),
    modified_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS password_reset_otp (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    hashed_otp VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    token VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_devices (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    device_key VARCHAR(255) NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    first_access_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    --     device_information TEXT, not sure about this one
    -- additional_metadata JSONB,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
-- CREATE INDEX trgm_index ON user_devices USING gin (user_id, device_info);
-- CREATE TABLE IF NOT EXISTS device_info (
--     id BIGINT GENERATED ALWAYS AS IDENTITY,
--     user_device_id BIGINT NOT NULL,
--     device_name VARCHAR(255) NOT NULL,
--     device_type VARCHAR(50) NOT NULL,
--     last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     location VARCHAR(255),
--     FOREIGN KEY (user_device_id) REFERENCES user_devices(id),
--     PRIMARY KEY (id)
-- ); won't need it
-- will only be created for important actions like password change/deletion
CREATE TABLE user_actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_status VARCHAR(50) NOT NULL DEFAULT 'pending',
    verification_code VARCHAR(64),
    user_device_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (user_device_id) REFERENCES user_devices(id)
);
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    attempt_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(50),
    -- IPv4 or IPv6 address
    is_new_device BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS recovery_attempts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    attempt_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(50),
    -- IPv4 or IPv6 address
    recovery_type VARCHAR(50),
    -- e.g., 'username', 'password'
    is_new_device BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS locked_users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    lockout_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lockout_end TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    lockout_reason VARCHAR(255) NOT NULL,
    is_requested_by_user BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    token_type VARCHAR(25),
    expiration_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
-- Statistics
--User Statistics:
--    Track the number of active users and registered users on a daily basis.
--    Calculate the percentage increase or decrease in registered users compared to the previous day.
CREATE TABLE IF NOT EXISTS user_statistics (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    interval_type time_period NOT NULL,
    active_user_count INT NOT NULL,
    registered_users INT NOT NULL,
    registered_users_change DECIMAL(5, 2),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);