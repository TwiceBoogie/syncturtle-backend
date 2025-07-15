#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

handleEnvFile() {
  local env_file="$PROJECT_ROOT/.env"

  if [ ! -f "$env_file" ]; then
    echo ".env file not found. Creating one..."
    touch "$env_file"
  fi

  echo "Loading and checking environment variables from .env..."
  set -a
  source "$env_file"
  set +a

  updated=false

  if [ -z "${PGADMIN_EMAIL:-}" ]; then
    PGADMIN_EMAIL="random@gmail.com"
    echo "PGADMIN_EMAIL=$PGADMIN_EMAIL" >> "$env_file"
    echo "→ Set default PGADMIN_EMAIL"
    updated=true
  fi

  if [ -z "${RABBITMQ_USERNAME:-}" ]; then
    RABBITMQ_USERNAME="default_rabbitmq_user"
    echo "RABBITMQ_USERNAME=$RABBITMQ_USERNAME" >> "$env_file"
    echo "→ Set default RABBITMQ_USERNAME"
    updated=true
  fi

  if [ -z "${POSTGRES_USERNAME:-}" ]; then
    POSTGRES_USERNAME="default_postgres_user"
    echo "POSTGRES_USERNAME=$POSTGRES_USERNAME" >> "$env_file"
    echo "→ Set default POSTGRES_USERNAME"
    updated=true
  fi

  if [ -z "${PGADMIN_PASSWORD:-}" ]; then
    PGADMIN_PASSWORD=$(openssl rand -base64 20 | tr -d '+/=')
    echo "PGADMIN_PASSWORD=$PGADMIN_PASSWORD" >> "$env_file"
    echo "→ Generated PGADMIN_PASSWORD"
    updated=true
  fi

  if [ -z "${RABBITMQ_PASSWORD:-}" ]; then
    RABBITMQ_PASSWORD=$(openssl rand -base64 20 | tr -d '+/=')
    echo "RABBITMQ_PASSWORD=$RABBITMQ_PASSWORD" >> "$env_file"
    echo "→ Generated RABBITMQ_PASSWORD"
    updated=true
  fi

  if [ -z "${POSTGRES_PASSWORD:-}" ]; then
    POSTGRES_PASSWORD=$(openssl rand -base64 20 | tr -d '+/=')
    echo "POSTGRES_PASSWORD=$POSTGRES_PASSWORD" >> "$env_file"
    echo "→ Generated POSTGRES_PASSWORD"
    updated=true
  fi

  if [ "$updated" = true ]; then
    echo "✅ .env was updated with missing/default values."
  else
    echo "✅ All .env values already set."
  fi
}
