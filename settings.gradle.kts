pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric"}
		maven("https://maven.neoforged.net/releases/") { name = "NeoForge"}
		maven("https://maven.minecraftforge.net") { name = "Forge"}
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Release" }
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.2"
	id("dev.kikugie.loom-back-compat") version "0.2.1"
}

fun getVersions(name: String): List<String> {
	return providers.gradleProperty(name).orNull?.split(",")?.map { it.trim() } ?: emptyList()
}

val enabledVersions = mapOf(
	"common" to getVersions("stonecutter_enabled_common_versions"),
	"fabric" to getVersions("stonecutter_enabled_fabric_versions"),
	"neoforge" to getVersions("stonecutter_enabled_neoforge_versions"),
)
val enabledUniqueVersions = enabledVersions.values.flatten().distinct()

stonecutter {
	create(rootProject) {
		versions(*enabledUniqueVersions.toTypedArray())

		enabledVersions.forEach { (branchName, branchVersions) ->
			branch(branchName) {
				branchVersions.forEach { version ->
					version(version)
				}
			}
		}
	}
}

rootProject.name = "MusicNotification"
