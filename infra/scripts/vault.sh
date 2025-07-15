#!/usr/bin/env bash

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

configurations=(
  "syncturtle-instance-service"
  "synturtle-password-service"
  "synturtle-user-service"
  "synturtle-task-service"
)
roles=(
  "instance-service"
  "password-service"
  "user-service"
  "task-service"
)

generate_secret_fetch_key() {
  local secret_key
  secret_key=$(openssl rand -base64 32)
  echo "Generated new SECRET_FETCH_KEY"

  vault kv put secret/application SECRET_FETCH_KEY="$secret_key"

  local env_file="$PROJECT_ROOT/web/.env"
  [ ! -f "$env_file" ] && touch "$env_file"

  if grep -q "^SECRET_FETCH_KEY=" "$env_file"; then
    sed -i'' -e "s/^SECRET_FETCH_KEY=.*/SECRET_FETCH_KEY=$secret_key/" "$env_file"
  else
    echo "SECRET_FETCH_KEY=$secret_key" >> "$env_file"
  fi
}

start_vault_setup() {
  echo "Starting Vault server..."
  local vault_cmd="vault"
  [ "${os_name:-}" = "linux" ] && vault_cmd="./vault/vault"

  export VAULT_ADDR="http://127.0.0.1:8200"

  # Enable secrets engines
  vault secrets enable database || true
  vault secrets enable rabbitmq || true
  vault secrets enable transit || true

  # enable pki secrets ad config
  # vault secrets enable pki || true
  # vault secrets tune -max-lease-ttl=87600h pki

  # vault write pki/config/urls \
  # issuing_certificates="https://vault.internal:8200/v1/pki/ca" \
  # crl_distribution_points="https://vault.internal:8200/v1/pki/crl"

  # vault write pki/roles/syncturtle \
  #   allowed_domains="twiceb.internal" \
  #   allow_subdomains=true \
  #   max_ttl="72h"


  # Enable auth methods
  vault auth enable approle || true

  # RabbitMQ
  vault write rabbitmq/config/connection \
    connection_uri="http://localhost:15672" \
    username="$RABBITMQ_USERNAME" \
    password="$RABBITMQ_PASSWORD"
  vault write rabbitmq/roles/my-role \
    vhosts='{"/":{"write": ".*", "read": ".*"}}'

  # Database config
  for (( i = 0; i < ${#configurations[@]}; i++ )); do
    configuration="${configurations[$i]}"
    role="${roles[$i]}"
    database=$(echo "$role" | cut -d '-' -f 1)

    echo "Configuring Vault policies for $role"
    # policies
    vault policy write "${role}-policy" "${PROJECT_ROOT}/infra/vault/policies/${role}-policy.hcl"
    # approles
    vault write auth/approle/role/$role \
      token_ttl=1h token_max_ttl=8h token_policies="${role}-policy"

    echo "Configuring Vault DB role for ${role} (DB: ${database})"

    # Generate dynamic SQL with {{name}} interpolation
    # Use double quotes around the {database} name to avoid SQL syntax issues
    template_file="$PROJECT_ROOT/infra/db/rotate.sql"
    temp_sql="$PROJECT_ROOT/infra/db/temp_rotate.sql"

    sed "s/%DATABASE%/${database}/g" "${template_file}" > "${temp_sql}"

    vault write database/config/"${configuration}" \
      plugin_name=postgresql-database-plugin \
      allowed_roles="${role}" \
      connection_url="postgresql://{{username}}:{{password}}@localhost:5432/${database}?sslmode=disable" \
      username="${VAULT_DB_USER}" \
      password="${VAULT_DB_PASS}"

    vault write database/roles/"${role}" \
      db_name="${configuration}" \
      creation_statements=@"${temp_sql}" \
      default_ttl=1h \
      max_ttl=384h

    rm "${temp_sql}"
  done

  generate_secret_fetch_key
}