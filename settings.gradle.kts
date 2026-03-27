pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.neoforged.net/releases/")
		maven("https://maven.minecraftforge.net")
		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9"
}

val commonVersions =
	providers.gradleProperty("stonecutter_enabled_common_versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val fabricVersions =
	providers.gradleProperty("stonecutter_enabled_fabric_versions").orNull?.split(",")?.map { it.trim() } ?: emptyList()
val neoforgeVersions =
	providers.gradleProperty("stonecutter_enabled_neoforge_versions").orNull?.split(",")?.map { it.trim() }
		?: emptyList()
val dists = mapOf(
	"common" to commonVersions,
	"fabric" to fabricVersions,
	"neoforge" to neoforgeVersions
)
val uniqueVersions = dists.values.flatten().distinct()

stonecutter {
	create(rootProject) {
		versions(*uniqueVersions.toTypedArray())

		dists.forEach { (branchName, branchVersions) ->
			branch(branchName) {
				branchVersions.forEach { version ->
				version(version).buildscript(if (stonecutter.eval(version, ">=26.1")) "build-unobf.gradle.kts" else "build.gradle.kts")
				}
			}
		}
	}
}

rootProject.name = "MusicNotification"
