plugins {
	kotlin("jvm") version "2.3.20" apply false
	id("dev.kikugie.stonecutter")
	id("net.neoforged.moddev") version "2.0.140" apply false
	id("com.google.devtools.ksp") version "2.3.6" apply false

//	id("fabric-loom") version "1.15-SNAPSHOT" apply false
//    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
stonecutter active "26.1" /* [SC] DO NOT EDIT */

stonecutter parameters {
	replacements {
		string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
			replace("net.minecraft.Util", "net.minecraft.util.Util")
		}
		string(current.parsed > "26.0") {
			replace("accessWidener v2 named", "accessWidener v2 official")
			replace("GuiGraphics", "GuiGraphicsExtractor")
			replace("net.fabricmc.fabric.api.client.command.v2.ClientCommandManager", "net.fabricmc.fabric.api.client.command.v2.ClientCommands")
			replace("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper", "net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper")
			replace("KeyBindingHelper.registerKeyBinding", "KeyMappingHelper.registerKeyMapping")
		}
	}

	constants["cloth"] = true
}