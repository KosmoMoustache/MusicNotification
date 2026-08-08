plugins {
	id("java")
	id("idea")
	id("java-library")
}

version = "${loader}-${mod.version}+mc${stonecutterBuild.current.version}"

base {
	archivesName = mod.id
}

java {
	val javaVersion  = lproject.prop("java.version")!!.toInt()

	toolchain.vendor = JvmVendorSpec.JETBRAINS
	toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

repositories {
	mavenCentral()
	strictMaven("https://repo.spongepowered.org/repository/maven-public", "org.spongepowered", "org.spongepowered")
	strictMaven("https://maven.parchmentmc.org", "org.parchmentmc.data", "org.parchmentmc.data")
//	strictMaven("https://maven.terraformersmc.com/releases/", "com.terraformersmc", "com.terraformersmc")
	strictMaven("https://maven.gnomecraft.net/releases/", "com.terraformersmc", "com.terraformersmc")
	strictMaven("https://maven.shedaniel.me/", "me.shedaniel", "me.shedaniel.cloth")

	strictMaven("https://maven.nucleoid.xyz", "pb4.eu", "xyz.nucleoid")
	strictMaven("https://maven.quiltmc.org/repository/release", "quiltmc", "org.quiltmc")

	strictMaven("https://www.cursemaven.com", "Curseforge", "curse.maven")
	strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")

}

tasks.named<ProcessResources>("processResources") {
		val props = HashMap<String, String>().apply {
				this["java_version"] = lproject.prop("java.version")!!
				this["mod_id"] = mod.id
				this["mod_name"] = mod.name
				this["mod_version"] = mod.version
				this["mod_description"] = mod.description
				this["mod_license"] = mod.license
				this["mod_github"] = mod.github
				this["aw_version"] = mod.aw_version
				this["at_version"] = mod.at_version
				this["fabric_mc_range"] = mod.fabric_mc_range
				this["neoforge_mc_range"] = mod.neoforge_mc_range
				this["floader"] = deps.floader
				this["fapi"] = deps.fapi
				this["neoforge"] = deps.neoforge
				this["cloth_config"] = deps.cloth_config.orEmpty()
				this["modmenu"] = deps.modmenu.orEmpty()
			}

		filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json", "*.mixins.json5", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
			expand(props)
		}

	inputs.properties(props)

	dependsOn(":common:${deps.minecraft}:stonecutterGenerate")
}
