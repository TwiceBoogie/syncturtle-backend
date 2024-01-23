#!/usr/bin bash

# generate passwords for each service
declare USER_SERVICE_PW
declare PASSWORD_SERVICE_PW
declare TASK_SERVICE_PW
declare EMAIL_SERVICE_PW

rabbitUsers=("user-service" "password-service" "task-service" "email-service")
rabbitPasswords=(
  "$(openssl rand -base64 20 | tr -d '+/=')"
  "$(openssl rand -base64 20 | tr -d '+/=')"
  "$(openssl rand -base64 20 | tr -d '+/=')"
  "$(openssl rand -base64 20 | tr -d '+/=')"
)
queues=("q.usersvc" "q.passwordsvc" "q.tasksvc" "q.mail")

generatePasswords() {
  export USER_SERVICE_PW="${rabbitPasswords[0]}"
  export PASSWORD_SERVICE_PW="${rabbitPasswords[1]}"
  export TASK_SERVICE_PW="${rabbitPasswords[2]}"
  export EMAIL_SERVICE_PW="${rabbitPasswords[3]}"
}

declare_and_bind_exchanges() {
  docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare exchange name=user.events \
  type=fanout

  docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare exchange name=email.exchange \
  type=direct

  for ((i = 0; i < 4; i++)); do
      if [ "$i" -eq 3 ]; then
          docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding \
            source=email.exchange destination="${queues[$i]}" routing_key=high_priority.mail
      else
          docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding \
            source=user.events destination="${queues[$i]}"
      fi
  done

}

declare_queues() {
  for queue in "${queues[@]}" ; do
    docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare queue name="$queue"
  done
}

create_users() {
  echo "creating users"
  for (( i = 0; i < 4; i++ )); do
    docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" \
      declare user name="${rabbitUsers[$i]}" password="${rabbitPasswords[$i]}" tags=username
    docker exec rabbitMQ rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" \
      declare permission vhost=/ user="${rabbitUsers[$i]}" configure='^$' write='.*' read='.*'
  done
}

setup_rabbitmq() {
  generatePasswords
  declare_queues
  declare_and_bind_exchanges
  # create_users
}