#!/usr/bin/env sh
set -euo pipefail

echo "Starting Vault server in dev mode..."
vault server -dev -dev-root-token-id="${VAULT_DEV_ROOT_TOKEN_ID}" -dev-listen-address="${VAULT_DEV_LISTEN_ADDRESS}" &
VAULT_PID=$!

export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN="${VAULT_DEV_ROOT_TOKEN_ID}"

echo "Waiting for Vault.."
until curl -s $VAULT_ADDR/v1/sys/health | grep -q '"initialized":true'; do
    sleep 1
done

echo "Vault is ready. bootstrapping..."

configurations="syncturtle-instance-service syncturtle-password-service syncturtle-user-service syncturtle-task-service"
roles="instance-service password-service user-service task-service email-service api-gateway"

vault secrets enable database || true
vault secrets enable rabbitmq || true
vault secrets enable transit || true

vault auth enable approle || true

echo "⏳ Waiting for RabbitMQ to be ready..."
until curl -s "http://rabbitMQ:15672/api/overview" -u "${RABBITMQ_USERNAME}:${RABBITMQ_PASSWORD}" | grep -q "rabbitmq_version"; do
  echo "Waiting for RabbitMQ management API..."
  sleep 3
done

vault write rabbitmq/config/connection \
    connection_uri="http://rabbitMQ:15672" \
    username="${RABBITMQ_USERNAME}" \
    password="${RABBITMQ_PASSWORD}"
vault write rabbitmq/roles/my-role \
    vhosts='{"/":{"write": ".*", "read": ".*"}}'

i=0
for role in $roles; do
  echo "🔐 Configuring policy and AppRole for $role..."

  vault policy write "${role}-policy" "/vault/policies/${role}-policy.hcl"

  vault write auth/approle/role/$role \
    token_ttl=1h \
    token_max_ttl=8h \
    token_policies="${role}-policy"

  if [ "$role" != "api-gateway" ] && [ "$role" != "email-service" ]; then
    configuration=$(echo "$configurations" | awk -v idx="$((i + 1))" '{ print $idx }')
    database=$(echo "$role" | cut -d '-' -f 1)

    echo "📦 Configuring Vault DB role for ${role} (DB: ${database})..."

    sed "s/%DATABASE%/${database}/g" "/vault/sql/rotate.sql" > "/tmp/temp_rotate.sql"

    vault write database/config/"${configuration}" \
      plugin_name=postgresql-database-plugin \
      allowed_roles="${role}" \
      connection_url="postgresql://{{username}}:{{password}}@ms-postgres:5432/${database}?sslmode=disable" \
      username="${VAULT_DB_USER}" \
      password="${VAULT_DB_PASS}"

    vault write database/roles/"${role}" \
      db_name="${configuration}" \
      creation_statements=@"/tmp/temp_rotate.sql" \
      default_ttl=1h \
      max_ttl=384h
  fi

  i=$((i + 1))
done

wait "$VAULT_PID"