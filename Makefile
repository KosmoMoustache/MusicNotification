help: ## Prints help for targets with comments
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

build-project: ## Builds project
	./gradlew build

refresh: ## Refresh dependencies
	./gradlew --refresh-dependencies

gen-sources: ## Generate sources
	./gradlew genSources

run-data: ## Runs datagen
	./gradlew runData

run-client:
	./gradlew runClient

run-fabric-client: ## Runs fabric client
	./gradlew fabric:runClient

run-fabric-server: ## Runs fabric server
	./gradlew fabric:runServer

run-forge-client: ## Runs forge client
	./gradlew forge:runClient

run-forge-server: ## Runs forge server
	./gradlew forge:runServer

run-neoforge-client: ## Runs neoforge client
	./gradlew neoforge:runClient

run-neoforge-server: ## Runs neoforge server
	./gradlew neoforge:runServer

copy-tests: ## Copy test resources for each loader and versions
	./test/copy-test.sh

nuke: ## Nuke the project
	./gradlew --stop
	rm -rf $GRADLE_HOME/caches/transforms-*
	rm -rf $GRADLE_HOME/caches/build-cache-*
	find . -type d \( -name ".idea" -o -name ".kotlin" -o -name ".gradle" -o -name "build" -o -name "run" \) -exec rm -rf {} +