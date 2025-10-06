CREATE TABLE IF NOT EXISTS instances (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id UUID                 PRIMARY KEY,
    -- e.g. "SyncTurtle Enterprise" instance_name
    instance_name           VARCHAR(100),
    -- e.g. "syncturtle-enterprise-01"; unique among active rows
    slug                    VARCHAR(100) UNIQUE,
    -- community|cloud|enterprise
    edition                 VARCHAR(50) NOT NULL,
    -- app binary
    current_version         VARCHAR(50) NOT NULL,
    latest_version          VARCHAR(50),
    last_checked_at         TIMESTAMPTZ,
    -- config version
    config_version          BIGINT NOT NULL,
    config_last_checked_at  TIMESTAMPTZ,

    version                 BIGINT NOT NULL,
    domain                  TEXT,
    namespace               VARCHAR(255),
    -- unique and each enterprise deployment would have one
    instance_id             VARCHAR(255) UNIQUE NOT NULL,
    -- e.g. vm-42-internal
    vm_host                 TEXT,
    is_setup_done           BOOLEAN NOT NULL,
    is_verified             BOOLEAN NOT NULL,
    is_test                 BOOLEAN NOT NULL,
    deleted_at              TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_instances_slug_active
  ON instances(slug) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_instances_id
  ON instances(instance_id);

CREATE TABLE IF NOT EXISTS instance_configurations (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    key                     TEXT NOT NULL UNIQUE,
    value                   TEXT,
    category                TEXT NOT NULL,
    is_encrypted            BOOLEAN NOT NULL
);

-- no fk to users_lite since its a projection
CREATE TABLE IF NOT EXISTS instance_admins (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    instance_id             UUID NOT NULL, -- fk at app-level
    user_id                 UUID NOT NULL, -- logical fk to user-service.users.id
    role                    INTEGER NOT NULL CHECK ((role >= 0)),
    is_verified             BOOLEAN NOT NULL,
    deleted_at              TIMESTAMPTZ,
    FOREIGN KEY (instance_id) REFERENCES instances(id)
      ON DELETE CASCADE
);

-- prevents duplicates; one admin per record
CREATE UNIQUE INDEX IF NOT EXISTS ux_instance_admin_unique_active
    ON instance_admins(instance_id, user_id) WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_instance_admin_user
  ON instance_admins(user_id);

-- save to truncate/rebuilt; just a projection
CREATE TABLE IF NOT EXISTS users_lite (
  id  UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(36) NOT NULL,
  last_name VARCHAR(36) NOT NULL,
  display_name VARCHAR(255) NOT NULL,
  date_joined TIMESTAMPTZ NOT NULL,
  version BIGINT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_lite_email_lower
  ON users_lite (lower(email));

-- ENTITLEMENT-SERVICE
-- CREATE TABLE IF NOT EXISTS plan_catalog (
--   created_at  TIMESTAMPTZ NOT NULL,
--   updated_at  TIMESTAMPTZ NOT NULL,

--   key TEXT PRIMARY KEY, -- 'free' | 'team' | 'personal'
--   name  TEXT NOT NULL,
--   features_json JSONB NOT NULL,
--   price_per_month NUMERIC(10, 2)
-- );

-- CREATE TABLE IF NOT EXISTS customers (
--   created_at  TIMESTAMPTZ NOT NULL,
--   updated_at  TIMESTAMPTZ NOT NULL,

--   id UUID PRIMARY KEY,
--   email TEXT NOT NULL,
--   name  text,
--   billing_provider  TEXT NOT NULL, -- stripe, etc...
--   billing_customer_id TEXT -- stripe customer id
-- );

-- -- what they bought (cloud subscriptions only)
-- CREATE TABLE IF NOT EXISTS subscriptions (
--   created_at  TIMESTAMPTZ NOT NULL,
--   updated_at  TIMESTAMPTZ NOT NULL,
  
--   id  UUID PRIMARY KEY,
--   customer_id UUID NOT NULL,
--   plan_key  TEXT NOT NULL, -- free|team|business|etc..
--   status  TEXT NOT NULL, -- active, past due, canceled,
--   provider_sub_id TEXT NOT NULL, -- stripe subscription id
--   current_period_end  TIMESTAMPTZ NOT NULL,

--   FOREIGN KEY (customer_id) REFERENCES customers(id)
-- );

-- registry of deployments we operate for a customer (includes enterprise)
-- CREATE TABLE IF NOT EXISTS deployments (
--   created_at  TIMESTAMPTZ NOT NULL,
--   updated_at  TIMESTAMPTZ NOT NULL,

--   id  UUID PRIMARY KEY,
--   customer_id UUID NOT NULL,
--   kind  TEXT NOT NULL, -- 'cloud-shared' | 'enterprise-dedicated'
--   -- discovery/ops info for enterprise
--   vm_host TEXT, -- IP/hostname of their stack
--   api_base_url  TEXT,
--   instance_external_id  TEXT, -- that deployment's Instance.id (uuid) if we expose it upstream
--   license_key_hash  TEXT, -- optional: signed license for enterprise
--   status  TEXT NOT NULL, -- 'provisioning', 'active', 'suspended'

--   FOREIGN KEY (customer_id) REFERENCES customers(id)
-- );

-- CREATE TABLE IF NOT EXISTS deployment_domains (
--   created_at  TIMESTAMPTZ NOT NULL,
--   updated_at  TIMESTAMPTZ NOT NULL,

--   id  UUID PRIMARY KEY,
--   deployment_id UUID NOT NULL,
--   host  TEXT NOT NULL UNIQUE, -- e.g. random.syncturtle.com
--   is_primary BOOLEAN NOT NULL,

--   FOREIGN KEY (deployment_id) REFERENCES deployments(id)
-- );

-- CREATE TABLE IF NOT EXISTS plans (
--     created_at              TIMESTAMPTZ NOT NULL,
--     updated_at              TIMESTAMPTZ NOT NULL,
--     id                      UUID PRIMARY KEY,
--     key                     VARCHAR(50) NOT NULL, -- free|pro|enterprise
--     name                    VARCHAR(100) NOT NULL,
--     features                JSONB NOT NULL,
--     price_per_month         NUMERIC(10,2) NOT NULL CHECK (price_per_month >= 0),
--     version                 BIGINT NOT NULL,
-- );

-- CREATE UNIQUE INDEX IF NOT EXISTS ux_plans_key
--   ON plans(key);

-- CREATE INDEX IF NOT EXISTS idx_plans_features_gin ON plans USING GIN (features);

-- INSERT INTO plan_catalog (created_at, updated_at, key, name, features, price_per_month, version) VALUES
-- -- 1) Free / Community (cloud)
-- (now(), now(),
--  'free', 'Free',
--  '{
--   "description": "For individuals exploring SyncTurtle",
--   "limits": {
--     "workspaces": 1,
--     "members_per_workspace": 2,
--     "projects_per_workspace": 5,
--     "storage_gb": 1,
--     "automations_per_workspace": 2
--   },
--   "features": {
--     "custom_domains": false,
--     "ai_assist": false,
--     "webhooks": false,
--     "api_access": true,
--     "priority_support": false,
--     "audit_logs": false,
--     "retention_days": 30,
--     "sso_saml": false,
--     "scim": false,
--     "backups_daily": false
--   },
--   "labels": ["starter", "cloud"]
--  }'::jsonb, 0.00, 0),

-- -- 2) Personal (solo power-user)
-- (now(), now(),
--  'personal', 'Personal',
--  '{
--   "description": "Solo creators & power users",
--   "limits": {
--     "workspaces": 3,
--     "members_per_workspace": 1,
--     "projects_per_workspace": 50,
--     "storage_gb": 10,
--     "automations_per_workspace": 10
--   },
--   "features": {
--     "custom_domains": false,
--     "ai_assist": true,
--     "webhooks": true,
--     "api_access": true,
--     "priority_support": false,
--     "audit_logs": false,
--     "retention_days": 90,
--     "sso_saml": false,
--     "scim": false,
--     "backups_daily": true
--   },
--   "labels": ["individual", "cloud"]
--  }'::jsonb, 12.00, 0),

