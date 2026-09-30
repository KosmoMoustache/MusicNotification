package net.kosmo.music;

import com.mojang.blaze3d.platform.InputConstants;
import net.kosmo.music.gui.JukeboxScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class KeyBinding {
	//? if >=1.21.10
	private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "category"));
	private static final KeyMapping openJukeboxScreenKey = new KeyMapping("key.musicnotification.open_screen", InputConstants.UNKNOWN.getValue(),
		/*? >=1.21.10 {*/CATEGORY/*?} else {*//*"key.category.musicnotification.category"*//*?}*/
	);

	public static void tick() {
		while (openJukeboxScreenKey.consumeClick()) {
			Minecraft client = Minecraft.getInstance();
			client.gui.setScreen(new JukeboxScreen());
		}
	}

	public static KeyMapping getKeyMapping() {
		return openJukeboxScreenKey;
	}
}
