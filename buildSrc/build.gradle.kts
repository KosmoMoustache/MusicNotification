plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	gradlePluginPortal()
	maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

dependencies {
	implementation("dev.kikugie:stonecutter:0.9")
	implementation("net.fabricmc.fabric-loom-remap:net.fabricmc.fabric-loom-remap.gradle.plugin:1.15-SNAPSHOT")
	implementation("net.fabricmc.fabric-loom:net.fabricmc.fabric-loom.gradle.plugin:1.15-SNAPSHOT")
}
