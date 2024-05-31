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
  vault server -dev -dev-root-token-id="$VAULT_TOKEN" -dev-listen-address="0.0.0.0:8200" -config=setup.hcl &

  sleep 5
#  vault_pid=$(pgrep -f "vault server -dev -dev-root-token-id=$VAULT_TOKEN")
#  echo "Vault server PID: $vault_pid"
  vault secrets enable -path=secret kv-v2

  vault policy write user-service-policy ./policies/user-service-policy.hcl
  vault policy write email-service-policy ./policies/email-service-policy.hcl
  vault policy write password-service-policy ./policies/password-service-policy.hcl
  vault auth enable approle

  vault write auth/approle/role/user-service token_ttl=1h token_max_ttl=4h token_policies="user-service-policy"
  vault write auth/approle/role/email-service token_ttl=1h token_max_ttl=4h token_policies="email-service-policy"
  vault write auth/approle/role/password-service token_ttl=1h token_max_ttl=4h token_policies="password-service-policy"

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