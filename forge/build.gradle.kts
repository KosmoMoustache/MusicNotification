plugins {
    alias(libs.plugins.shadow)
}
//
repositories {
    maven {
        name = "MinecraftForge"
        url = uri("https://files.minecraftforge.net")
    }
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("musicnotification.mixins.json")
        mixinConfig("musicnotification.mixins.common.json")
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentForge: Configuration = configurations.getByName("developmentForge")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentForge.extendsFrom(configurations["common"])
}

dependencies {
    "forge"("net.minecraftforge:forge:${project.property("minecraft_version")}-${project.property("forge_version")}")

    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") {
        version {
            strictly("5.0.4")
        }
    }

//    minecraft("net.minecraftforge:forge:${project.property("forge_version")}")
//    modImplementation("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")

    api("me.shedaniel.cloth:cloth-config-forge:13.0.121") {
        exclude("net.sf.jopt-simple:jopt-simple")
    }

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionForge")) { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}