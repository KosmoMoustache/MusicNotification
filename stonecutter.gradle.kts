import org.gradle.api.tasks.Copy

plugins {
	id("dev.kikugie.stonecutter")
	id("com.google.devtools.ksp") version "2.3.6" apply false
	id("net.neoforged.moddev") version "2.0.140" apply false
	id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
	id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22" apply false
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22" apply false
}
stonecutter active "26.1" /* [SC] DO NOT EDIT */

stonecutter parameters {
	replacements {
		string(current.parsed > "1.21.1") {
			replace("ToastComponent", "ToastManager")
			replace("pushPose", "pushMatrix")
			replace("popPose", "popMatrix")
		}

		string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
			replace("net.minecraft.Util", "net.minecraft.util.Util")
		}

		string(current.parsed > "26.0") {
			replace("GuiGraphics", "GuiGraphicsExtractor")
			replace("net.fabricmc.fabric.api.client.command.v2.ClientCommandManager", "net.fabricmc.fabric.api.client.command.v2.ClientCommands")
			replace("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper", "net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper")
			replace("KeyBindingHelper.registerKeyBinding", "KeyMappingHelper.registerKeyMapping")
		}
	}
}

tasks.register<Copy>("collectBuildFiles") {
	group = "build"
	description = "Collect built jars into the root output directory."
	into(layout.projectDirectory.dir("output"))
	from(allprojects.filter {
		it != rootProject &&
			it.childProjects.isEmpty() &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("common").asFile.toPath()) &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("versions").asFile.toPath())
	}.map { it.layout.buildDirectory.dir("libs") }) {
		include("**/*.jar")
	}
}
