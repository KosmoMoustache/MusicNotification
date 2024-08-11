architectury {
    neoForge()
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentNeoForge"].extendsFrom(this)
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

dependencies {
    common(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }

    val neoforgeVersion: String by project
    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    val clothConfigVersion: String by project
    api( group = "me.shedaniel.cloth", name = "cloth-config-neoforge", version = clothConfigVersion )
}

//tasks {
//    processResources {
////        inputs.property("version", project.version)
////
////        filesMatching("META-INF/neoforge.mods.toml") {
////            expand("version" to project.version)
////        }
//
//        from(rootProject.file("common/src/main/resources")) {
//            include("**/**")
//            duplicatesStrategy = DuplicatesStrategy.WARN
//        }
//    }
//
////    shadowJar {
////        exclude("architectury.common.json")
////        configurations = listOf(project.configurations["shadowCommon"])
////        archiveClassifier.set("dev-shadow")
////    }
////
////    remapJar {
////        injectAccessWidener.set(true)
////        inputFile.set(shadowJar.flatMap { it.archiveFile })
////        dependsOn(shadowJar)
////    }
////
////    jar {
////        archiveClassifier.set("dev")
////    }
////
////    sourcesJar {
////        val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
////        dependsOn(commonSources)
////        from(commonSources.archiveFile.map { zipTree(it) })
////    }
//}
