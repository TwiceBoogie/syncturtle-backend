CREATE TABLE IF NOT EXISTS instances (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id UUID                 PRIMARY KEY,
    name                    VARCHAR(255), -- e.g. "SyncTurtle Enterprise" instance_name
    slug                    VARCHAR(100) UNIQUE, -- e.g. "syncturtle-enterprise-01"
    edition                 VARCHAR(50) NOT NULL, -- community|cloud|enterprise
    -- app binary
    current_version         VARCHAR(50) NOT NULL,
    latest_version          VARCHAR(255),
    last_checked_at         TIMESTAMPTZ,
    -- config
    config_version          BIGINT NOT NULL,
    config_last_checked_at  TIMESTAMPTZ NOT NULL,
    domain                  TEXT,
    namespace               VARCHAR(255),
    machine_signature       VARCHAR(255) UNIQUE NOT NULL, -- unique and each enterprise deployment would have one.
    vm_host                 TEXT, -- e.g. vm-42-internal
    is_setup_done           BOOLEAN NOT NULL,
    is_verified             BOOLEAN NOT NULL,
    is_test                 BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS instance_configurations (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    key                     TEXT NOT NULL UNIQUE,
    value                   TEXT,
    category                TEXT NOT NULL,
    is_encrypted            BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS instance_admins (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    instance_id             UUID NOT NULL, -- fk at app-level
    user_id                 UUID NOT NULL, -- logical fk to user-service.users.id
    role                    integer NOT NULL,
    is_verified             BOOLEAN NOT NULL,
    CONSTRAINT instance_admins_role_check CHECK ((role >= 0))
);

-- prevents duplicates
CREATE UNIQUE INDEX IF NOT EXISTS ux_instance_admin_unique
    ON instance_admins(instance_id, user_id);

CREATE TABLE IF NOT EXISTS plans (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    key                     VARCHAR(50) NOT NULL, -- free|pro|enterprise
    name                    VARCHAR(100) NOT NULL,
    features                JSONB NOT NULL,
    price_per_month         NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tenants (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    instance_id             UUID NOT NULL,
    plan_id                 UUID NOT NULL,
    name                    VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise"
    slug                    VARCHAR(100) NOT NULL,
    owner_id                UUID NOT NULL, -- logicial FK to user-service.users.id
    is_owner                BOOLEAN NOT NULL,
    -- settings JSONB NOT NULL DEFAULT '{}'::JSONB,
    FOREIGN KEY (instance_id) REFERENCES instances(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES plans(id),
    UNIQUE(instance_id, slug)   
);

-- CREATE TABLE IF NOT EXISTS pending_admins (
--     id                      UUID PRIMARY KEY,
--     instance_id             UUID NOT NULL,
--     email                   CITEXT NOT NULL,
--     correlation_id          UUID NOT NULL,
--     created_at              TIMESTAMPTZ NOT NULL
-- )