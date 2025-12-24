package net.kosmo.music;

import net.kosmo.music.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	//? if <=1.21.8 {
	/*private static final KeyMapping openJukeboxScreenKey = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.category.musicnotification.category");
	 *///?} else {
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "category"));
	private static final KeyMapping openJukeboxScreenKey = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, CATEGORY);
	//?}

	public static void tick() {
		while (openJukeboxScreenKey.consumeClick()) {
			Minecraft client = Minecraft.getInstance();
			client.setScreen(new JukeboxScreen(client.screen));
		}
	}

	public static KeyMapping getKeyMapping() {
		return openJukeboxScreenKey;
	}
}
