@file:Suppress("UnstableApiUsage")

plugins {
	kotlin("jvm")
	id("multiloader-common")
	id("net.fabricmc.fabric-loom")
	id("com.google.devtools.ksp")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

loom {
	accessWidenerPath =
		common.project.file("../../src/main/resources/${commonMod.awVersion}.aw")
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

stonecutter {
	filters.exclude("**/*.aw")
}

dependencies {
	minecraft("com.mojang:minecraft:${commonMod.mcVersion}")

	compileOnly("org.spongepowered:mixin:0.8.5")

	"io.github.llamalad7:mixinextras-common:0.3.5".let {
		compileOnly(it)
		annotationProcessor(it)
	}

	compileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")

	commonMod.depOrNull("cloth_config")?.let { cloth_configVersion ->
		api("me.shedaniel.cloth:cloth-config-neoforge:${cloth_configVersion}")
	}}

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
