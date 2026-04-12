@file:Suppress("UnstableApiUsage")

plugins {
	kotlin("jvm")
	id("net.fabricmc.fabric-loom")
	id("multiloader-loader")
	id("com.google.devtools.ksp")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

stonecutter {
	constants["modMenu"] = commonMod.depOrNull("modmenu") != null
}

dependencies {
	fun fabricModules(vararg modules: String) = modules.forEach {
		implementation(fabricApi.module(it, "${commonMod.dep("fabric-api")}+${commonMod.mcVersion}"))
	}

	minecraft("com.mojang:minecraft:${commonMod.mcVersion}")

	implementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
	commonMod.depOrNull("modmenu")?.let { modMenuVersion ->
		implementation("com.terraformersmc:modmenu:${modMenuVersion}")
	}
	commonMod.depOrNull("cloth_config")?.let { cloth_configVersion ->
		implementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_configVersion}")
	}

	fabricModules(
		"fabric-command-api-v2", "fabric-gametest-api-v1", "fabric-client-gametest-api-v1"
	)

	// Runtime only mods
	implementation("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mcVersion}")

	// Fabric Loader JUnit for testing
	testImplementation("net.fabricmc:fabric-loader-junit:${commonMod.dep("fabric-loader")}")
}

//Mixin hotswap
afterEvaluate {
	loom.runs.configureEach {
		// https://fabricmc.net/wiki/tutorial:mixin_hotswaps
		vmArg("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
	}
}

loom {
	accessWidenerPath = common.project.file("../../src/main/resources/${commonMod.awVersion}.aw")

	runs {
		getByName("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			programArg("--quickPlaySingleplayer \"FabricPlayground\"")
			vmArgs("-XX:+AllowEnhancedClassRedefinition")
			// "-Dfabric.log.level=debug"
		}
	}
}

// gametest
fabricApi {
	configureTests {
		enableGameTests = false
		createSourceSet = true
		eula = true
		modId = "musicnotification-test"
	}
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

tasks.named<Copy>("processGametestResources") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
