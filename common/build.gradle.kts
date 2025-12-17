plugins {
	id("multiloader-common")
	id("fabric-loom")
	kotlin("jvm") version "2.2.0"
	id("com.google.devtools.ksp") version "2.2.0-2.0.2"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}


loom {
	accessWidenerPath =
		common.project.file("../../src/main/resources/${commonMod.awVersion}.accesswidener")

	mixin {
		useLegacyMixinAp = false
	}
}

fletchingTable {
	j52j.register("main") {
		extension("json", "**/*.json5")
	}
}

dependencies {
	minecraft(
		group = "com.mojang",
		name = "minecraft",
		version = commonMod.mcVersion
	)
	mappings(loom.layered {
		officialMojangMappings()
		commonMod.depOrNull("parchment")?.let { parchmentVersion ->
			parchment("org.parchmentmc.data:parchment-${commonMod.mcVersion}:$parchmentVersion@zip")
		}
	})

	compileOnly("org.spongepowered:mixin:0.8.5")

	"io.github.llamalad7:mixinextras-common:0.3.5".let {
		compileOnly(it)
		annotationProcessor(it)
	}

	modCompileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
	modApi("me.shedaniel.cloth:cloth-config-neoforge:${commonMod.depOrNull("cloth_config")}")
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
