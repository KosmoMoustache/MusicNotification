plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation("dev.kikugie:stonecutter:${gradle.parent!!.rootProject.providers.gradleProperty("stonecutter_version").get()}")
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.21")
}
