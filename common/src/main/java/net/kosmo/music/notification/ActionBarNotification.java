package net.kosmo.music.notification;

import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ActionBarNotification extends Notification {

	static public boolean canBeShown() {
		Screen screen = Minecraft.getInstance().gui.screen();
		return screen == null || screen instanceof ChatScreen;
	}

	public void show(Identifier resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
		// TODO: Allow customization of message time
		// TODO: Check if a message is already being displayed and queue the next one ?
		Minecraft.getInstance().gui.hud.setOverlayMessage(Component.translatable("text.musicnotification.notification.action_bar", trackData.title(), trackData.author()), Config.options().STYLE_ACTION_BAR_ANIMATE_COLOR);
	}
}
