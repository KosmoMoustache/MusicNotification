plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentNeoForge: Configuration = configurations.getByName("developmentNeoForge")


configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentNeoForge.extendsFrom(configurations["common"])
}

dependencies {
    neoForge(libs.neoforge.minecraft)

    api(libs.cloth.config.neoforge)

    // Architectury API. This is optional, and you can comment it out if you don't need it.
//    modImplementation("dev.architectury:architectury-neoforge:${rootProject.property("architectury_version")}")

    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", configuration = "transformProductionNeoForge")) { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand("version" to project.version)
        }

        from(rootProject.file("common/src/main/resources")) {
            include("**/**")
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
    }

    shadowJar {
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}
