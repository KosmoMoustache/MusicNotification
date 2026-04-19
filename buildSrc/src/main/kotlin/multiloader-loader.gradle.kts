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
//		dependsOn(commonResources)


		// Only include the right aw file
		from(commonResources) {
			exclude("**/*.aw")
		}
		from(commonResources) {
			include("${mod.aw_version}.aw")
		}
	}
}
