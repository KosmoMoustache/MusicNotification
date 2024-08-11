enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")

        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

rootProject.name = "MusicNotification"

include("common")
include("fabric")
include("neoforge")
//include("forge")
