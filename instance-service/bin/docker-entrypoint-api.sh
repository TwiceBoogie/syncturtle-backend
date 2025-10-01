#!/bin/bash
set -euo pipefail

# Collect system information
HOSTNAME=$(hostname)
MAC_ADDRESS=$(ip link show | awk '/ether/ {print $2}' | head -n 1)
CPU_INFO=$(cat /proc/cpuinfo)
MEMORY_INFO=$(free -h)
DISK_INFO=$(df -h)

# Concatenate information and compute SHA-256 hash
SIGNATURE=$(echo "$HOSTNAME$MAC_ADDRESS$CPU_INFO$MEMORY_INFO$DISK_INFO" | sha256sum | awk '{print $1}')

echo "Setup: computed MACHINE_SIGNATURE=$SIGNATURE"

exec java -jar /app/app.jar \
    --spring.profiles.active=setup \
    --machine-signature="$SIGNATURE"