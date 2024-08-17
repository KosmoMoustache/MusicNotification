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