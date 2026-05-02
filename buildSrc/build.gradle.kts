plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation("dev.kikugie:stonecutter:0.9.2")
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
}
