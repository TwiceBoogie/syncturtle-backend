path "secret/data/user-service*" {
    capabilities = ["create", "read", "update", "delete", "list"]
}
path "secret/data/application*" {
    capabilities = ["create", "read", "update", "delete", "list"]
}
path "database/creds/user-service" {
    capabilities = ["read"]
}
path "sys/renew/*" {
    capabilities = ["update"]
}
# Allow creating, reading, and listing tokens for approle authentication method
path "auth/approle/login" {
  capabilities = ["create", "read", "list"]
}

path "auth/approle/role/user-service/role-id" {
    capabilities = ["read"]
}

path "auth/approle/role/user-service/secret-id" {
  capabilities = ["create", "read"]
}

# Allow reading and writing to RabbitMQ roles
path "rabbitmq/roles/my-role" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Allow reading and writing to RabbitMQ creds
path "rabbitmq/creds/my-role" {
  capabilities = ["read", "list"]
}