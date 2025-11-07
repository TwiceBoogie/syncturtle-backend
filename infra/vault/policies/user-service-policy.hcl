# vault kv secrets

# Allow full access to shared application-level secrets
path "secret/data/application" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# Allow full access to this service's own secrets
path "secret/data/user-service" {
  capabilities = ["read", "list"]
}
path "secret/data/user-service/*" {
  capabilities = ["create", "read", "update", "delete", "list"]
}

# db creds

# Allow reading dynamically generated Postgres credentials
path "database/creds/user-service" {
  capabilities = ["read"]
}

# approle auth

# Allow login via AppRole
path "auth/approle/login" {
  capabilities = ["create", "read"]
}

# Allow reading Role ID
path "auth/approle/role/user-service/role-id" {
  capabilities = ["read"]
}

# Allow creating and reading Secret ID
path "auth/approle/role/user-service/secret-id" {
  capabilities = ["create", "read"]
}

# renew the login token
path "auth/token/renew-self" {
  capabilities = ["update"]
}

# renew * revoke dynamic secret leases (rabbitmq, db, etc.)
path "sys/leases/renew" {
  capabilities = ["update"]
}

# Allow token renewal (for longer running tasks, optional)
path "sys/leases/revoke" {
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

# dynamic certs

# Allow issuing certificates
path "pki/issue/syncturtle" {
  capabilities = ["create", "update"]
}

# Optional: Read the CA cert chain if needed
path "pki/cert/ca_chain" {
  capabilities = ["read"]
}