-- -- 3) Team (small teams)
-- (now(), now(),
--  'team', 'Team',
--  '{
--   "description": "Small teams shipping together",
--   "limits": {
--     "workspaces": 10,
--     "members_per_workspace": 25,
--     "projects_per_workspace": 200,
--     "storage_gb": 100,
--     "automations_per_workspace": 50
--   },
--   "features": {
--     "custom_domains": true,
--     "ai_assist": true,
--     "webhooks": true,
--     "api_access": true,
--     "priority_support": true,
--     "audit_logs": true,
--     "retention_days": 365,
--     "sso_saml": false,
--     "scim": false,
--     "backups_daily": true
--   },
--   "labels": ["teams", "cloud"]
--  }'::jsonb, 29.00, 0),

-- -- 4) Business (growing orgs)
-- (now(), now(),
--  'business', 'Business',
--  '{
--   "description": "Growing organizations that need controls",
--   "limits": {
--     "workspaces": 50,
--     "members_per_workspace": 200,
--     "projects_per_workspace": 1000,
--     "storage_gb": 1000,
--     "automations_per_workspace": 200
--   },
--   "features": {
--     "custom_domains": true,
--     "ai_assist": true,
--     "webhooks": true,
--     "api_access": true,
--     "priority_support": true,
--     "audit_logs": true,
--     "retention_days": 1095,
--     "sso_saml": true,
--     "scim": true,
--     "backups_daily": true
--   },
--   "labels": ["business", "cloud", "security"]
--  }'::jsonb, 79.00, 0),

