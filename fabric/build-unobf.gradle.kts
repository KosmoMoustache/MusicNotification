@file:Suppress("UnstableApiUsage")

plugins {
	kotlin("jvm")
	id("net.fabricmc.fabric-loom")
	id("multiloader-loader")
	id("com.google.devtools.ksp")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

kotlin {
	jvmToolchain(lproject.prop("java.version")!!.toInt())
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

stonecutter {
	constants["modMenu"] = deps.modmenu != null
}

dependencies {
	fun fabricModules(vararg modules: String) = modules.forEach {
		implementation(fabricApi.module(it, "${deps.fapi}+${deps.minecraft}"))
	}

	minecraft("com.mojang:minecraft:${deps.minecraft}")

	implementation("net.fabricmc:fabric-loader:${deps.floader}")
	deps.modmenu?.let { version ->
		implementation("com.terraformersmc:modmenu:${version}")
	}
	deps.cloth_config?.let { version ->
		implementation("me.shedaniel.cloth:cloth-config-fabric:${version}")
	}

	fabricModules(
		"fabric-command-api-v2", "fabric-gametest-api-v1", "fabric-client-gametest-api-v1"
	)

	// Runtime only mods
	implementation("net.fabricmc.fabric-api:fabric-api:${deps.fapi}+${deps.minecraft}")

	// Fabric Loader JUnit for testing
	testImplementation("net.fabricmc:fabric-loader-junit:${deps.floader}")
}

//Mixin hotswap
afterEvaluate {
	loom.runs.configureEach {
		// https://fabricmc.net/wiki/tutorial:mixin_hotswaps
		vmArg("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
	}
}

loom {
	accessWidenerPath = common.project.file("../../src/main/resources/${mod.aw_version}.aw")

	runs {
		getByName("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			programArgs("--quickPlaySingleplayer", "wd_PlaygroundVoid", "--width",  "1280", "--height",  "720")
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
