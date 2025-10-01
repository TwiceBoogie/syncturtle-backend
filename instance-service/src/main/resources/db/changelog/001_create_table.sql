CREATE TABLE IF NOT EXISTS instances (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id UUID                 PRIMARY KEY,
    instance_name                    VARCHAR(255), -- e.g. "SyncTurtle Enterprise" instance_name
    slug                    VARCHAR(100) UNIQUE, -- e.g. "syncturtle-enterprise-01"
    edition                 VARCHAR(50) NOT NULL, -- community|cloud|enterprise
    -- app binary
    current_version         VARCHAR(50) NOT NULL,
    latest_version          VARCHAR(255),
    last_checked_at         TIMESTAMPTZ,
    -- config
    config_version          BIGINT NOT NULL,
    config_last_checked_at  TIMESTAMPTZ NOT NULL,
    version                 BIGINT NOT NULL,
    domain                  TEXT,
    namespace               VARCHAR(255),
    instance_id             VARCHAR(255) UNIQUE NOT NULL, -- unique and each enterprise deployment would have one. instance_id
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
    price_per_month         NUMERIC(10,2) NOT NULL,
    version                 BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_plans_features_gin ON plans USING GIN (features);
-- CREATE TABLE IF NOT EXISTS tenants (
--     created_at              TIMESTAMPTZ NOT NULL,
--     updated_at              TIMESTAMPTZ NOT NULL,
--     id                      UUID PRIMARY KEY,
--     instance_id             UUID NOT NULL,
--     plan_id                 UUID NOT NULL,
--     name                    VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise"
--     slug                    VARCHAR(100) NOT NULL,
--     organization_size       integer,
--     owner_id                UUID NOT NULL, -- logicial FK to user-service.users.id
--     is_owner                BOOLEAN NOT NULL,
--     -- settings JSONB NOT NULL DEFAULT '{}'::JSONB,
--     FOREIGN KEY (instance_id) REFERENCES instances(id) ON DELETE CASCADE,
--     FOREIGN KEY (plan_id) REFERENCES plans(id),
--     UNIQUE(instance_id, slug)   
-- );

-- CREATE TABLE IF NOT EXISTS tenant_member_invites (
--     created_at              TIMESTAMPTZ NOT NULL,
--     updated_at              TIMESTAMPTZ NOT NULL,
--     id                      UUID PRIMARY KEY,
--     tenant_id               UUID NOT NULL,
--     email                   VARCHAR(255) NOT NULL,
--     accepted                BOOLEAN NOT NULL,
--     token                   VARCHAR(255) NOT NULL,
--     message                 TEXT,
--     responded_at            TIMESTAMPTZ,
--     role                    SMALLINT NOT NULL,
--     deleted_at              TIMESTAMPTZ,
--     -- optional aduit (logical)
--     created_by              UUID,
--     updated_by              UUID,
--     CONSTRAINT ck_tenant_invites_role CHECK (role >= 0),
--     FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
-- );

-- -- enfore "one active invite per (tenant,email)"
-- CREATE UNIQUE INDEX IF NOT EXISTS ux_ti_unique_active
--     ON tenant_member_invites(tenant_id, email)
--     WHERE  deleted_at IS NULL;

-- -- hot paths lookups
-- CREATE INDEX IF NOT EXISTS idx_ti_tenant ON tenant_member_invites(tenant_id);
-- CREATE INDEX IF NOT EXISTS idx_ti_email_pending
--     ON tenant_member_invites(email)
--     WHERE accepted = FALSE AND deleted_at IS NULL;

-- CREATE TABLE IF NOT EXISTS tenant_members (
--     created_at              TIMESTAMPTZ NOT NULL,
--     updated_at              TIMESTAMPTZ NOT NULL,
--     id                      UUID PRIMARY KEY,
--     tenant_id               UUID NOT NULL,
--     member_id               UUID NOT NULL, -- logical fk to user-service: users.id
--     role                    SMALLINT NOT NULL,
--     company_role            TEXT,
--     is_active               BOOLEAN NOT NULL,
--     deleted_at              TIMESTAMPTZ,
--     -- optional aduit (logical)
--     created_by              UUID,
--     updated_by              UUID,
--     CONSTRAINT ck_tenant_members_role CHECK ((role >= 0)),
--     FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
-- );

-- -- enfore "one active membership per (tenant,user)"
-- CREATE UNIQUE INDEX IF NOT EXISTS ux_tm_unique_active
--     ON tenant_members(tenant_id, member_id)
--     WHERE deleted_at IS NULL;

-- CREATE INDEX IF NOT EXISTS idx_tm_tenant ON tenant_members(tenant_id);
-- CREATE INDEX IF NOT EXISTS idx_tm_member ON tenant_members(member_id);
-- CREATE INDEX IF NOT EXISTS idx_tm_user_active ON tenant_members(member_id, is_active)
--     WHERE deleted_at IS NULL;

-- Optional (recommended): ensure keys are unique
-- CREATE UNIQUE INDEX IF NOT EXISTS ux_plans_key ON plans(key);

INSERT INTO plans (id, created_at, updated_at, key, name, features, price_per_month) VALUES
-- 1) Free / Community (cloud)
('00000000-0000-0000-0000-000000000001', now(), now(),
 'free', 'Free',
 '{
  "description": "For individuals exploring SyncTurtle",
  "limits": {
    "workspaces": 1,
    "members_per_workspace": 2,
    "projects_per_workspace": 5,
    "storage_gb": 1,
    "automations_per_workspace": 2
  },
  "features": {
    "custom_domains": false,
    "ai_assist": false,
    "webhooks": false,
    "api_access": true,
    "priority_support": false,
    "audit_logs": false,
    "retention_days": 30,
    "sso_saml": false,
    "scim": false,
    "backups_daily": false
  },
  "labels": ["starter", "cloud"]
 }'::jsonb, 0.00),

