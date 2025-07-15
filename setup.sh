#!/usr/bin/env bash

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TLS_PATH="$PROJECT_ROOT/infra/vault/tls"

cat > "$PROJECT_ROOT/infra/vault/setup/setup.hcl" <<EOF
ui = true

backend "inmem" {}

listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_cert_file = "$TLS_PATH/vault.cert.pem"
  tls_key_file  = "$TLS_PATH/vault.key.pem"
}

plugin_directory = "plugins"
api_addr = "https://127.0.0.1:8200"
disable_mlock = true
EOF
