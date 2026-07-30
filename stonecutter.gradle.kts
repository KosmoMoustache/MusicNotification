import org.gradle.api.tasks.Copy

plugins {
	id("dev.kikugie.stonecutter")
	id("com.google.devtools.ksp") version "2.3.6" apply false
	id("net.neoforged.moddev") version "2.0.140" apply false
}
stonecutter active "26.2" /* [SC] DO NOT EDIT */

stonecutter parameters {
	replacements {
		string(current.parsed >= "1.21.2") {
			replace("graphics.blitSprite(", "graphics.blitSprite(RenderType::guiTextured,")
			replace("FastColor.as8BitChannel", "ARGB.white")
			replace("ToastComponent", "ToastManager")
			replace("FastColor.ARGB32.color", "ARGB.color");
			replace("import net.minecraft.util.FastColor;", "import net.minecraft.util.ARGB;");
		}
		string(current.parsed >= "1.21.6") {
			replace("graphics.blitSprite(RenderType::guiTextured,", "graphics.blitSprite(RenderPipelines.GUI_TEXTURED,")
			replace("pushPose", "pushMatrix")
			replace("popPose", "popMatrix")
		}

		string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
			replace("net.minecraft.Util", "net.minecraft.util.Util")
		}

		string(current.parsed >= "1.21.11", "extract_contents") {
			replace("renderWidget", "renderContents")
		}
		string(current.parsed >= "26.0", "extract_contents") {
			replace("renderContents", "extractContents")
		}

		string(current.parsed > "26.0") {
			replace("GuiGraphics", "GuiGraphicsExtractor")
			replace("net.fabricmc.fabric.api.client.command.v2.ClientCommandManager", "net.fabricmc.fabric.api.client.command.v2.ClientCommands")
			replace("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper", "net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper")
			replace("KeyBindingHelper.registerKeyBinding", "KeyMappingHelper.registerKeyMapping")
			replace("ResourceManagerHelper.registerBuiltinResourcePack", "ResourceLoader.registerBuiltinPack")
			replace("ResourcePackActivationType", "PackActivationType")
		}
		string(current.parsed >= "26.2") {
			replace("client.setScreen", "client.gui.setScreen")
			replace("this.minecraft.setScreen", "this.minecraft.gui.setScreen")
			replace("client.screen", "client.gui.screen()")
			replace("Minecraft.getInstance().screen", "Minecraft.getInstance().gui.screen()")
			replace("Minecraft.getInstance().gui.setOverlayMessage", "Minecraft.getInstance().gui.hud.setOverlayMessage")
			replace("getToastManager()", "gui.toastManager()")
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

tasks.register("runAllClients") {
	group = "build"
	description = "Run clients for all versions sequentially."
	val runClients = allprojects.filter {
		it != rootProject &&
			it.childProjects.isEmpty() &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("common").asFile.toPath()) &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("versions").asFile.toPath())
	}.mapNotNull { it.tasks.findByName("runClient") }
	runClients.forEachIndexed { index, task ->
		if (index > 0) {
			task.mustRunAfter(runClients[index - 1])
		}
	}
	dependsOn(runClients)
}

tasks.register("runAllClientsParallel") {
	group = "build"
	description = "Run clients for all versions in parallel."
	val runClients = allprojects.filter {
		it != rootProject &&
			it.childProjects.isEmpty() &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("common").asFile.toPath()) &&
			!it.projectDir.toPath().startsWith(rootProject.layout.projectDirectory.dir("versions").asFile.toPath())
	}.mapNotNull { it.tasks.findByName("runClient") }
	dependsOn(runClients)
}