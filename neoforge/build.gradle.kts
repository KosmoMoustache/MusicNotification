plugins {
	kotlin("jvm")
	id("multiloader-loader")
	id("net.neoforged.moddev")
	id("com.google.devtools.ksp")
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}


dependencies {
	api("me.shedaniel.cloth:cloth-config-neoforge:${commonMod.dep("cloth_config")}")
}

neoForge {
	version = commonMod.dep("neoforge")

	accessTransformers.from(project.file("../../src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${project.path})"
			programArgument("--quickPlaySingleplayer \"NeoForgePlayground\"")
		}
	}

	mods {
		register(commonMod.id) {
			sourceSet(sourceSets.main.get())
		}
	}

	parchment {
		commonMod.depOrNull("parchment")?.let {
			mappingsVersion = it
			minecraftVersion = commonMod.mcVersion
		}
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

tasks {
	processResources {
		exclude("${mod.id}.aw")
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(":neoforge:${commonMod.propOrNull("minecraft_version")}:stonecutterGenerate")
}
