dependencyResolutionManagement {
    repositories {
        // NeoForge
        maven("https://maven.neoforged.net/releases") {
            mavenContent { releasesOnly() }
        }
        // Minecraft
        maven("https://libraries.minecraft.net") {
            name = "minecraft"
        }
    }
}
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
//        maven("https://maven.minecraftforge.net/")
//        maven("https://files.minecraftforge.net/maven/")
        maven("https://maven.architectury.dev/")
        // Deps
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/releases/")
    }
}

//dependencyResolutionManagement {
//    versionCatalogs {
//        create("libs") {
//            from(files("./gradle/libs.versions.toml"))
//        }
//    }
//}

include("common")
include("fabric")
include("neoforge")
//include("forge")
