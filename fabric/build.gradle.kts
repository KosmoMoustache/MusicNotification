plugins {
    id("fabric-loom")
    `multiloader-loader`
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.18"
}

fletchingTable {
    j52j.register("main") {
        extension("json", "**/*.json5")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mcVersion}")
//	mappings(loom.officialMojangMappings())
    mappings(loom.layered {
        officialMojangMappings()
        commonMod.depOrNull("parchment")?.let { parchmentVersion ->
            parchment("org.parchmentmc.data:parchment-${commonMod.mcVersion}:$parchmentVersion@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric-loader")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric-api")}+${commonMod.mcVersion}")

    modApi("com.terraformersmc:modmenu:${commonMod.depOrNull("modmenu")}")
    modApi("me.shedaniel.cloth:cloth-config-fabric:${commonMod.depOrNull("cloth_config")}")
}

loom {
    accessWidenerPath = common.project.file("../../src/main/resources/${mod.aw}")

    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
    }

    mixin {
        defaultRefmapName = "${mod.id}.refmap.json"
    }
}

//tasks.processResources {
//    if (stonecutterBuild.eval(stonecutterBuild.current.version, "<1.21")) {
//        doLast {
//            moveAndDeleteFileOrFolder(
//                file("${layout.buildDirectory.get().toString()}/resources/main/"),
//                "data/musicnotification/function",
//                "data/musicnotification/functions"
//            )
//        }
//    }
//}
