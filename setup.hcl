ui=true

backend "inmem" {
}

listener "tcp" {
  address = "0.0.0.0:8200"
  tls_cert_file = "work/ca/intermediate/certs/vault.cert.pem"
  tls_key_file = "work/ca/intermediate/private/vault.key.pem"
}

plugin_directory = "plugins"
api_addr = "https://127.0.0.1:8200"
disable_mlock = true