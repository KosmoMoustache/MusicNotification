plugins {
	id("java")
	id("idea")
	id("multiloader-common")
}

val commonJava: Configuration by configurations.creating {
	isCanBeResolved = true
}
val commonResources: Configuration by configurations.creating {
	isCanBeResolved = true
}

dependencies {
	val commonPath = common.hierarchy.toString()
	compileOnly(project(path = commonPath))
	commonJava(project(path = commonPath, configuration = "commonJava"))
	commonResources(project(path = commonPath, configuration = "commonResources"))
}

tasks {
	named<JavaCompile>("compileJava") {
		dependsOn(commonJava)
		source(commonJava)
	}
	named<ProcessResources>("processResources") {
		dependsOn(commonResources)
		from(commonResources)
	}

	jar {
		exclude("accesswideners/**")
	}
}
