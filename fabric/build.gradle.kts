@file:Suppress("UnstableApiUsage")

plugins {
	kotlin("jvm")
	 id("fabric-loom-compat")
	id("multiloader-loader")
	id("dev.kikugie.fletching-table.fabric")
}

// TODO: Useless ??
kotlin {
	jvmToolchain(lproject.prop("java.version")!!.toInt())
}

stonecutter {
	constants["modMenu"] = deps.modmenu != null
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

	if (sc.current.parsed >= "1.21.5") {
		fabricModules("command-api-v2", "gametest-api-v1", "client-gametest-api-v1")
	} else {
		fabricModules("command-api-v2")
	}

	// Runtime only mods
	modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fapi}+${deps.minecraft}")

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
			programArgs("--quickPlaySingleplayer", "wd_PlaygroundVoid", "--width", "1280", "--height", "720")
			if (sc.current.parsed > "1.21.1") {
				vmArgs("-XX:+AllowEnhancedClassRedefinition")
			}
			// "-Dfabric.log.level=debug"
		}
	}
}

// gametest
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

