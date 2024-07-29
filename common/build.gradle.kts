architectury {
    val enabled_platforms: String by rootProject
    common(enabled_platforms.split(","))
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating // Don't use shadow from the shadow plugin because we don't want IDEA to index this.

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
}

loom {
    accessWidenerPath = file("src/main/resources/musicnotification.accesswidener")
}

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
//    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_version")}")
}

tasks {
    processResources {
        exclude("*")
    }
}