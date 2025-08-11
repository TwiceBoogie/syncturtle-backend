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
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    id UUID PRIMARY KEY,
    username VARCHAR(128) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(36) NOT NULL,
    last_name VARCHAR(36) NOT NULL,
    password VARCHAR(255),
    is_password_autoset BOOLEAN NOT NULL,
    is_email_verified BOOLEAN DEFAULT FALSE,
    mobile_phone INT8,
    display_name VARCHAR(255) NOT NULL,
    token VARCHAR(66),
    token_updated_at TIMESTAMPTZ,
    about VARCHAR(255),
    birthday VARCHAR(255),
    gender VARCHAR(255),
    user_status user_status DEFAULT 'ACTIVE',
    notification_count int8 DEFAULT 0,
    login_attempt_policy BIGINT NOT NULL,
    notify_password_change BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    FOREIGN KEY (login_attempt_policy) REFERENCES login_attempt_policy(id)
);
ALTER TABLE users
ADD CONSTRAINT unique_email_username UNIQUE (email, username);
CREATE INDEX idx_username ON users (username);
CREATE INDEX trgm_index ON users USING gin (username gin_trgm_ops);
CREATE INDEX trgm_email ON users USING gin (email gin_trgm_ops);

CREATE TABLE IF NOT EXISTS profiles (
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    id UUID PRIMARY KEY,
    is_onboarded BOOLEAN NOT NULL,
    onboarding_step JSONB NOT NULL,
    billing_address_country VARCHAR(255) NOT NULL,
    billing_address JSONB, -- full address (street, city, zip)
    has_billing_address BOOLEAN NOT NULL, -- flag for invoice eligibility
    company_name VARCHAR(255) NOT NULL, -- legal / invoice name
    user_id UUID NOT NULL,
    role VARCHAR(300),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- SELECT 
--     i.*,
--     p.company_name,
--     p.billing_address
-- FROM
--     public.invoices AS i
--     JOIN public.workspaces AS w ON i.workspace_id = w.id
--     JOIN public.users AS u ON w.owner_id = u.id
--     JOIN public.profiles AS p ON p.user_id = u.id
-- WHERE
--     i.id = :invoiceId;

-- Historical consistency
-- If you need to freeze the billing details at invoice time (so later edits to the profile don’t affect past invoices), you can:

-- Denormalize: copy p.company_name + p.billing_address into new columns on invoices when you generate them.

-- Or, snapshot the profile JSON into an invoice_metadata JSONB column.

CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id uuid NOT NULL,
    is_chosen BOOLEAN DEFAULT FALSE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_profile_limit (
    user_id uuid,
    profile_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (user_id)
);
CREATE TABLE IF NOT EXISTS activation_codes (
    id uuid DEFAULT gen_random_uuid(),
    code_type VARCHAR(20) NOT NULL,
    -- 'activation' or 'device_verification'
    hashed_code VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id uuid NOT NULL,
    reset_count INT DEFAULT 0,
    created_by VARCHAR(36),
    created_date TIMESTAMP NOT NULL,
    modified_by VARCHAR(36),
    modified_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS password_reset_otp (
    id uuid DEFAULT gen_random_uuid(),
    hashed_otp VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id uuid NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS password_reset_token (
    id uuid DEFAULT gen_random_uuid(),
    token VARCHAR(255),
    expiration_time TIMESTAMP NOT NULL,
    user_id uuid NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS user_devices (
    id uuid DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
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
    action_type VARCHAR(50) NOT NULL,
    action_status VARCHAR(50) NOT NULL DEFAULT 'pending',
    verification_code VARCHAR(64),
    user_device_id uuid NOT NULL,
    is_user_notified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_time TIMESTAMP,
    FOREIGN KEY (user_device_id) REFERENCES user_devices(id)
);
CREATE TABLE IF NOT EXISTS login_attempts (
    id uuid DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
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
    user_id uuid NOT NULL,
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
    user_id uuid NOT NULL,
    lockout_reason VARCHAR(255) NOT NULL,
    is_requested_by_user BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id uuid DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
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
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    interval_type time_period NOT NULL,
    active_user_count INT NOT NULL,
    registered_users INT NOT NULL,
    registered_users_change DECIMAL(5, 2),
    PRIMARY KEY (id)
);