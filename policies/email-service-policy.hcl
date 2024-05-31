path "secret/data/email-service*" {
    capabilities = ["create", "read", "update", "delete", "list"]
}
path "secret/data/application*" {
    capabilities = ["create", "read", "update", "delete", "list"]
}
path "sys/renew/*" {
    capabilities = ["update"]
}
# Allow creating, reading, and listing tokens for approle authentication method
path "auth/approle/login" {
  capabilities = ["create", "read", "list"]
}
# Allow reading and writing to RabbitMQ roles
// path "rabbitmq/roles/my-role" {
//   capabilities = ["create", "read", "update", "delete", "list"]
// }
path "auth/approle/role/email-service/role-id" {
    capabilities = ["read"]
}

path "auth/approle/role/email-service/secret-id" {
  capabilities = ["create", "read"]
}

# Allow reading and writing to RabbitMQ creds
path "rabbitmq/creds/my-role" {
  capabilities = ["read", "list"]
}