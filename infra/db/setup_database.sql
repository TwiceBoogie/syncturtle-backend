\set ON_ERROR_STOP on

-- 1) Roles (idempotent)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'liquibase_user') THEN
    CREATE ROLE liquibase_user LOGIN PASSWORD 'secure_liquibase_pw' NOSUPERUSER CREATEDB INHERIT;
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'vault_user') THEN
    CREATE ROLE vault_user LOGIN PASSWORD 'secure_vault_pw' NOSUPERUSER CREATEROLE INHERIT;
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'vault_access') THEN
    CREATE ROLE vault_access NOLOGIN INHERIT;
  END IF;
END$$;

-- 2) Databases (will error if exist; run once or ignore errors)
--    If you re-run frequently, create these outside or wrap with a DO+dblink block.
CREATE DATABASE "instance";
CREATE DATABASE "workspace";
CREATE DATABASE "password";
CREATE DATABASE "user";
CREATE DATABASE "task";
CREATE DATABASE "user-test";
CREATE DATABASE "password-test";

-- 3) Make liquibase_user the OWNER of each database
ALTER DATABASE "instance" OWNER TO liquibase_user;
ALTER DATABASE "workspace" OWNER TO liquibase_user;
ALTER DATABASE "password" OWNER TO liquibase_user;
ALTER DATABASE "user"     OWNER TO liquibase_user;
ALTER DATABASE "task"     OWNER TO liquibase_user;
ALTER DATABASE "user-test" OWNER TO liquibase_user;
ALTER DATABASE "password-test" OWNER TO liquibase_user;

-- 4) Let vault_user act as grantor of vault_access to dynamic users
GRANT vault_access TO vault_user WITH ADMIN OPTION;

-- === Per-database block (repeat) ===========================================
-- WORKSPACE_DB
\connect "workspace"

ALTER SCHEMA public OWNER TO liquibase_user;

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT  CREATE, USAGE ON SCHEMA public TO liquibase_user;
GRANT  USAGE ON SCHEMA public TO vault_access;

-- If you prefer not to change owner, at least:
-- GRANT USAGE, CREATE ON SCHEMA public TO liquibase_user;

-- Runtime role access to DB & schema (inherited by dynamic users via vault_access)
GRANT CONNECT ON DATABASE "workspace" TO vault_access;
GRANT USAGE  ON SCHEMA public   TO vault_access;

-- Backfill grants for any existing objects (safe if none yet)
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO vault_access;
GRANT USAGE, SELECT                 ON ALL SEQUENCES IN SCHEMA public TO vault_access;

-- Default privileges for FUTURE objects created by liquibase_user
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;

ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO vault_access;
  
-- USER DB
\connect "user"

-- Schema ownership (so liquibase_user can freely create/manage in public)
ALTER SCHEMA public OWNER TO liquibase_user;

-- By default, Postgres grants CREATE on public to PUBLIC. 
-- Lock it down so only liquibase_user can create in public, and vault_access can only use it
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT  CREATE, USAGE ON SCHEMA public TO liquibase_user;
GRANT  USAGE ON SCHEMA public TO vault_access;

-- If you prefer not to change owner, at least:
-- GRANT USAGE, CREATE ON SCHEMA public TO liquibase_user;

-- Runtime role access to DB & schema (inherited by dynamic users via vault_access)
GRANT CONNECT ON DATABASE "user" TO vault_access;
GRANT USAGE  ON SCHEMA public   TO vault_access;

-- Backfill grants for any existing objects (safe if none yet)
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO vault_access;
GRANT USAGE, SELECT                 ON ALL SEQUENCES IN SCHEMA public TO vault_access;

-- Default privileges for FUTURE objects created by liquibase_user
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;

ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO vault_access;

-- PASSWORD DB
\connect "password"
ALTER SCHEMA public OWNER TO liquibase_user;

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT  CREATE, USAGE ON SCHEMA public TO liquibase_user;
GRANT  USAGE ON SCHEMA public TO vault_access;

GRANT CONNECT ON DATABASE "password" TO vault_access;
GRANT USAGE  ON SCHEMA public        TO vault_access;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO vault_access;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA public TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO vault_access;

-- TASK DB
\connect "task"
ALTER SCHEMA public OWNER TO liquibase_user;

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT  CREATE, USAGE ON SCHEMA public TO liquibase_user;
GRANT  USAGE ON SCHEMA public TO vault_access;

GRANT CONNECT ON DATABASE "task" TO vault_access;
GRANT USAGE  ON SCHEMA public    TO vault_access;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO vault_access;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA public TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO vault_access;

-- INSTANCE DB
\connect "instance"
ALTER SCHEMA public OWNER TO liquibase_user;

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT  CREATE, USAGE ON SCHEMA public TO liquibase_user;
GRANT  USAGE ON SCHEMA public TO vault_access;

GRANT CONNECT ON DATABASE "instance" TO vault_access;
GRANT USAGE  ON SCHEMA public        TO vault_access;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO vault_access;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA public TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO vault_access;

-- (Optional) test DBs same way...
-- \connect "user-test"
-- (repeat grants & defaults)
-- \connect "password-test"
-- (repeat grants & defaults)
