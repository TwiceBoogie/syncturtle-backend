#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

env_file="${PROJECT_ROOT}/.env"

echo "Loading and checking environment variables from .env..."
set -a
source "${env_file}"
set +a

roles=(
    "instance-service"
    "password-service"
    "user-service"
    "task-service"
    "api-gateway"
    "email-service"
)

generate_secret_fetch_key() {
    local secret_key
    secret_key=$(openssl rand -base64 32)
    echo "Generated new SECRET_FETCH_KEY"

    export VAULT_TOKEN="${VAULT_DEV_ROOT_TOKEN_ID}"
    export VAULT_ADDR="${VAULT_ADDR:-http://localhost:8200}"

    vault kv put secret/application SECRET_FETCH_KEY="$secret_key"

    local env_file="$PROJECT_ROOT/web/.env"
    [ ! -f "$env_file" ] && touch "$env_file"

    escaped_secret_key=$(printf '%s\n' "$secret_key" | sed 's/[\/&]/\\&/g')

    if grep -q "^SECRET_FETCH_KEY=" "$env_file"; then
    sed -i '' -e "s/^SECRET_FETCH_KEY=.*/SECRET_FETCH_KEY=$escaped_secret_key/" "$env_file"
    else
    echo "SECRET_FETCH_KEY=$escaped_secret_key" >> "$env_file"
    fi
}

set_env_var() {
    local key="$1"
    local value="$2"

    if grep -q "^${key}=" "$env_file"; then
        # replace existing line
        sed -i '' "s|^${key}=.*|${key}=${value}|" "$env_file"
    else
        # append if not found
        echo "${key}=${value}" >> "$env_file"
    fi
}

handleEnvFile() {
    echo "Fetching Vault AppRole credentials..."

    for role in "${roles[@]}"; do
        uppercase_role=$(echo "$role" | tr '[:lower:]' '[:upper:]' | tr '-' '_')

        VAULT_ROLE_ID=$(curl -s \
            --header "X-Vault-Token: ${VAULT_DEV_ROOT_TOKEN_ID}" \
            "${VAULT_ADDR}/v1/auth/approle/role/${role}/role-id" | jq -r .data.role_id)

        VAULT_SECRET_ID=$(curl -s --request POST \
            --header "X-Vault-Token: ${VAULT_DEV_ROOT_TOKEN_ID}" \
            "${VAULT_ADDR}/v1/auth/approle/role/${role}/secret-id" | jq -r .data.secret_id)
        
        set_env_var "VAULT_${uppercase_role}_ROLE_ID" "${VAULT_ROLE_ID}"
        set_env_var "VAULT_${uppercase_role}_SECRET_ID" "${VAULT_SECRET_ID}"
    done

    echo ".env file updated with Vault credentials for roles."
}

generate_secret_fetch_key
handleEnvFile