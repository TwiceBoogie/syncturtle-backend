CREATE DATABASE "password";
CREATE DATABASE "user";
CREATE DATABASE "task";
CREATE DATABASE "user-test";
CREATE DATABASE "password-test";

CREATE ROLE vault_user WITH LOGIN PASSWORD 'Twice_Mina1' NOCREATEDB NOSUPERUSER CREATEROLE;

GRANT CONNECT ON DATABASE "password" TO vault_user;
GRANT CONNECT ON DATABASE "user" TO vault_user;
GRANT CONNECT ON DATABASE "task" TO vault_user;

\c password;

CREATE ROLE access_user_svc NOINHERIT;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO access_user_svc;

\c user;

CREATE ROLE access_password_svc NOINHERIT;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO access_password_svc;

\c task;

CREATE ROLE access_task_svc NOINHERIT;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO access_task_svc;