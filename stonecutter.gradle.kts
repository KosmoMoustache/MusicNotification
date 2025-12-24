plugins {
	id("dev.kikugie.stonecutter")

	id("net.neoforged.moddev") version "2.0.126" apply false
	id("fabric-loom") version "1.14-SNAPSHOT" apply false
//    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
stonecutter active "1.21.7" /* [SC] DO NOT EDIT */

stonecutter parameters {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("net.minecraft.Util", "net.minecraft.util.Util")
	}
}