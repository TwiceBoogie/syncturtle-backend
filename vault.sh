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
        path "database/creds/password" {
          capabilities = ["read"]
        }
        path "sys/renew/*" {
          capabilities = ["update"]
        }' | vault policy write password-service -

  vault secrets enable database

  vault secrets enable transit

  vault write database/config/personavault-password-service \
    plugin_name=postgresql-database-plugin \
    allowed_roles=personavault-readonly \
    connection_url="postgresql://{{username}}:{{password}}@localhost:5432/password?sslmode=disable" \
    username="$POSTGRES_USERNAME" \
    password="$POSTGRES_PASSWORD"

  vault write database/roles/personavault-readonly \
    db_name=personavault-password-service \
    creation_statements=@rotate.sql \
    default_ttl=1h \
    max_ttl=24h
}