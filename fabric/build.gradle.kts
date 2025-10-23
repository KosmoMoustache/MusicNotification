plugins {
	id("fabric-loom")
	`multiloader-loader`
	kotlin("jvm") version "2.2.0"
	id("com.google.devtools.ksp") version "2.2.0-2.0.2"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.18"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${commonMod.mcVersion}")
//	mappings(loom.officialMojangMappings())
	mappings(loom.layered {
		officialMojangMappings()
		commonMod.depOrNull("parchment")?.let { parchmentVersion ->
			parchment("org.parchmentmc.data:parchment-${commonMod.mcVersion}:$parchmentVersion@zip")
		}
	})

	modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
	modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mcVersion}")

	// TODO: Enable individual fabric modules as needed
//	dependencies {
//		fun fabricModules(vararg modules: String) = modules.forEach {
//			modImplementation(fabricApi.module("fabric-$it", property("deps.fabric-api") as String))
//		}
//
//		fabricModules("registry-sync-v0", "resource-loader-v0", "gametest-api-v1", "data-generation-api-v1")
//	}

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
	accessWidenerPath = common.project.file("../../src/main/resources/${mod.aw}")

	runs {
		create("FabricClient") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
//			vmArgs("-Dfabric.log.level=debug")
		}
	}

	mixin {
		defaultRefmapName = "${mod.id}.refmap.json"
	}
}

// gametest
fabricApi {
	configureTests {
		createSourceSet = true
		modId = commonMod.id
		eula = true
	}
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

tasks.named<Copy>("processGametestResources") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

//tasks.processResources {
//    if (stonecutterBuild.eval(stonecutterBuild.current.version, "<1.21")) {
//        doLast {
//            moveAndDeleteFileOrFolder(
//                file("${layout.buildDirectory.get().toString()}/resources/main/"),
//                "data/musicnotification/function",
//                "data/musicnotification/functions"
//            )
//        }
//    }
//}
