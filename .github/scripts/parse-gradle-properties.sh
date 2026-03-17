#!/bin/bash
#!/usr/bin/env bash

set -euo pipefail

version=${1:-}

set_output() {
    local key="$1"
    local value="$2"
    if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
        echo "${key}=${value}" >> "$GITHUB_OUTPUT"
    fi
}

parse_properties_file() {
    local file=$1
    while IFS='=' read -r key value; do
        key=$(echo "$key" | awk '{$1=$1;print}')
        value=$(echo "$value" | awk '{$1=$1;print}')

        if [[ -z "$key" || "$key" =~ ^# || "$key" == "org.gradle.jvmargs" ]]; then
            continue
        fi

        key=$(echo "$key" | tr '[:lower:]' '[:upper:]' | tr -c '[:alnum:]' '_')
        key=$(echo "$key" | sed 's/_$//')

        echo "${key}=${value}"
        set_output "$key" "$value"
    done < "$file"
}

parse_properties_file gradle.properties

if [[ -n "$version" ]]; then
    parse_properties_file "versions/${version}/gradle.properties"
fi
