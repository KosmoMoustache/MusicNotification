#!/bin/bash
#!/usr/bin/env bash

set -euo pipefail

allowed_mod_loaders=$1
allowed_versions=$2

parse_list() {
  local raw="$1"
  raw="${raw//[\[\]\']/}"
  raw="${raw//\"/}"
  raw="${raw// /}"

  if [[ -z "$raw" ]]; then
    return
  fi

  IFS=',' read -r -a parsed <<< "$raw"
  printf '%s\n' "${parsed[@]}"
}

contains() {
  local needle="$1"
  shift

  for value in "$@"; do
    if [[ "$value" == "$needle" ]]; then
      return 0
    fi
  done

  return 1
}

mapfile -t allowed_mod_loaders_array < <(parse_list "$allowed_mod_loaders")
mapfile -t allowed_versions_array < <(parse_list "$allowed_versions")

set_output() {
  local key="$1"
  local value="$2"
  if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
    echo "$key=$value" >> "$GITHUB_OUTPUT"
  fi
}

matrix_content="{\"include\":["
enabled_platforms=$(awk -F= '/stonecutter_enabled_platforms/{print $2}' gradle.properties | tr -d ' ')

for platform in $(echo "$enabled_platforms" | tr ',' ' '); do
  if [[ ${#allowed_mod_loaders_array[@]} -eq 0 ]] || contains "$platform" "${allowed_mod_loaders_array[@]}"; then
    versions=$(awk -F= '/stonecutter_enabled_'$platform'_versions/{print $2}' gradle.properties | tr -d ' ')
    for version in $(echo "$versions" | tr ',' ' '); do
      if [[ ${#allowed_versions_array[@]} -eq 0 ]] || contains "$version" "${allowed_versions_array[@]}"; then
         if [[ "$platform" == "fabric" ]]; then
            supported_mod_loaders="\"fabric\",\"quilt\""
         else
          supported_mod_loaders="\"$platform\""
         fi

        matrix_entry="{\"mod_loader\":\"$platform\",\"version\":\"$version\",\"supported_mod_loaders\":[$supported_mod_loaders]},"
        matrix_content+="$matrix_entry"
      fi
    done
  fi
done

matrix_content="${matrix_content%,}]}"
echo $matrix_content
set_output "matrix" "$matrix_content"

