architectury {
    val enabledPlatforms: String by rootProject
    common(enabledPlatforms.split(","))
}

loom {
    accessWidenerPath = file("src/main/resources/musicnotification.accesswidener")
}

dependencies {
    val fabricLoaderVersion: String by project(":fabric").dependencyProject
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Architectury API. This is optional, and you can comment it out if you don't need it.
//    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_version")}")
}