-- 2) Personal (solo power-user)
('00000000-0000-0000-0000-000000000002', now(), now(),
 'personal', 'Personal',
 '{
  "description": "Solo creators & power users",
  "limits": {
    "workspaces": 3,
    "members_per_workspace": 1,
    "projects_per_workspace": 50,
    "storage_gb": 10,
    "automations_per_workspace": 10
  },
  "features": {
    "custom_domains": false,
    "ai_assist": true,
    "webhooks": true,
    "api_access": true,
    "priority_support": false,
    "audit_logs": false,
    "retention_days": 90,
    "sso_saml": false,
    "scim": false,
    "backups_daily": true
  },
  "labels": ["individual", "cloud"]
 }'::jsonb, 12.00),

-- 3) Team (small teams)
('00000000-0000-0000-0000-000000000003', now(), now(),
 'team', 'Team',
 '{
  "description": "Small teams shipping together",
  "limits": {
    "workspaces": 10,
    "members_per_workspace": 25,
    "projects_per_workspace": 200,
    "storage_gb": 100,
    "automations_per_workspace": 50
  },
  "features": {
    "custom_domains": true,
    "ai_assist": true,
    "webhooks": true,
    "api_access": true,
    "priority_support": true,
    "audit_logs": true,
    "retention_days": 365,
    "sso_saml": false,
    "scim": false,
    "backups_daily": true
  },
  "labels": ["teams", "cloud"]
 }'::jsonb, 29.00),

-- 4) Business (growing orgs)
('00000000-0000-0000-0000-000000000004', now(), now(),
 'business', 'Business',
 '{
  "description": "Growing organizations that need controls",
  "limits": {
    "workspaces": 50,
    "members_per_workspace": 200,
    "projects_per_workspace": 1000,
    "storage_gb": 1000,
    "automations_per_workspace": 200
  },
  "features": {
    "custom_domains": true,
    "ai_assist": true,
    "webhooks": true,
    "api_access": true,
    "priority_support": true,
    "audit_logs": true,
    "retention_days": 1095,
    "sso_saml": true,
    "scim": true,
    "backups_daily": true
  },
  "labels": ["business", "cloud", "security"]
 }'::jsonb, 79.00),

-- 5) Enterprise (SSO/SCIM, audits, premium support)
('00000000-0000-0000-0000-000000000005', now(), now(),
 'enterprise', 'Enterprise',
 '{
  "description": "Enterprises with compliance & scale needs",
  "limits": {
    "workspaces": 1000,
    "members_per_workspace": 10000,
    "projects_per_workspace": 100000,
    "storage_gb": 10000,
    "automations_per_workspace": 1000
  },
  "features": {
    "custom_domains": true,
    "ai_assist": true,
    "webhooks": true,
    "api_access": true,
    "priority_support": true,
    "audit_logs": true,
    "retention_days": 3650,
    "sso_saml": true,
    "scim": true,
    "backups_daily": true,
    "dedicated_env": true,
    "sla": "99.9%"
  },
  "labels": ["enterprise", "cloud", "compliance"]
 }'::jsonb, 199.00),

-- 6) OSS Self-Host (open-source LifeOS)
('00000000-0000-0000-0000-000000000006', now(), now(),
 'oss-selfhost', 'OSS Self-Host',
 '{
  "description": "Community edition for self-hosting SyncTurtle",
  "limits": {
    "workspaces": 100,
    "members_per_workspace": 1000,
    "projects_per_workspace": 10000,
    "storage_gb": 0,
    "automations_per_workspace": 0
  },
  "features": {
    "custom_domains": true,
    "ai_assist": false,
    "webhooks": true,
    "api_access": true,
    "priority_support": false,
    "audit_logs": false,
    "retention_days": 0,
    "sso_saml": false,
    "scim": false,
    "backups_daily": false,
    "license": "AGPL-3.0"
  },
  "labels": ["self-host", "oss"]
 }'::jsonb, 0.00)
ON CONFLICT (id) DO UPDATE SET
  updated_at = EXCLUDED.updated_at,
  key = EXCLUDED.key,
  name = EXCLUDED.name,
  features = EXCLUDED.features,
  price_per_month = EXCLUDED.price_per_month;
