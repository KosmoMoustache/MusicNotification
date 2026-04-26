@file:Suppress("UnstableApiUsage")

plugins {
	id("multiloader-common")
	id("dev.kikugie.loom-back-compat")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

loom {
	accessWidenerPath =
		common.project.file("../../src/main/resources/${mod.aw_version}.aw")
}

stonecutter {
	filters.exclude("**/*.aw")
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${deps.minecraft}")

	if (stonecutter.eval(deps.minecraft, "<=26.0")) {
		mappings(loom.layered {
			officialMojangMappings()
			deps.parchment?.let { version ->
				parchment("org.parchmentmc.data:parchment-${deps.minecraft}:$version@zip")
			}
		})
	}

	compileOnly("org.spongepowered:mixin:0.8.5")

	"io.github.llamalad7:mixinextras-common:0.5.4".let {
		compileOnly(it)
		annotationProcessor(it)
	}

	compileOnly("net.fabricmc:fabric-loader:${deps.floader}")
	deps.cloth_config?.let { version ->
		modApi("me.shedaniel.cloth:cloth-config-neoforge:${version}")
	}
}

val commonJava: Configuration by configurations.creating {
	isCanBeResolved = false
	isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
	isCanBeResolved = false
	isCanBeConsumed = true
}

artifacts {
	afterEvaluate {
		val mainSourceSet = sourceSets.main.get()
		mainSourceSet.java.sourceDirectories.files.forEach {
			add(commonJava.name, it)
		}
		mainSourceSet.resources.sourceDirectories.files.forEach {
			add(commonResources.name, it)
		}
	}
}
