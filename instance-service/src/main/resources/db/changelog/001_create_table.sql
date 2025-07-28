CREATE TABLE IF NOT EXISTS instances (
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL,
    id UUID PRIMARY KEY,
    slug VARCHAR(100) UNIQUE NOT NULL, -- e.g. "syncturtle-enterprise-01"
    name VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise" instance_name
    edition VARCHAR(50) NOT NULL, -- community|cloud|enterprise
    current_version VARCHAR(50) NOT NULL,
    latest_version VARCHAR(255),
    last_checked_at TIMESTAMPTZ,
    domain TEXT,
    namespace VARCHAR(255),
    machine_signature VARCHAR(255) UNIQUE NOT NULL, -- unique and each enterprise deployment would have one.
    vm_host TEXT, -- e.g. vm-42-internal
    is_setup_done BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_test BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS instance_configurations (
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL,
    id UUID PRIMARY KEY,
    key VARCHAR(100) NOT NULL,
    value TEXT,
    category TEXT NOT NULL,
    is_encrypted BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS instance_admins (
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL,
    instance_id UUID NOT NULL, -- fk at app-level
    user_id UUID NOT NULL, -- logical fk to user-service.users.id
    role integer NOT NULL,
    is_verified BOOLEAN NOT NULL,
    PRIMARY KEY (instance_id, user_id),
    CONSTRAINT instance_admins_role_check CHECK ((role >= 0))
);

CREATE TABLE IF NOT EXISTS plans (
    key VARCHAR(50) PRIMARY KEY, -- free|pro|enterprise
    name VARCHAR(100) NOT NULL,
    features JSONB NOT NULL DEFAULT '{}'::JSONB,
    price_per_month NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tenants (
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL,
    id UUID PRIMARY KEY,
    instance_id UUID NOT NULL,
    plan_key VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise"
    slug VARCHAR(100) NOT NULL,
    owner_id UUID NOT NULL, -- logicial FK to user-service.users.id
    -- settings JSONB NOT NULL DEFAULT '{}'::JSONB,
    FOREIGN KEY (instance_id) REFERENCES instances(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_key) REFERENCES plans(key),
    UNIQUE(instance_id, slug)   
);