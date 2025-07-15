#!/usr/bin/env bash

set -euo pipefail

echo "Running Liquibase migration using liquibase profile..."
java -jar target/user-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=liquibase