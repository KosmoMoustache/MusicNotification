set shell := ["bash", "-cu"]
set windows-shell := ["powershell.exe", "-NoLogo", "-NoProfile", "-Command"]

gradlew := if os_family() == "windows" { ".\\gradlew.bat" } else { "./gradlew" }
nuke_target := if os_family() == "windows" { "_nuke-windows" } else { "_nuke-unix" }
copy_tests_target := if os_family() == "windows" { "_copy-tests-windows" } else { "_copy-tests-unix" }

# Prints help for recipes
help:
    just --list

# Default recipe
@default:
    just help

# Builds project
build-project:
    {{ gradlew }} build

# Refresh dependencies
refresh:
    {{ gradlew }} --refresh-dependencies

# Generate sources
gen-sources:
    {{ gradlew }} genSources

# Runs datagen
run-data:
    {{ gradlew }} runData

# Runs default client
run-client:
    {{ gradlew }} runClient

# Runs fabric client
run-fabric-client:
    {{ gradlew }} fabric:runClient

# Runs fabric server
run-fabric-server:
    {{ gradlew }} fabric:runServer

# Runs forge client
run-forge-client:
    {{ gradlew }} forge:runClient

# Runs forge server
run-forge-server:
    {{ gradlew }} forge:runServer

# Runs neoforge client
run-neoforge-client:
    {{ gradlew }} neoforge:runClient

# Runs neoforge server
run-neoforge-server:
    {{ gradlew }} neoforge:runServer

# Copy test resources for each loader and versions (Only for Unix-like systems) | Optional argument: version to copy only (e.g. 1.21.1)
copy-tests version="":
    just {{ copy_tests_target }} {{ version }}

# Nuke implementation for Windows (not yet implemented)
_copy-tests-windows version="":
    echo "Not supported on Windows. Please copy test resources manually."

# Nuke implementation for Linux/macOS
_copy-tests-unix version="":
    ./test/copy-test.sh {{ version }}

# Nuke the project
nuke:
    just {{ nuke_target }}

# Nuke implementation for Linux/macOS
_nuke-unix:
    {{ gradlew }} --stop
    rm -rf "${GRADLE_HOME:-$HOME/.gradle}"/caches/transforms-*
    rm -rf "${GRADLE_HOME:-$HOME/.gradle}"/caches/build-cache-*
    find . -type d \( -name ".idea" -o -name ".kotlin" -o -name ".gradle" -o -name "build" -o -name "run" \) -exec rm -rf {} +

# Nuke implementation for Windows (PowerShell)
_nuke-windows:
    {{ gradlew }} --stop
    if ($env:GRADLE_HOME) { Remove-Item "$env:GRADLE_HOME\\caches\\transforms-*" -Recurse -Force -ErrorAction SilentlyContinue }
    if ($env:GRADLE_HOME) { Remove-Item "$env:GRADLE_HOME\\caches\\build-cache-*" -Recurse -Force -ErrorAction SilentlyContinue }
    Get-ChildItem -Path . -Recurse -Directory -Force -ErrorAction SilentlyContinue | Where-Object { $_.Name -in @('.idea', '.kotlin', '.gradle', 'build', 'run') } | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue

# Run GitHub Actions workflows using act
act-build:
    act workflow_dispatch -W .github/workflows/build.yml --input mod_loaders=fabric,neoforge --input versions=1.21.10,1.21.11

# Run changelog generation workflow using act
act-changelog:
    act workflow_dispatch -W .github/workflows/changelog.yml --input mod_version=3.0.0

# Run cache clearing workflow using act
act-clear-cache:
    act workflow_dispatch -W .github/workflows/clear-cache.yml

# Run publish workflow using act
_act-publish github-token="" curseforge-token="" modrinth-token="":
    act workflow_dispatch -W .github/workflows/publish.yml --input mod_loaders=fabric --input versions=1.21.11 --input platforms=github,modrinth -s GITHUB_TOKEN={{ github-token }} -s CURSEFORGE_TOKEN={{ curseforge-token }} -s MODRINTH_TOKEN={{ modrinth-token }}
