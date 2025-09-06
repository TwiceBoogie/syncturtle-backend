ALTER TABLE instances
    RENAME COLUMN current_version TO app_current_version,
    RENAME COLUMN latest_version TO app_latest_version,
    RENAME COLUMN last_checked_at TO app_last_checked_at,
    ADD COLUMN IF NOT EXISTS config_version BIGINT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS config_last_checked_at TIMESTAMPTZ;
