-- =========================================
-- Extensions
-- =========================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;  -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- =========================================
-- Core reference tables
-- =========================================
CREATE TABLE IF NOT EXISTS login_policies (
  created_at         TIMESTAMPTZ  NOT NULL,
  updated_at         TIMESTAMPTZ NOT NULL,
  id                 BIGINT GENERATED ALWAYS AS IDENTITY,
  policy_name        VARCHAR(50)  NOT NULL,
  max_attempts       INT          NOT NULL,
  lockout_duration   INTERVAL,
  reset_duration     INTERVAL,
  created_by               VARCHAR(36),
  updated_by               VARCHAR(36),
  CONSTRAINT pk_login_policies PRIMARY KEY (id),
  CONSTRAINT uq_login_policies_name UNIQUE (policy_name)
);

-- =========================================
-- Users
-- =========================================
CREATE TABLE IF NOT EXISTS users (
  created_at               TIMESTAMPTZ NOT NULL,
  updated_at               TIMESTAMPTZ NOT NULL,
  id                       UUID        NOT NULL,
  username                 VARCHAR(128) NOT NULL,
  email                    VARCHAR(255) NOT NULL,
  first_name               VARCHAR(36)  NOT NULL,
  last_name                VARCHAR(36)  NOT NULL,
  password                 VARCHAR(255),
  is_password_autoset      BOOLEAN      NOT NULL,
  is_password_expired      BOOLEAN      NOT NULL,
  is_email_verified        BOOLEAN      NOT NULL,
  is_active                BOOLEAN      NOT NULL,
  mobile_phone             VARCHAR(32),
  display_name             VARCHAR(255) NOT NULL,
  user_status              VARCHAR(32)  NOT NULL,
  notification_count       BIGINT       NOT NULL,
  login_policy_id  BIGINT       NOT NULL,
  notify_password_change   BOOLEAN      NOT NULL,
  created_by               VARCHAR(36),
  updated_by               VARCHAR(36),

  -- Denormalized last-known snapshot
  last_login_time          TIMESTAMPTZ,
  last_login_ip            VARCHAR(255),
  last_login_uagent        VARCHAR(1024),
  last_login_medium        VARCHAR(32),
  last_active              TIMESTAMPTZ,

  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT fk_users_login_policy
    FOREIGN KEY (login_policy_id) REFERENCES login_policies(id),
  CONSTRAINT ck_users_status CHECK (user_status IN (
    'SUSPENDED','INACTIVE','ACTIVE','ARCHIVED',
    'BLOCKED','PENDING_DELETION','LOCKED','PENDING_USER_CONFIRMATION'
  ))
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS gin_users_username ON users USING gin (username gin_trgm_ops);
CREATE INDEX IF NOT EXISTS gin_users_email    ON users USING gin (email gin_trgm_ops);

-- Case-insensitive uniqueness
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email_lower
  ON users (lower(email));
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_username_lower
  ON users (lower(username));

-- =========================================
-- Refresh Tokens (session)
-- =========================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id                      BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id                 UUID        NOT NULL,
  handle                  TEXT,
  secret_hash             TEXT        NOT NULL,
  issued_at               TIMESTAMPTZ NOT NULL,
  absolute_expires_at     TIMESTAMPTZ NOT NULL,
  expires_at              TIMESTAMPTZ NOT NULL,
  last_used_at            TIMESTAMPTZ NOT NULL,
  revoked                 BOOLEAN     NOT NULL,
  revoked_at              TIMESTAMPTZ,
  replaced_by             BIGINT,
  version                 BIGINT      NOT NULL,

  -- Provenance at issuance
  issued_ip               VARCHAR(255),
  issued_user_agent       VARCHAR(1024),
  issued_domain           VARCHAR(255),
  issued_context          VARCHAR(16),

  -- Last-touch metadata
  last_used_ip            VARCHAR(255),
  last_used_user_agent    VARCHAR(1024),

  CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
  CONSTRAINT fk_rt_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_rt_replaced_by
    FOREIGN KEY (replaced_by) REFERENCES refresh_tokens(id) ON DELETE SET NULL,
  CONSTRAINT uc_rt_handle UNIQUE (handle),
  CONSTRAINT uc_rt_secret_hash UNIQUE (secret_hash),
  CONSTRAINT uc_rt_replaced_by UNIQUE (replaced_by),
  CONSTRAINT ck_rt_time_order
    CHECK (expires_at > issued_at
       AND absolute_expires_at > issued_at
       AND expires_at <= absolute_expires_at),
  CONSTRAINT ck_rt_issued_context
    CHECK (issued_context IN ('ADMIN','APP','SPACE'))
);

