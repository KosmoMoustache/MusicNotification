import net.neoforged.nfrtgradle.CreateMinecraftArtifacts

plugins {
	kotlin("jvm")
	id("multiloader-loader")
	id("net.neoforged.moddev")
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.23"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}

	accessConverter.register(sourceSets.main) {
		add("accesswideners/${mod.aw_version}.aw")
	}
}

stonecutter {
	constants["music_frequency"] = stonecutter.eval(current.version, "<=1.21.5")
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
	enable {
		version = deps.neoforge
	}

	val at = project.file("build/resources/main/META-INF/accesstransformer.cfg")
	accessTransformers.from(at.absolutePath)
	validateAccessTransformers = true

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${project().path})"
			programArguments.addAll("--quickPlaySingleplayer", "wd_void", "--width", "1280", "--height", "720")
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
		exclude("*.aw")
	}
}
tasks {
	named<CreateMinecraftArtifacts>("createMinecraftArtifacts") {
		dependsOn(":neoforge:${deps.minecraft}:processResources")
	}
}
