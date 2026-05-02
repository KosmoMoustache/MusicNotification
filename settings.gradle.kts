pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
			content {
				includeGroup("net.fabricmc")
			}
		}
		maven {
			name = "NeoForge"
			url = uri("https://maven.neoforged.net/releases/")
			content {
				includeGroup("net.neoforged")
			}
		}
		maven {
			name = "Forge"
			url = uri("https://maven.minecraftforge.net")
			content {
				includeGroup("net.minecraftforge")
			}
		}
		maven {
			name = "KikuGie Snapshots"
			url = uri("https://maven.kikugie.dev/snapshots")
			content {
				includeGroup("dev.kikugie")
				includeGroup("dev.kikugie.stonecutter")
				includeGroup("dev.kikugie.loom-back-compat")
				includeGroup("dev.kikugie.fletching-table.neoforge")
				includeGroup("dev.kikugie.fletching-table.fabric")
			}
		}
		maven {
			name = "KikuGie Release"
			url = uri("https://maven.kikugie.dev/releases")
			content {
				includeGroup("dev.kikugie")
				includeGroup("dev.kikugie.stonecutter")
				includeGroup("dev.kikugie.loom-back-compat")
				includeGroup("dev.kikugie.fletching-table.neoforge")
				includeGroup("dev.kikugie.fletching-table.fabric")
			}
		}
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
