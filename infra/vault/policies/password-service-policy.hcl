# vault kv secrets

# Full access to shared secrets like SECRET_FETCH_KEY
path "secret/data/application" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Full access to service-specific secrets
path "secret/data/password-service" {
  capabilities = ["read", "list"]
}

path "secret/data/password-service/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# vault transit

# Allow encryption using the 'password' key in transit
path "transit/encrypt/password" {
  capabilities = ["update"]
}

# Allow decryption using the 'password' key in transit
path "transit/decrypt/password" {
  capabilities = ["update"]
}

# db creds

# Allow retrieving temporary Postgres credentials
path "database/creds/password-service" {
  capabilities = ["read"]
}

# approle auth

# AppRole login
path "auth/approle/login" {
  capabilities = ["create", "read"]
}

# Read the Role ID for password-service
path "auth/approle/role/password-service/role-id" {
  capabilities = ["read"]
}

# Create and read Secret ID
path "auth/approle/role/password-service/secret-id" {
  capabilities = ["create", "read"]
}

# Allow token renewal (optional)
path "sys/renew/*" {
  capabilities = ["update"]
}

# rabbitmq

# Read generated RabbitMQ credentials for a shared role
path "rabbitmq/creds/my-role" {
  capabilities = ["read"]
}