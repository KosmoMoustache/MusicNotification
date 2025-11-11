package net.kosmo.music;

import net.kosmo.music.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	private static final KeyMapping openJukeboxScreenKey = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");

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
