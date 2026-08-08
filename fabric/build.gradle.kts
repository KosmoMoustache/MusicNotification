@file:Suppress("UnstableApiUsage")

plugins {
	id("multiloader-loader")
	id("dev.kikugie.loom-back-compat")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.23"
}

stonecutter {
	constants["modMenu"] = deps.modmenu != null
	constants["music_frequency"] = stonecutter.eval(current.version, "<=1.21.5")
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

dependencies {
	fun fabricModules(vararg modules: String) = modules.forEach {
		modImplementation(fabricApi.module("fabric-$it", "${deps.fapi}+${deps.minecraft}"))
	}

	minecraft("com.mojang:minecraft:${deps.minecraft}")

	if (stonecutter.eval(deps.minecraft, "<=1.21.11")) {
		mappings(loom.layered {
			officialMojangMappings()
			deps.parchment?.let { version ->
				parchment("org.parchmentmc.data:parchment-${deps.minecraft}:$version@zip")
			}
		})
	}

	modImplementation("net.fabricmc:fabric-loader:${deps.floader}")
	deps.modmenu?.let { version ->
		modImplementation("com.terraformersmc:modmenu:${version}")
	}
	deps.cloth_config?.let { version ->
		modImplementation("me.shedaniel.cloth:cloth-config-fabric:$version")
	}

	if (sc.current.parsed.matches(">= 1.21.5")) {
		fabricModules("command-api-v2", "gametest-api-v1", "client-gametest-api-v1")
	} else {
		fabricModules("command-api-v2")
	}

	// Runtime only mods
	modApi("net.fabricmc.fabric-api:fabric-api:${deps.fapi}+${deps.minecraft}") // TODO: modApi

	// Fabric Loader JUnit for testing
	testImplementation("net.fabricmc:fabric-loader-junit:${deps.floader}")

	// Mods
	if (sc.current.parsed.matches("<= 1.21.1")) {
		modImplementation(fletchingTable.modrinth("vanillabackport", deps.minecraft))
		modImplementation(fletchingTable.modrinth("platform", deps.minecraft))
	}
//	modImplementation(fletchingTable.modrinth("more-music-discs", deps.minecraft))
//	modImplementation(fletchingTable.modrinth("deimos", deps.minecraft))
}

//Mixin hotswap
afterEvaluate {
	loom.runs.configureEach {
		// https://fabricmc.net/wiki/tutorial:mixin_hotswaps
		vmArg("-javaagent:${configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") }}")
	}
}

loom {
	accessWidenerPath = common.project.file("../../src/main/resources/accesswideners/${mod.aw_version}.aw")

	runs {
		getByName("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			programArgs("--quickPlaySingleplayer", "wd_void", "--width", "1280", "--height", "720")
			if (sc.current.parsed > "1.21.1") {
				vmArgs("-XX:+AllowEnhancedClassRedefinition")
			}
			// "-Dfabric.log.level=debug"
		}
	}

	if (stonecutter.eval(deps.minecraft, "<=1.21.11")) {
		mixin {
			useLegacyMixinAp = true
			defaultRefmapName = "${mod.id}.refmap.json"
		}
	}
}


tasks.named<ProcessResources>("processResources") {
	val awFile = common.project.file("../../src/main/resources/accesswideners/${mod.aw_version}.aw")

	from(awFile.parentFile) {
		include(awFile.name)
		rename(awFile.name, "${mod.id}.aw")
		into("")
	}
}

// GameTest
if (sc.current.parsed > "1.21.1") {
	fabricApi {
		configureTests {
			enableGameTests = false
			createSourceSet = true
			eula = true
			modId = "musicnotification-test"
		}
	}
	tasks {
		named<Test>("test") {
			useJUnitPlatform()
		}

		named<Copy>("processGametestResources") {
			duplicatesStrategy = DuplicatesStrategy.EXCLUDE
		}
	}
}

