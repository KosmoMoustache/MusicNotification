plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

base.archivesName.set("${rootProject.property("archives_base_name").toString()}-fabric")

loom {
    accessWidenerPath.set(project(":platforms:common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.
val developmentFabric: Configuration = configurations.getByName("developmentFabric")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

repositories {
    maven("https://maven.shedaniel.me/") // cloth config
    maven("https://maven.terraformersmc.com/releases/") // mod menu
    maven("https://maven.quiltmc.org/repository/release") // quilt
}

dependencies {
    modImplementation("org.quiltmc:quilt-loader:${rootProject.property("quilt_loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("org.quiltmc.qsl.core:lifecycle_events:${rootProject.property("quilt_loader_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")

    common(project(":platforms:common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(":platforms:common", configuration = "transformProductionFabric")) { isTransitive = false }

    // ModMenu
    modImplementation("com.terraformersmc:modmenu:${rootProject.property("mod_menu_version")}")
    // Cloth Config
//    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${rootProject.property("cloth_config_version")}") {
//        exclude("net.fabricmc.fabric-api")
//    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("quilt.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")
        configurations = listOf(project.configurations["shadowCommon"])
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        dependsOn(shadowJar)
        archiveClassifier.set("quilt")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":platforms:common").tasks.getByName<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}