#!/usr/bin/env bash

configurations=(
  "personavault-password-service"
  "personavault-user-service"
  "personavault-task-service"
)
roles=(
  "password-service"
  "user-service"
  "task-service"
)
sql_roles=(
  "access_password_svc"
  "access_user_svc"
  "access_task_svc"
)

start_vault_server() {
  echo "Starting vault server."
  vault server -dev -dev-root-token-id="$VAULT_TOKEN" &

  sleep 5
#  vault_pid=$(pgrep -f "vault server -dev -dev-root-token-id=$VAULT_TOKEN")
#  echo "Vault server PID: $vault_pid"


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

  for (( i = 0; i < ${#configurations[@]}; i++ )); do
      configuration="${configurations[$i]}"
      role="${roles[$i]}"
      database=$(echo "$role" | cut -d '-' -f 1)

      # Replace placeholder in rotate.sql with actual role name
      sed "s/%ACCESS_ROLE%/${sql_roles[$i]}/g" rotate.sql > temp_rotate.sql

      vault write database/config/"$configuration" \
        plugin_name=postgresql-database-plugin \
        allowed_roles="$role" \
        connection_url="postgresql://{{username}}:{{password}}@localhost:5432/$database?sslmode=disable" \
        username="$POSTGRES_USERNAME" \
        password="$POSTGRES_PASSWORD"

      vault write database/roles/"$role" \
        db_name="$configuration" \
        creation_statements=@temp_rotate.sql \
        default_ttl=1h \
        max_ttl=384h

      # Remove temp file
      rm temp_rotate.sql
  done

}