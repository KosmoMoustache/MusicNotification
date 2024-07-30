import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "dev.architectury.loom")

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    group = rootProject.property("maven_group").toString()
    version = rootProject.property("mod_version").toString()

    if (project.hasProperty("loom.platform")) {
        version =
            "${version}+mc${rootProject.property("minecraft_version").toString()}-${
                project.property("loom.platform").toString()
            }"
    }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        // TODO
//        "minecraft"(libs.minecraft)
        "minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version").toString()}")

        @Suppress("UnstableApiUsage")
        "mappings"(loom.layered {
            officialMojangMappings()
        })
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }

    java {
        // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
        // if it is present.
        // If you remove this line, sources will not be generated.
        withSourcesJar()

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

allprojects {
    repositories {
        // Minecraft
        maven("https://libraries.minecraft.net") {
            name = "minecraft"
        }

        // Fabric
        maven("https://maven.fabricmc.net/")
        // NeoForge
        maven("https://maven.neoforged.net/releases") {
            mavenContent { releasesOnly() }
        }
        // Forge
//        maven("https://maven.minecraftforge.net/")
//        maven("https://files.minecraftforge.net/maven/")
        // Architectury
        maven("https://maven.architectury.dev/")
        // Deps
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/releases/")
    }
    tasks {
        named<Jar>("jar") {
            from(project.rootProject.file("LICENSE"))
        }
    }
}