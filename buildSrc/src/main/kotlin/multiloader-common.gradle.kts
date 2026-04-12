import org.gradle.internal.jvm.inspection.JvmVendor

plugins {
	id("java")
	id("idea")
	id("java-library")
}

version = "${loader}-${commonMod.version}+mc${stonecutterBuild.current.version}"

base {
	archivesName = commonMod.id
}

java {
	toolchain.vendor = JvmVendorSpec.JETBRAINS
	toolchain.languageVersion = JavaLanguageVersion.of(commonProject.prop("java.version")!!)
//	sourceCompatibility = JavaVersion.VERSION_25
//	targetCompatibility = JavaVersion.VERSION_25
//	withSourcesJar()
//	withJavadocJar()
}

repositories {
	mavenCentral()
	exclusiveContent {
		forRepository {
			maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
		}
		filter { includeGroupAndSubgroups("org.spongepowered") }
	}
	exclusiveContent {
		forRepositories(
			maven("https://maven.parchmentmc.org") { name = "ParchmentMC" },
			maven("https://maven.neoforged.net/releases") { name = "NeoForge" },
		)
		filter { includeGroup("org.parchmentmc.data") }
	}
	maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
	maven("https://maven.shedaniel.me/")

	maven("https://maven.nucleoid.xyz") { name = "pb4.eu" }
	maven("https://maven.quiltmc.org/repository/release")
}

tasks {
	processResources {
		val expandProps = mapOf(
			"javaVersion" to commonMod.propOrNull("java.version"),
			"modId" to commonMod.id,
			"modName" to commonMod.name,
			"modVersion" to commonMod.version,
			"modGroup" to commonMod.group,
			"modAuthor" to commonMod.author,
			"modDescription" to commonMod.description,
			"modLicense" to commonMod.license,
			"modGitHub" to commonMod.github,
			"awVersion" to commonMod.awVersion,
			"minecraftVersion" to commonMod.propOrNull("minecraft_version"),
			"minMinecraftVersion" to commonMod.propOrNull("min_minecraft_version"),
			"fabricLoaderVersion" to commonMod.depOrNull("fabric-loader"),
			"fabricApiVersion" to commonMod.depOrNull("fabric-api"),
			"neoForgeVersion" to commonMod.depOrNull("neoforge"),
			"clothConfigVersion" to commonMod.depOrNull("cloth_config")
		).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

		val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

		filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
			expand(expandProps)
		}

		filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json", "*.mixins.json5")) {
			expand(jsonExpandProps)
		}

		inputs.properties(expandProps)
	}
}

tasks.named("processResources") {
	dependsOn(":common:${commonMod.propOrNull("minecraft_version")}:stonecutterGenerate")
}
