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
# Array to store rows
#rows=()
#
## Function to print the table header
#print_table_header() {
#    printf "+------------+----------+-------+---------+-------------+\n"
#    printf "| %-10s | %-8s | %-5s | %-7s | %-11s |\n" "steps" "pid" "name" "status" "messages"
#    printf "+------------+----------+-------+---------+-------------+\n"
#}
#
#add_row() {
#    local row=("$1" "$2" "$3" "$4" "$5")
#    rows+=("${row[@]}")
#}
#
## Function to add a row to the table
#print_row() {
#    local status_color=""
#
#    if [ "$4" = "success" ]; then
#        status_color=$(tput setaf 2) # Green color
#    else
#        status_color=$(tput setaf 1) # Red color
#    fi
#
#    printf "| %-10s | %-8s | %-5s | ${status_color}%-7s$(tput sgr0) | %-11s |\n" "$1" "$2" "$3" "$4" "$5"
#}

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

# Function to fetch and export Vault credentials
set_vault_credentials() {
  local service_role=$1

  # Fetch the Role ID
  VAULT_APPROLE_ROLE_ID=$(vault read -format=json auth/approle/role/"$service_role"/role-id | jq -r .data.role_id)
  export VAULT_APPROLE_ROLE_ID
  echo "VAULT_APPROLE_ROLE_ID for $service_role: $VAULT_APPROLE_ROLE_ID"

  # Generate a Secret ID
  VAULT_APPROLE_SECRET_ID=$(vault write -f -format=json auth/approle/role/"$service_role"/secret-id | jq -r .data.secret_id)
  export VAULT_APPROLE_SECRET_ID
  echo "VAULT_APPROLE_SECRET_ID for $service_role: $VAULT_APPROLE_SECRET_ID"
}

wait_for_service() {
  local url=$1
  local name=$2
  echo "Waiting for $name to start..."
  while ! curl -s $url > /dev/null; do
    echo "Waiting for $name..."
    sleep 5
  done
  echo "$name is up and running."
}

run_services() {
  local services=(
    "eureka-server"
    "config-server"
    "user-service"
    "email-service"
    "password-service"
    # "task-service"
    "api-gateway"
  )

  for service in "${services[@]}"; do
    # Check if the service needs Vault credentials
    if [[ "$service" == "user-service" || "$service" == "email-service" || "$service" == "password-service" ]]; then
      # Set Vault credentials for the current service
      set_vault_credentials "$service"
    fi

    cd "./$service" || exit
    echo "Running $service with VAULT_APPROLE_ROLE_ID=$VAULT_APPROLE_ROLE_ID and VAULT_APPROLE_SECRET_ID=$VAULT_APPROLE_SECRET_ID"
    env | grep VAULT

    mvn spring-boot:run &
    cd .. || exit

    # Wait for eureka-server and config-server to be ready before starting other services
    if [[ "$service" == "eureka-server" ]]; then
      wait_for_service "http://localhost:8761/eureka/apps" "Eureka Server"
    elif [[ "$service" == "config-server" ]]; then
      wait_for_service "http://localhost:8888/actuator/health" "Config Server"
    fi

    # Unset Vault credentials to prevent leakage to other services
    if [[ "$service" == "user-service" || "$service" == "email-service" || "$service" == "password-service" ]]; then
      unset VAULT_APPROLE_ROLE_ID
      unset VAULT_APPROLE_SECRET_ID
    fi
  done
}

main() {

  generate_vault_var
  echo "$VAULT_ADDR"
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
    # run_services
#    echo "user_service: $USER_SERVICE_PW"
#    echo "password_service: $PASSWORD_SERVICE_PW"
#    echo "task_service: $TASK_SERVICE_PW"
#    echo "email_service: $EMAIL_SERVICE_PW"
    echo "vault_token: $VAULT_TOKEN"
  fi
}

main