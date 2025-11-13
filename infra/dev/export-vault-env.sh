#!/usr/bin/env sh
set -eu
svc="$1" # e.g. API_GATEWAY / USER_SERVICE (UPPER_SNAKE)
base="/workspace/secrets"
rid="${base}/VAULT_${svc}_ROLE_ID"
sid="${base}/VAULT_${svc}_SECRET_ID"
[ -r "$rid" ] || { echo "Missing $rid" >&2; exit 1; }
[ -r "$sid" ] || { echo "Missing $sid" >&2; exit 1; }
export "VAULT_${svc}_ROLE_ID=$(cat "$rid")"
export "VAULT_${svc}_SECRET_ID=$(cat "$sid")"
exec "$@"