#!/usr/bin/env bash

echo "Running Liquibase migration using liquibase profile..."
java -jar target/instance-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=liquibase