-- Indexes for refresh_tokens
CREATE INDEX IF NOT EXISTS idx_rt_user               ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_rt_user_active        ON refresh_tokens (user_id, revoked, expires_at);
CREATE INDEX IF NOT EXISTS idx_rt_expires            ON refresh_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_rt_absexp             ON refresh_tokens(absolute_expires_at);
CREATE INDEX IF NOT EXISTS idx_rt_revoked            ON refresh_tokens(revoked);
CREATE INDEX IF NOT EXISTS idx_rt_last_used          ON refresh_tokens(last_used_at);

-- =========================================
-- Profiles & related
-- =========================================
CREATE TABLE IF NOT EXISTS profiles (
  created_at               TIMESTAMPTZ NOT NULL,
  updated_at               TIMESTAMPTZ NOT NULL,
  id                       UUID        NOT NULL,
  is_onboarded             BOOLEAN     NOT NULL,
  onboarding_step          JSONB       NOT NULL,
  billing_address_country  VARCHAR(255) NOT NULL,
  billing_address          JSONB,
  has_billing_address      BOOLEAN     NOT NULL,
  company_name             VARCHAR(255) NOT NULL,
  user_id                  UUID        NOT NULL,
  role                     VARCHAR(300),
  created_by               VARCHAR(36),
  updated_by               VARCHAR(36),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  PRIMARY KEY (id),
);

-- Enforce true 1:1 (one profile per user)
CREATE UNIQUE INDEX IF NOT EXISTS uq_profiles_user ON profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_profiles_user ON profiles(user_id);

