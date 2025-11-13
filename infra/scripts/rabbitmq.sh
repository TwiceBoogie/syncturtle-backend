#!/bin/sh
set -euo pipefail

echo "RabbitMQ username: ${RABBITMQ_USERNAME:-NOT SET}"
echo "RabbitMQ password: ${RABBITMQ_PASSWORD:-NOT SET}"

if [ -z "${RABBITMQ_USERNAME:-}" ] || [ -z "${RABBITMQ_PASSWORD:-}" ]; then
  echo "❌ RABBITMQ_USERNAME or RABBITMQ_PASSWORD not set"
  exit 1
fi

echo "⏳ Waiting for RabbitMQ to be ready..."
until rabbitmqctl status > /dev/null 2>&1; do
  sleep 1
done

echo "Declaring queues and exchanges..."

rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare exchange name=user.events type=fanout
rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare exchange name=email.exchange type=direct

for queue in q.usersvc q.passwordsvc q.tasksvc q.mail; do
  rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare queue name="$queue"
done

rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding source=user.events destination=q.usersvc
rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding source=user.events destination=q.passwordsvc
rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding source=user.events destination=q.tasksvc
rabbitmqadmin -u "$RABBITMQ_USERNAME" -p "$RABBITMQ_PASSWORD" declare binding source=email.exchange destination=q.mail routing_key=high_priority.mail