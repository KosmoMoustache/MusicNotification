import net.neoforged.nfrtgradle.CreateMinecraftArtifacts

plugins {
	kotlin("jvm")
	id("multiloader-loader")
	id("net.neoforged.moddev")
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
	lang.create("main") {
		patterns.add("**/*.yml")
	}
}

dependencies {
	deps.cloth_config?.let { version ->
		api("me.shedaniel.cloth:cloth-config-neoforge:${version}")
	}

//	if (sc.current.parsed.matches("<= 1.21.1")) {
//		implementation(fletchingTable.modrinth("vanillabackport", deps.minecraft))
//		implementation(fletchingTable.modrinth("platform", deps.minecraft))
//	}
}

neoForge {
	version = deps.neoforge

	accessTransformers.from(project.file("../../src/main/resources/META-INF/${mod.at_version}.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${project.path})"
			programArgument("--quickPlaySingleplayer wd_void")
			programArgument("--width 1280")
			programArgument("--height 720")
		}
	}

	mods {
		register(mod.id) {
			sourceSet(sourceSets.main.get())
		}
	}

	deps.parchment?.let {
		parchment {
			mappingsVersion = it
			minecraftVersion = deps.minecraft
		}
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

tasks {
	named<ProcessResources>("processResources") {
		exclude("**/*.aw")
	}
	named<CreateMinecraftArtifacts>("createMinecraftArtifacts") {
		dependsOn(":neoforge:${deps.minecraft}:stonecutterGenerate")
	}
}

