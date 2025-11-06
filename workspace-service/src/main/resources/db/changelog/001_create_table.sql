CREATE TABLE IF NOT EXISTS workspaces (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    deleted_at              TIMESTAMPTZ,
    id                      UUID PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL, -- e.g. "SyncTurtle Enterprise"
    slug                    VARCHAR(100) NOT NULL,
    organization_size       INTEGER,
    -- logicial FK to user-service.users.id
    owner_id                UUID NOT NULL,
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID
);

CREATE TABLE IF NOT EXISTS workspace_member_invites (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    deleted_at              TIMESTAMPTZ,
    id                      UUID PRIMARY KEY,
    workspace_id            UUID NOT NULL,
    email                   VARCHAR(255) NOT NULL,
    accepted                BOOLEAN NOT NULL,
    token                   VARCHAR(255) NOT NULL,
    message                 TEXT,
    responded_at            TIMESTAMPTZ,
    role                    SMALLINT NOT NULL CHECK (role >= 0),
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID,
    FOREIGN KEY (workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED
);

-- enfore "one active invite per (workspace,email)"
CREATE UNIQUE INDEX IF NOT EXISTS ux_wi_unique_active
    ON workspace_member_invites(workspace_id, email)
    WHERE deleted_at IS NULL;

-- hot paths lookups
CREATE INDEX IF NOT EXISTS idx_wi_workspace ON workspace_member_invites(workspace_id);
CREATE INDEX IF NOT EXISTS idx_wi_email_pending
    ON workspace_member_invites(email)
    WHERE accepted = FALSE AND deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS workspace_members (
    created_at              TIMESTAMPTZ NOT NULL,
    updated_at              TIMESTAMPTZ NOT NULL,
    deleted_at              TIMESTAMPTZ,
    id                      UUID PRIMARY KEY,
    workspace_id            UUID NOT NULL,
    -- logical fk to user-service: users.id
    member_id               UUID NOT NULL,
    role                    SMALLINT NOT NULL CHECK (role >= 0),
    company_role            TEXT,
    is_active               BOOLEAN NOT NULL,
    -- optional aduit (logical)
    created_by              UUID,
    updated_by              UUID,
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