# vault kv secrets

# Allow full access to shared application-level secrets
path "secret/data/application" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Allow full access to this service's own secrets
path "secret/data/task-service" {
  capabilities = ["read", "list"]
}

path "secret/data/task-service/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# db creds

# Allow reading dynamically generated Postgres credentials
path "database/creds/task-service" {
  capabilities = ["read"]
}

# approle auth

# Allow login via AppRole
path "auth/approle/login" {
  capabilities = ["create", "read"]
}

# Allow reading Role ID
path "auth/approle/role/task-service/role-id" {
  capabilities = ["read"]
}

# Allow creating and reading Secret ID
path "auth/approle/role/task-service/secret-id" {
  capabilities = ["create", "read"]
}

# Allow token renewal (for longer running tasks, optional)
path "sys/renew/*" {
  capabilities = ["update"]
}

# rabbitmq

# Allow managing RabbitMQ role definition
path "rabbitmq/roles/my-role" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Allow retrieving generated RabbitMQ credentials
path "rabbitmq/creds/my-role" {
  capabilities = ["read"]
}