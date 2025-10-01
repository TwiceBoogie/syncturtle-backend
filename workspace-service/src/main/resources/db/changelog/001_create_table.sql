CREATE TABLE IF NOT EXISTS workspaces (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    instance_id             UUID NOT NULL,
    plan_id                 UUID NOT NULL, -- logical FK to instance-service.plans.id
    name                    VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise"
    slug                    VARCHAR(100) NOT NULL,
    organization_size       integer,
    owner_id                UUID NOT NULL, -- logicial FK to user-service.users.id
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID,
    deleted_at              TIMESTAMPTZ,
    -- settings JSONB NOT NULL DEFAULT '{}'::JSONB,
    -- FOREIGN KEY (instance_id) REFERENCES instances(id) ON DELETE CASCADE,
    -- FOREIGN KEY (plan_id) REFERENCES plans(id),
    UNIQUE(instance_id, slug)   
);

CREATE TABLE IF NOT EXISTS workspace_member_invites (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    workspace_id               UUID NOT NULL,
    email                   VARCHAR(255) NOT NULL,
    accepted                BOOLEAN NOT NULL,
    token                   VARCHAR(255) NOT NULL,
    message                 TEXT,
    responded_at            TIMESTAMPTZ,
    role                    SMALLINT NOT NULL,
    deleted_at              TIMESTAMPTZ,
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID,
    CONSTRAINT ck_workspace_invites_role CHECK (role >= 0),
    FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- enfore "one active invite per (workspace,email)"
CREATE UNIQUE INDEX IF NOT EXISTS ux_wi_unique_active
    ON workspace_member_invites(workspace_id, email)
    WHERE  deleted_at IS NULL;

-- hot paths lookups
CREATE INDEX IF NOT EXISTS idx_wi_workspace ON workspace_member_invites(workspace_id);
CREATE INDEX IF NOT EXISTS idx_wi_email_pending
    ON workspace_member_invites(email)
    WHERE accepted = FALSE AND deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS workspace_members (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    id                      UUID PRIMARY KEY,
    workspace_id            UUID NOT NULL,
    member_id               UUID NOT NULL, -- logical fk to user-service: users.id
    role                    SMALLINT NOT NULL,
    company_role            TEXT,
    is_active               BOOLEAN NOT NULL,
    deleted_at              TIMESTAMPTZ,
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID,
    CONSTRAINT ck_workspace_members_role CHECK ((role >= 0)),
    FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- enfore "one active membership per (workspace,user)"
CREATE UNIQUE INDEX IF NOT EXISTS ux_wm_unique_active
    ON workspace_members(workspace_id, member_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_wm_workspace ON workspace_members(workspace_id);
CREATE INDEX IF NOT EXISTS idx_wm_member ON workspace_members(member_id);
CREATE INDEX IF NOT EXISTS idx_wm_user_active ON workspace_members(member_id, is_active)
    WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS instances_view (
    id                      UUID PRIMARY KEY, -- mirrors instance-service.instances.id (canonical FK)
    slug                    VARCHAR(48) NOT NULL,
    edition                 VARCHAR(50) NOT NULL,
    version                 BIGINT NOT NULL, -- monotonic per-aggregate
    updated_at              TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS plans_view (
    id                      UUID PRIMARY KEY,
    version                 BIGINT NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL
);