#!/usr/bin/env bash

# e= exit immediately if command exits with non-zero status
# u= treat unset var as an error and exit
# o= command in pipeline fails the whole thng fails
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "$PROJECT_ROOT"