-- -- 5) Enterprise (SSO/SCIM, audits, premium support)
-- (now(), now(),
--  'enterprise', 'Enterprise',
--  '{
--   "description": "Enterprises with compliance & scale needs",
--   "limits": {
--     "workspaces": 1000,
--     "members_per_workspace": 10000,
--     "projects_per_workspace": 100000,
--     "storage_gb": 10000,
--     "automations_per_workspace": 1000
--   },
--   "features": {
--     "custom_domains": true,
--     "ai_assist": true,
--     "webhooks": true,
--     "api_access": true,
--     "priority_support": true,
--     "audit_logs": true,
--     "retention_days": 3650,
--     "sso_saml": true,
--     "scim": true,
--     "backups_daily": true,
--     "dedicated_env": true,
--     "sla": "99.9%"
--   },
--   "labels": ["enterprise", "cloud", "compliance"]
--  }'::jsonb, 199.00, 0),

-- -- 6) OSS Self-Host (open-source LifeOS)
-- (now(), now(),
--  'oss-selfhost', 'OSS Self-Host',
--  '{
--   "description": "Community edition for self-hosting SyncTurtle",
--   "limits": {
--     "workspaces": 100,
--     "members_per_workspace": 1000,
--     "projects_per_workspace": 10000,
--     "storage_gb": 0,
--     "automations_per_workspace": 0
--   },
--   "features": {
--     "custom_domains": true,
--     "ai_assist": false,
--     "webhooks": true,
--     "api_access": true,
--     "priority_support": false,
--     "audit_logs": false,
--     "retention_days": 0,
--     "sso_saml": false,
--     "scim": false,
--     "backups_daily": false,
--     "license": "AGPL-3.0"
--   },
--   "labels": ["self-host", "oss"]
--  }'::jsonb, 0.00, 0)
-- ON CONFLICT (id) DO UPDATE SET
--   updated_at = EXCLUDED.updated_at,
--   key = EXCLUDED.key,
--   name = EXCLUDED.name,
--   features = EXCLUDED.features,
--   price_per_month = EXCLUDED.price_per_month,
--   version = EXCLUDED.version;
