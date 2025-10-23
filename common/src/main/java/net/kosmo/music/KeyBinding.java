package net.kosmo.music;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	private static KeyMapping openJukeboxScreenKey = new KeyMapping("key.musicnotification.open_screen", GLFW.GLFW_KEY_M, "key.musicnotification.categories");

	public static void tick() {
		while (openJukeboxScreenKey.consumeClick()) {
			MusicNotificationClient.LOGGER.info("Key pressed!");
//            client.setScreen(new JukeboxScreen(client.screen));
		}
	}

	public static KeyMapping getKeyMapping() {
		return openJukeboxScreenKey;
	}
}
