package net.kosmo.music.util;

import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CommonComponents {
	public static SpriteIconButton jukebox(int width, Button.OnPress onPress, boolean iconOnly) {
		return SpriteIconButton.builder(Component.translatable("gui.musicnotification.jukebox.menu"), onPress, iconOnly)
			.width(width)
			.sprite(Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/jukebox"), 14, 14)
			.build();
	}
}
