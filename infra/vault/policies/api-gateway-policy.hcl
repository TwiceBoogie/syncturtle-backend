# vaulkt vk secrets

# Allow full access to shared application-level secrets
path "secret/data/application" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Allow full access to this service's own secrets
path "secret/data/api-gateway" {
  capabilities = ["read", "list"]
}

path "secret/data/api-gateway*" {
    capabilities = ["create", "read", "update", "delete", "list"]
}

# approle auth

# Allow login via AppRole
path "auth/approle/login" {
  capabilities = ["create", "read"]
}

# Allow reading Role ID
path "auth/approle/role/api-gateway/role-id" {
  capabilities = ["read"]
}

# Allow creating and reading Secret ID
path "auth/approle/role/api-gateway/secret-id" {
  capabilities = ["create", "read"]
}

# Allow token renewal (for longer running tasks, optional)
path "sys/renew/*" {
  capabilities = ["update"]
}