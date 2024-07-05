import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    alias(libs.plugins.architectury)
    alias(libs.plugins.architectury.loom) apply false
//    id("architectury-plugin") version "3.4-SNAPSHOT"
//    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
//    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
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
        version = "${version}+minecraft-${rootProject.property("minecraft_version")}-${project.property("loom.platform").toString()}"
    }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version")}")

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

//
//version = project.mod_version + "+" + project.minecraft_version
//group = project.maven_group
//
//
//
//loom {
//    accessWidenerPath.set(file('src/main/resources/musicnotification.accesswidener'))
//    mixin.defaultRefmapName.set("musicnotification-refmap.json")
//}
//
//repositories {
//    // Add repositories to retrieve artifacts from in here.
//    // You should only use this when depending on other mods because
//    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
//    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
//    // for more information about repositories.
//    maven {
//        name = "Cloth Config"
//        url = "https://maven.shedaniel.me/"
//    }
//    maven {
//        name = "TerraformerMC"
//        url = "https://maven.terraformersmc.com/releases/"
//    }
//}
//
//
//dependencies {
//    minecraft "com.mojang:minecraft:${project.minecraft_version}"
//    mappings loom.officialMojangMappings()
//    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
//
//    // Fabric API. This is technically optional, but you probably want it anyway.
//    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
//
//    // ModMenu
//    modImplementation("com.terraformersmc:modmenu:${project.mod_menu_version}")
//
//    // Cloth Config
//    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${project.cloth_config_version}") {
//        exclude(group: "net.fabricmc.fabric-api")
//    }
//}
//
//processResources {
//
//    filesMatching("fabric.mod.json") {
//        expand "mod_version": project.mod_version
//    }
//}
//
//tasks.withType(JavaCompile).configureEach {
//    it.options.release.set(21)
//}
//
//java {
//    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
//    // if it is present.
//    // If you remove this line, sources will not be generated.
//    withSourcesJar()
//
//    sourceCompatibility = JavaVersion.VERSION_21
//    targetCompatibility = JavaVersion.VERSION_21
//}
//
//jar {
//    from("LICENSE") {
//        rename { "${it}_${project.base.archivesName.get()}"}
//    }
//}
//
//tasks.register('checkProperties') {
//    doLast {
//        def props = new Properties()
//        file('gradle.properties').withInputStream { props.load(it) }
//
//        def json = new JsonSlurper().parse(file('src/main/resources/fabric.mod.json'))
//
//        def compareVersions = { propVersion, jsonVersion ->
//            if (jsonVersion.startsWith(">=")) {
//                jsonVersion = jsonVersion.substring(2)
//                return propVersion >= jsonVersion
//            } else {
//                return propVersion == jsonVersion
//            }
//        }
//
//        if (props['minecraft_version'].contains('w')) {
//            return;
//        }
//
//        if (!compareVersions(props['minecraft_version'], json['depends']['minecraft'])) {
//            throw new GradleException("The minecraft version in gradle.properties and fabric.mod.json do not match")
//        }
//
//        if (!compareVersions(props['loader_version'], json['depends']['fabricloader'])) {
//            throw new GradleException("The fabricloader version in gradle.properties and fabric.mod.json do not match")
//        }
//
//        if (!compareVersions(props['cloth_config_version'], json['depends']['cloth-config'])) {
//            throw new GradleException("The cloth-config version in gradle.properties and fabric.mod.json do not match")
//        }
//    }
//}
//
//tasks.named('build') {
//    dependsOn('checkProperties')
//}