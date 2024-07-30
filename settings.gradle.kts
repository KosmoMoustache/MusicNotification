pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.architectury.dev/")
    }
}

include("common")
include("fabric")
include("neoforge")
//include("forge")
