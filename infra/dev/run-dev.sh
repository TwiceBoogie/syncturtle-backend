#!/usr/bin/env sh
set -eu

exec ./mvnw -q -DskipTests -f "${SERVICE}/pom.xml" spring-boot:run "${SPRING_BOOT_JDWP}" "$@"