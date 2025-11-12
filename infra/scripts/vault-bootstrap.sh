#!/bin/sh
set -eu

# ":?" check if variable is set and not
: "${VAULT_ADDR:? set VAULT_ADDR http://vault:8200}"
: "${VAULT_DEV_ROOT_TOKEN_ID:? set VAULT_DEV_ROOT_TOKEN_ID}"
: "${RABBITMQ_USERNAME:? set RABBITMQ_USERNAME}"
: "${RABBITMQ_PASSWORD:? set RABBITMQ_PASSWORD}"
: "${VAULT_DB_USER:? SET VAULT_DB_USER}"
: "${VAULT_DB_PASS:? SET VAULT_DB_PASS}"

VAULT_TOKEN="${VAULT_DEV_ROOT_TOKEN_ID}"
HDR_TK="X-Vault-Token: ${VAULT_TOKEN}"
HDR_CT="Content-Type: application/json"

vget() {
    curl -sS -X GET "${VAULT_ADDR}$1" -H "${HDR_TK}"
}
vpost() {
    path="$1" ; body="$2"
    curl -sS -X POST "${VAULT_ADDR}${path}" -H "${HDR_TK}" -H "${HDR_CT}" -d "$body"
}
vput() {
    path="$1" ; body="$2"
    curl -sS -X PUT "${VAULT_ADDR}${path}" -H "${HDR_TK}" -H "${HDR_CT}" -d "$body"
}

enable_mount() {
    path="$1" ; type="$2"
    # POST /v1/sys/mounts/<path> {"type": "<type>"}
    if ! vpost "/v1/sys/mounts/${path}" "{\"type\":\"${type}\"}" >/dev/null 2>&1; then
        echo "Mount ${path} may already exist, continuing...\n"
    fi
}

enable_auth() {
    path="$1" ; type="$2"
    # POST /v1/sys/auth/<path> {"type": "<type>"}
    if ! vpost "/v1/sys/auth/${path}" "{\"type\":\"${type}\"}" >/dev/null 2>&1; then
        echo "Auth ${path} may already exist, continuing...\n"
    fi
}

echo "Waiting for vault to finish and be healthy at $VAULT_ADDR ..."
until vget "/v1/sys/health" | grep -q '"initialized":true'; do
    echo "sleeping"
    sleep 1
done
echo "Vault is ready, continuing \\n"

echo "Enabling engines + approle"
enable_mount "database" "database"
enable_mount "rabbitmq" "rabbitmq"
enable_mount "transit" "transit"
enable_auth "approle" "approle"

echo "Waiting for rabbitmq to be ready."
until curl -fsS "http://rabbitmq:15672/api/overview" -u "${RABBITMQ_USERNAME}:${RABBITMQ_PASSWORD}" | grep -q "rabbitmq_version"; do
    sleep 3
done
echo "Rabbitmq is ready, continuing \n"

# POST /v1/rabbitmq/config/connection
vpost "/v1/rabbitmq/config/connection" "$(jq -n --arg uri "http://rabbitmq:15672" \
    --arg u "${RABBITMQ_USERNAME}" --arg p "${RABBITMQ_PASSWORD}" \
    '{connection_uri:$uri, username:$u, password:$p}')" >/dev/null || echo "rabbitmq connection exists.\n"

vpost "/v1/rabbitmq/roles/my-role" '{
    "vhosts": { "/": { "write": ".*", "read": ".*" } }
}' >/dev/null || echo "rabbitmq role exists.\n"

configurations="syncturtle-instance-service syncturtle-password-service syncturtle-user-service syncturtle-task-service"
roles="instance-service password-service user-service task-service email-service api-gateway"

i=0
for role in $roles; do
    echo "configuring policy and approle for $role..."

    policy_file="/vault/policies/${role}-policy.hcl"
    if [ -r "$policy_file" ]; then
        policy_json=$(jq -Rs . "$policy_file") || {
            echo "ERROR: failed to read policy file: $policy_file"; exit 1;
        }

        vput "/v1/sys/policies/acl/${role}-policy" "{\"policy\": ${policy_json}}" >/dev/null || \
            echo "policy ${role}-policy may already exist, continuing...\\n"
    else
        echo "WARNING: policy file missing: $policy_file (continuing)\n"
    fi

    vpost "/v1/auth/approle/role/${role}" '{
        "token_ttl": "1h",
        "token_max_ttl": "8h",
        "secret_id_num_uses": 0,
        "token_policies": ["'"${role}"'-policy"]
    }' >/dev/null || echo "approle ${role} may already exist, continuing...\n"

    # database roles
    if [ "$role" != "api-gateway" ] && [ "$role" != "email-service" ]; then
        configuration=$(echo "$configurations" | awk -v idx="$((i + 1))" '{ print $idx }')
        database=$(echo "$role" | cut -d '-' -f 1)

        echo "Configuring DB engine + role for ${role} (DB: ${database})..."

        tmp_sql="/tmp/${role}_rotate.sql"
        sed "s/%DATABASE%/${database}/g" "/vault/sql/rotate.sql" > "$tmp_sql"

        # POST /v1/database/config/<name>
        vpost "/v1/database/config/${configuration}" "$(jq -n \
            --arg plugin "postgresql-database-plugin" \
            --arg allowed "$role" \
            --arg url "postgresql://{{username}}:{{password}}@ms-postgres:5432/${database}?sslmode=disable" \
            --arg user "$VAULT_DB_USER" \
            --arg pass "$VAULT_DB_PASS" \
            '{plugin_name:$plugin, allowed_roles:$allowed, connection_url:$url, username:$user, password:$pass}')" \
            >/dev/null || echo "db config ${configuration} may already exist, continuing...\n"

        # POST /v1/database/roles/<role>
        vpost "/v1/database/roles/${role}" "$(jq -n \
            --arg db "${configuration}" \
            --rawfile stmt "$tmp_sql" \
            '{db_name:$db, creation_statements:$stmt, default_ttl:"1h", max_ttl:"384h"}')" \
            >/dev/null || echo "db role ${role} may already exist, continuing...\n"
    fi
    i=$((i + 1))
done

echo "Creating role_id"

mkdir -p ./vault/secrets; chmod 700 ./vault/secrets
get_role_id() {
    local name="$1"
    vget "/v1/auth/approle/role/${name}/role-id" | jq -r .data.role_id
}
create_secret_id() {
    local name="$1"
    vpost "/v1/auth/approle/role/${name}/secret-id" '{}' | jq -r .data.secret_id
}

for role in $roles; do
    upper="$(echo "$role" | tr '[:lower:]-' '[:upper:]_')"
    rid="$(get_role_id "$role")"
    sid="$(create_secret_id "$role")"

    printf '%s' "$rid" > "./vault/secrets/VAULT_${upper}_ROLE_ID"
    printf '%s' "$sid" > "./vault/secrets/VAULT_${upper}_SECRET_ID"
    chmod 600 "./vault/secrets/VAULT_${upper}_ROLE_ID" "/vault/secrets/VAULT_${upper}_SECRET_ID"
    echo "Wrote ./secrets/VAULT_${upper}_ROLE_ID and ..._SECRET_ID"
done

echo "Boostrap (HTTP API) complete."
