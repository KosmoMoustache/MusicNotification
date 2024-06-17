pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.architectury.dev/") {
            name = "Architectury"
        }
        maven("https://files.minecraftforge.net/maven/") {
            name = "Forge"
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForge"
        }
        maven("https://maven.shedaniel.me/") {
            name = "Shedaniel"
        }
    }
}

rootProject.name = "musicnotification"

include("common")
include("fabric")
include("neoforge")
//include("forge")
//include("quilt")

