ALTER TABLE login_policies
    ADD COLUMN is_default BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE login_policies
SET is_default = (policy_name = 'Default Policy');
CREATE UNIQUE INDEX uq_login_attempt_policy_default
    ON login_policies(is_default) WHERE is_default;