CREATE TABLE IF NOT EXISTS user_profiles (
  id            BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id       UUID NOT NULL,
  is_chosen     BOOLEAN NOT NULL,
  file_name     VARCHAR(255) NOT NULL,
  file_path     VARCHAR(255) NOT NULL,
  file_type     VARCHAR(50),
  upload_date   TIMESTAMPTZ NOT NULL,
  CONSTRAINT pk_user_profiles PRIMARY KEY (id),
  CONSTRAINT fk_user_profiles_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Ensure only one "chosen" per user
CREATE UNIQUE INDEX IF NOT EXISTS uc_user_profiles_chosen
  ON user_profiles(user_id)
  WHERE is_chosen IS TRUE;

-- Optional: prevent duplicate path per user
-- CREATE UNIQUE INDEX IF NOT EXISTS uc_user_profiles_path ON user_profiles(user_id, file_path);

CREATE INDEX IF NOT EXISTS idx_user_profiles_user ON user_profiles(user_id);

CREATE TABLE IF NOT EXISTS user_profile_limit (
  user_id       UUID NOT NULL,
  profile_count INT  NOT NULL,
  CONSTRAINT pk_user_profile_limit PRIMARY KEY (user_id),
  CONSTRAINT fk_user_profile_limit_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =========================================
-- Activation & Password Reset
-- =========================================
CREATE TABLE IF NOT EXISTS activation_codes (
  id               UUID        NOT NULL,
  code_type        VARCHAR(20) NOT NULL, -- 'activation','device_verification'
  hashed_code      VARCHAR(255),
  expiration_time  TIMESTAMPTZ NOT NULL,
  user_id          UUID        NOT NULL,
  reset_count      INT         NOT NULL,
  created_by       VARCHAR(36),
  created_date     TIMESTAMPTZ NOT NULL,
  modified_by      VARCHAR(36),
  modified_date    TIMESTAMPTZ,
  CONSTRAINT pk_activation_codes PRIMARY KEY (id),
  CONSTRAINT fk_activation_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_activation_user   ON activation_codes(user_id);
CREATE INDEX IF NOT EXISTS idx_activation_expiry ON activation_codes(expiration_time);

CREATE TABLE IF NOT EXISTS password_reset_otp (
  id               UUID        NOT NULL,
  hashed_otp       VARCHAR(255),
  expiration_time  TIMESTAMPTZ NOT NULL,
  user_id          UUID        NOT NULL,
  created_date     TIMESTAMPTZ NOT NULL,
  modified_date    TIMESTAMPTZ,
  CONSTRAINT pk_pw_reset_otp PRIMARY KEY (id),
  CONSTRAINT fk_pw_reset_otp_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pw_reset_otp_user   ON password_reset_otp(user_id);
CREATE INDEX IF NOT EXISTS idx_pw_reset_otp_expiry ON password_reset_otp(expiration_time);

CREATE TABLE IF NOT EXISTS password_reset_token (
  id               UUID        NOT NULL,
  token            VARCHAR(255),
  expiration_time  TIMESTAMPTZ NOT NULL,
  user_id          UUID        NOT NULL,
  created_date     TIMESTAMPTZ NOT NULL,
  modified_date    TIMESTAMPTZ,
  CONSTRAINT pk_pw_reset_token PRIMARY KEY (id),
  CONSTRAINT fk_pw_reset_token_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pw_reset_token_user   ON password_reset_token(user_id);
CREATE INDEX IF NOT EXISTS idx_pw_reset_token_expiry ON password_reset_token(expiration_time);

-- =========================================
-- Devices & Actions
-- =========================================
CREATE TABLE IF NOT EXISTS user_devices (
  id                        UUID        NOT NULL,
  user_id                   UUID        NOT NULL,
  device_key                VARCHAR(255) NOT NULL,
  device_name               VARCHAR(255) NOT NULL,
  last_access               TIMESTAMPTZ  NOT NULL,
  first_access_timestamp    TIMESTAMPTZ  NOT NULL,
  CONSTRAINT pk_user_devices PRIMARY KEY (id),
  CONSTRAINT fk_user_devices_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uc_user_device_key UNIQUE (user_id, device_key)
);

CREATE INDEX IF NOT EXISTS idx_user_devices_user       ON user_devices(user_id);
CREATE INDEX IF NOT EXISTS idx_user_devices_lastaccess ON user_devices(user_id, last_access DESC);

CREATE TABLE IF NOT EXISTS user_actions (
  id                 BIGINT GENERATED ALWAYS AS IDENTITY,
  action_type        VARCHAR(50)  NOT NULL,
  action_status      VARCHAR(50)  NOT NULL,
  verification_code  VARCHAR(64),
  user_device_id     UUID         NOT NULL,
  is_user_notified   BOOLEAN      NOT NULL,
  created_at         TIMESTAMPTZ  NOT NULL,
  updated_at         TIMESTAMPTZ  NOT NULL,
  expiration_time    TIMESTAMPTZ,
  CONSTRAINT pk_user_actions PRIMARY KEY (id),
  CONSTRAINT fk_user_actions_device
    FOREIGN KEY (user_device_id) REFERENCES user_devices(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_actions_device ON user_actions(user_device_id);
CREATE INDEX IF NOT EXISTS idx_user_actions_recent ON user_actions(user_device_id, created_at DESC);

-- =========================================
-- Login Attempts (audit)
-- =========================================
CREATE TABLE IF NOT EXISTS logins (
  id                 UUID        NOT NULL,
  user_id            UUID,
  attempt_timestamp  TIMESTAMPTZ NOT NULL,
  success            BOOLEAN     NOT NULL,
  failure_reason     TEXT,
  ip_address         VARCHAR(255),
  user_agent         VARCHAR(1024),
  domain             VARCHAR(255),
  auth_medium        VARCHAR(32)  NOT NULL,
  context            VARCHAR(32)  NOT NULL,
  device_id          UUID,
  is_new_device      BOOLEAN      NOT NULL,
  request_id         VARCHAR(64),
  correlation_id     VARCHAR(64),
  jwt_jti            VARCHAR(64),
  refresh_token_id   BIGINT,
  CONSTRAINT pk_logins PRIMARY KEY (id),
  CONSTRAINT fk_la_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_la_refresh
    FOREIGN KEY (refresh_token_id) REFERENCES refresh_tokens(id) ON DELETE SET NULL,
  CONSTRAINT ck_la_auth_medium CHECK (auth_medium IN ('PASSWORD','MAGIC_LINK','SSO')),
  CONSTRAINT ck_la_context     CHECK (context IN ('ADMIN','APP','SPACE'))
);

-- General indexes
CREATE INDEX IF NOT EXISTS idx_la_user_time ON logins (user_id, attempt_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_la_time      ON logins (attempt_timestamp);
CREATE INDEX IF NOT EXISTS idx_la_ip        ON logins (ip_address);

-- Hot-path rate-limit indexes (partial)
CREATE INDEX IF NOT EXISTS idx_la_user_fail_time
  ON logins (user_id, attempt_timestamp DESC)
  WHERE success = false;

CREATE INDEX IF NOT EXISTS idx_la_ip_fail_time
  ON logins (ip_address, attempt_timestamp DESC)
  WHERE success = false;

-- Time-range scans on very large tables
CREATE INDEX IF NOT EXISTS brin_la_time ON logins USING brin (attempt_timestamp);

-- =========================================
-- Recovery Attempts & Locks
-- =========================================
CREATE TABLE IF NOT EXISTS recovery_attempts (
  id                 BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id            UUID        NOT NULL,
  attempt_timestamp  TIMESTAMPTZ NOT NULL,
  success            BOOLEAN     NOT NULL,
  ip_address         VARCHAR(50),
  recovery_type      VARCHAR(50),
  is_new_device      BOOLEAN     NOT NULL,
  CONSTRAINT pk_recovery_attempts PRIMARY KEY (id),
  CONSTRAINT fk_recovery_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_recovery_user_time ON recovery_attempts (user_id, attempt_timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_recovery_ip_fail_time
  ON recovery_attempts (ip_address, attempt_timestamp DESC)
  WHERE success = false;

CREATE TABLE IF NOT EXISTS locked_users (
  created_at           TIMESTAMPTZ NOT NULL,
  updated_at           TIMESTAMPTZ NOT NULL,
  id                   BIGINT GENERATED ALWAYS AS IDENTITY,
  user_id              UUID, -- could be anonymous user
  lockout_start        TIMESTAMPTZ NOT NULL,
  lockout_end          TIMESTAMPTZ NOT NULL,
  lockout_reason       VARCHAR(255) NOT NULL,
  is_requested_by_user BOOLEAN      NOT NULL,
  failed_during_lock_count INT NOT NULL,
  escalation_level         INT NULL NULL,
  last_failed_attempt      TIMESTAMPTZ,
  version                  BIGINT NOT NULL,
  created_by               VARCHAR(36),
  updated_by               VARCHAR(36),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_locked_users_user ON locked_users(user_id);

-- =========================================
-- Statistics
-- =========================================
CREATE TABLE IF NOT EXISTS user_statistics (
  created_at              TIMESTAMPTZ NOT NULL,
  updated_at              TIMESTAMPTZ NOT NULL,
  id                      BIGINT GENERATED ALWAYS AS IDENTITY,
  interval_type           VARCHAR(16)  NOT NULL,
  active_user_count       INT          NOT NULL,
  registered_users        INT          NOT NULL,
  registered_users_change DECIMAL(5,2),
  CONSTRAINT pk_user_statistics PRIMARY KEY (id),
  CONSTRAINT ck_user_statistics_interval
    CHECK (interval_type IN ('DAILY','WEEKLY','MONTHLY','YEARLY'))
);

-- =========================================
-- Seed data (explicit columns—no DB defaults)
-- =========================================
INSERT INTO login_policies
  (policy_name, max_attempts, lockout_duration, reset_duration, created_at, updated_at)
VALUES
  ('Default Policy',       5, '5 minutes', '24 hours', now(), now()),
  ('High Security Policy', 3, '15 minutes','48 hours', now(), now()),
  ('No Lockout Policy',   10,  NULL,       NULL,       now(), now())
ON CONFLICT DO NOTHING;
