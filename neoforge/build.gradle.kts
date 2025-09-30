plugins {
    `multiloader-loader`
    id("net.neoforged.moddev")
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.18"
}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }
}

neoForge {
    enable {
        version = commonMod.dep("neoforge")
    }
}

dependencies {
    // Required dependencies
//	implementation("group:${commonMod.dep("key")}")

    api( group = "me.shedaniel.cloth", name = "cloth-config-neoforge", version = commonMod.dep("cloth_config") )
}

neoForge {
    accessTransformers.from(project.file("../../src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

    runs {
        register("client") {
            client()
            ideName = "NeoForge Client (${project.path})"
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }

    parchment {
        commonMod.depOrNull("parchment")?.let {
            mappingsVersion = it
            minecraftVersion = commonMod.mcVersion
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

tasks {
    processResources {
        exclude("${mod.id}.accesswidener")
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(":neoforge:${commonMod.propOrNull("minecraft_version")}:stonecutterGenerate")
}


tasks.named("processResources") {
    // Rename neoforge.mods.toml to mods.toml
    if (stonecutterBuild.eval(stonecutterBuild.current.version, "<1.20.5")) {
        doLast {
            moveAndDeleteFileOrFolder(
                file("${layout.buildDirectory.get().toString()}/resources/main/META-INF"),
                "neoforge.mods.toml",
                "mods.toml",
            )
        }
    }

    // Rename function folder to functions for <1.21
    if (stonecutterBuild.eval(stonecutterBuild.current.version, "<1.21")) {
        doLast {
            moveAndDeleteFileOrFolder(
                file("${layout.buildDirectory.get().toString()}/resources/main/"),
                "data/musicnotification/function",
                "data/musicnotification/functions"
            )
        }
    }
}
