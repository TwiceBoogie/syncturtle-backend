#!/usr/bin/env bash

start_vault_server() {
  echo "Starting vault server."
  vault server -dev -dev-root-token-id="$VAULT_TOKEN" &

  sleep 5

  echo 'path "secrets/password-service*" {
          capabilities = ["create", "read", "update", "delete", "list"]
        }
        path "secret/application*" {
          capabilities = ["create", "read", "update", "delete", "list"]
        }
        path "transit/decrypt/password" {
          capabilities = ["update"]
        }
        path "transit/encrypt/password" {
          capabilities = ["update"]
        }
        path "database/creds/password-service" {
          capabilities = ["read"]
        }
        path "sys/renew/*" {
          capabilities = ["update"]
        }' | vault policy write password-service -

  vault secrets enable database

  vault secrets enable rabbitmq

  vault secrets enable transit

  vault write rabbitmq/config/connection \
    connection_uri="http://localhost:15672" \
    username="$RABBITMQ_USERNAME" \
    password="$RABBITMQ_PASSWORD"

  vault write rabbitmq/roles/my-role \
    vhosts='{"/":{"write": ".*", "read": ".*"}}'


  vault write database/config/personavault-password-service \
    plugin_name=postgresql-database-plugin \
    allowed_roles=password-service \
    connection_url="postgresql://{{username}}:{{password}}@localhost:5432/password?sslmode=disable" \
    username="$POSTGRES_USERNAME" \
    password="$POSTGRES_PASSWORD"
# 16 days (384 hours)
  vault write database/roles/password-service \
    db_name=personavault-password-service \
    creation_statements=@rotate.sql \
    default_ttl=1h \
    max_ttl=384h

  vault write database/config/personavault-user-service \
    plugin_name=postgresql-database-plugin \
    allowed_roles=user-service \
    connection_url="postgresql://{{username}}:{{password}}@localhost:5432/user?sslmode=disable" \
    username="$POSTGRES_USERNAME" \
    password="$POSTGRES_PASSWORD"

  vault write database/roles/user-service \
    db_name=personavault-user-service \
    creation_statements=@rotate.sql \
    default_ttl=1h \
    max_ttl=384h

  vault write database/config/personavault-task-service \
    plugin_name=postgresql-database-plugin \
    allowed_roles=task-service \
    connection_url="postgresql://{{username}}:{{password}}@localhost:5432/task?sslmode=disable" \
    username="$POSTGRES_USERNAME" \
    password="$POSTGRES_PASSWORD"

  vault write database/roles/task-service \
    db_name=personavault-task-service \
    creation_statements=@rotate.sql \
    default_ttl=1h \
    max_ttl=384h
}