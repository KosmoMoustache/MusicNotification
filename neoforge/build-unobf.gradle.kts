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
	deps.cloth_config?.let { version ->
		api("me.shedaniel.cloth:cloth-config-neoforge:${version}")
	}
}

neoForge {
	version = deps.neoforge

	accessTransformers.from(project.file("../../src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${project.path})"
			programArgument("--quickPlaySingleplayer wd_PlaygroundVoid")
			programArgument("--width 1280")
			programArgument("--height 720")
		}
	}

	mods {
		register(mod.id) {
			sourceSet(sourceSets.main.get())
		}
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

tasks {
	processResources {
		exclude("**/*.aw")
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(":neoforge:${deps.minecraft}:stonecutterGenerate")
}
