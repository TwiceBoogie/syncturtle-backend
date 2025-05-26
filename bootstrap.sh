#!/usr/bin/env bash

if [[ -n $ZSH_VERSION ]]; then
    echo "Zsh detected, setting array indexing to start from 0"
    setopt KSH_ARRAYS
fi

source check_env_var.sh
source install_vault.sh
source vault.sh
source rabbitmq.sh



# shellcheck disable=SC2034
os_name=$(uname -s | tr '[:upper:]' '[:lower:]')
# shellcheck disable=SC2034
if [ "$os_name" = "linux" ]; then
    arch=$(dpkg --print-architecture)
else
    arch=$(uname -m)
fi

logo() {
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  NC='\033[0m' # No Color

  echo -e "${RED}"
  echo -e "
   ____              _       _
  |  _ \\            | |     | |
  | |_) | ___   ___ | |_ ___| |_ _ __ __ _ _ __
  |  _ < / _ \\ / _ \\| __/ __| __| '__/ _\` | '_ \\
  | |_) | (_) | (_) | |\\__ \\ |_| | | (_| | |_) |
  |____/ \\___/ \\___/ \\__|___/\\__|_|  \\__,_| .__/
                                          | |
                                          |_|
  "
  echo -e "${NC}" # Reset color
}

check_docker_permissions() {
    if docker ps &>/dev/null; then
        echo "Docker is accessible without sudo. Proceeding with the script."
        return 0
    else
        echo "Failed to access Docker without sudo. Checking with sudo..."
        if sudo docker ps &>/dev/null; then
            echo "Docker is accessible with sudo, but the script requires no sudo for Docker."
            echo "Please configure your Docker permissions to allow non-sudo access:"
            echo "1. Add your user to the Docker group: sudo usermod -aG docker \${USER}"
            echo "2. Log out and back in for this to take effect."
            echo "3. Verify with: docker ps"
            return 1
        else
            echo "Docker is not accessible, even with sudo. Please ensure Docker is installed and configured correctly."
            return 2
        fi
    fi
}


wait_for_containers() {
  echo "Starting docker containers."
  docker compose --env-file .env up -d
  local container_names=("ms-postgres" "ms-pgadmin" "zipkin" "rabbitMQ")
  local total_containers=${#container_names[@]}
  local containers_ready=0
  local iterations=0

  containers_are_up() {
    for name in "${container_names[@]}" ; do
      if docker ps -f name="$name" --format '{{.Status}}' | grep -q "Up"; then
        ((containers_ready++))
      fi
    done
  }

  while [ "$containers_ready" -lt "$total_containers" ] && [ "$iterations" -lt 3 ]; do
    sleep 5
    containers_ready=0
    containers_are_up
    ((iterations++))
  done

  if [ "$containers_ready" -lt "$total_containers" ]; then
      echo "Containers didn't start successfully after $iterations attempts."
    else
      echo "All containers are up!"
    fi
}

create_databases() {
  echo "Creating databases for services."

#  docker exec -i ms-postgres psql -U "$POSTGRES_USERNAME" -d postgres -f "$(pwd)/setup_database.sql"
  docker cp ./setup_database.sql ms-postgres:/
  docker exec -i ms-postgres psql -U "$POSTGRES_USERNAME" -d postgres < ./setup_database.sql
}

run_liquibase() {
  echo "Running liquibase command for services."

  ./mvnw -pl user-service liquibase:update
  ./mvnw -pl password-service liquibase:update
  ./mvnw -pl task-service liquibase:update
  docker exec -i ms-postgres psql -U "$POSTGRES_USERNAME" -d user -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"access_user_svc\";"
  docker exec -i ms-postgres psql -U "$POSTGRES_USERNAME" -d password -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"access_password_svc\";"
  docker exec -i ms-postgres psql -U "$POSTGRES_USERNAME" -d task -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"access_task_svc\";"
}

main() {
  logo
  handleEnvFile
  local envFileExists=$?
  if [ "$envFileExists" -eq 1 ]; then
    vault_setup
    wait_for_containers
    create_databases
    run_liquibase
     start_vault_server
     setup_rabbitmq
  fi
}

main