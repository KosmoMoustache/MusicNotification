plugins {
	id("fabric-loom")
	`multiloader-loader`
	kotlin("jvm") version "2.2.0"
	id("com.google.devtools.ksp") version "2.2.0-2.0.2"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${commonMod.mcVersion}")
	mappings(loom.layered {
		officialMojangMappings()
		commonMod.depOrNull("parchment")?.let { parchmentVersion ->
			parchment("org.parchmentmc.data:parchment-${commonMod.mcVersion}:$parchmentVersion@zip")
		}
	})

	modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")

	fun fabricModules(vararg modules: String) = modules.forEach {
		modImplementation(fabricApi.module("fabric-$it", "${commonMod.dep("fabric-api")}+${commonMod.mcVersion}"))
	}

	fabricModules("command-api-v2", "gametest-api-v1", "client-gametest-api-v1")


//	modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mcVersion}")

	modApi("com.terraformersmc:modmenu:${commonMod.depOrNull("modmenu")}")
	modApi("me.shedaniel.cloth:cloth-config-fabric:${commonMod.depOrNull("cloth_config")}")

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
	accessWidenerPath = common.project.file("../../src/main/resources/${commonMod.awVersion}.accesswidener")

	runs {
		getByName("client") {
//		create("FabricClient") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			programArg("--quickPlaySingleplayer \"FabricPlayground\"")
			vmArgs("-XX:+AllowEnhancedClassRedefinition")
			// "-Dfabric.log.level=debug"
		}
	}

	mixin {
		defaultRefmapName = "${mod.id}.refmap.json"
	}
}

// gametest
fabricApi {
	configureTests {
//		enableGameTests = true
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
