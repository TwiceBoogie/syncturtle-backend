-- Create databases
CREATE DATABASE "instance";
CREATE DATABASE "password";
CREATE DATABASE "user";
CREATE DATABASE "task";
CREATE DATABASE "user-test";
CREATE DATABASE "password-test";

-- Create roles
CREATE ROLE liquibase_user LOGIN PASSWORD 'secure_liquibase_pw' NOSUPERUSER CREATEDB;
CREATE ROLE vault_user LOGIN PASSWORD 'secure_vault_pw' NOSUPERUSER CREATEROLE;
CREATE ROLE vault_access NOLOGIN;

-- Grant Vault access to connect to target databases
GRANT CONNECT ON DATABASE "password" TO vault_user;
GRANT CONNECT ON DATABASE "user" TO vault_user;
GRANT CONNECT ON DATABASE "task" TO vault_user;
GRANT CONNECT ON DATABASE "instance" TO vault_user;

-- Make liquibase_user the owner of each database
ALTER DATABASE "password" OWNER TO liquibase_user;
ALTER DATABASE "user" OWNER TO liquibase_user;
ALTER DATABASE "task" OWNER TO liquibase_user;
ALTER DATABASE "instance" OWNER TO liquibase_user;

-- For future sequences
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user
IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO vault_access;

-- For any sequences that might already exist
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO vault_access;

-- For future tables
ALTER DEFAULT PRIVILEGES FOR ROLE liquibase_user
IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO vault_access;

-- Give vault_user permission to GRANT vault_access to others
-- ✅ Allows vault_user to GRANT vault_access to others
GRANT vault_access TO vault_user WITH ADMIN OPTION;