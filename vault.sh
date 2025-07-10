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

generate_secret_fetch_key() {
  local secret_key
  secret_key=$(openssl rand -base64 32)

  echo "Generated new SECRET_FETCH_KEY"
  echo "Injecting into Vault and .env"

  # Inject into Vault
  vault kv put secret/application SECRET_FETCH_KEY="$secret_key"

  local env_file="./web/.env"

  if [ ! -f "$env_file" ]; then
    echo "Creating new .env file"
    touch "$env_file"
  fi

  # Detect OS to use the correct sed syntax
  if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS (BSD sed)
    if grep -q "^SECRET_FETCH_KEY=" "$env_file"; then
      sed -i '' "s/^SECRET_FETCH_KEY=.*/SECRET_FETCH_KEY=$secret_key/" "$env_file"
    else
      echo "SECRET_FETCH_KEY=$secret_key" >> "$env_file"
    fi
  else
    # Linux (GNU sed)
    if grep -q "^SECRET_FETCH_KEY=" "$env_file"; then
      sed -i "s/^SECRET_FETCH_KEY=.*/SECRET_FETCH_KEY=$secret_key/" "$env_file"
    else
      echo "SECRET_FETCH_KEY=$secret_key" >> "$env_file"
    fi
  fi
}

start_vault_server() {
  echo "Starting vault server."
  local vault_cmd;
  if [ "$os_name" = "linux" ]; then
    vault_cmd="./vault/vault"
  else
    vault_cmd="vault"
  fi
  export VAULT_ADDR="http://127.0.0.1:8200"
  eval $vault_cmd secrets enable -path=secret kv-v2

  eval $vault_cmd policy write user-service-policy ./policies/user-service-policy.hcl
  eval $vault_cmd policy write email-service-policy ./policies/email-service-policy.hcl
  eval $vault_cmd policy write password-service-policy ./policies/password-service-policy.hcl
  eval $vault_cmd auth enable approle

  eval $vault_cmd write auth/approle/role/user-service token_ttl=1h token_max_ttl=4h token_policies="user-service-policy"
  eval $vault_cmd write auth/approle/role/email-service token_ttl=1h token_max_ttl=4h token_policies="email-service-policy"
  eval $vault_cmd write auth/approle/role/password-service token_ttl=1h token_max_ttl=4h token_policies="password-service-policy"

  eval $vault_cmd secrets enable database

  eval $vault_cmd secrets enable rabbitmq

  eval $vault_cmd secrets enable transit

  eval $vault_cmd write rabbitmq/config/connection \
    connection_uri="http://localhost:15672" \
    username="$RABBITMQ_USERNAME" \
    password="$RABBITMQ_PASSWORD"

  $vault_cmd write rabbitmq/roles/my-role \
    vhosts='{"/":{"write": ".*", "read": ".*"}}'

  for (( i = 0; i < ${#configurations[@]}; i++ )); do
      configuration="${configurations[$i]}"
      role="${roles[$i]}"
      database=$(echo "$role" | cut -d '-' -f 1)

      # Replace placeholder in rotate.sql with actual role name
      sed "s/%ACCESS_ROLE%/${sql_roles[$i]}/g" rotate.sql > temp_rotate.sql

      eval $vault_cmd write database/config/"$configuration" \
        plugin_name=postgresql-database-plugin \
        allowed_roles="$role" \
        connection_url="postgresql://{{username}}:{{password}}@localhost:5432/$database?sslmode=disable" \
        username="$POSTGRES_USERNAME" \
        password="$POSTGRES_PASSWORD"

      eval $vault_cmd write database/roles/"$role" \
        db_name="$configuration" \
        creation_statements=@temp_rotate.sql \
        default_ttl=1h \
        max_ttl=384h

      # Remove temp file
      rm temp_rotate.sql
  done
  generate_secret_fetch_key()
}