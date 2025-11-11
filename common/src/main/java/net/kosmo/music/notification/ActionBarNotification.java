package net.kosmo.music.notification;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ActionBarNotification extends Notification {
	
	public void show(ResourceLocation resourceLocation, TrackData trackData) {
		if (canBeShown()) {
			return;
			// TODO: use fallback, put the check in NotificationManager
		}
		super.show(resourceLocation, trackData);
		// TODO: Allow customization of message time
		// TODO: Check if a message is already being displayed and queue the next one ?
		Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("text.musicnotification.notification.action_bar", trackData.title(), trackData.author()), Config.options().STYLE_ACTION_BAR_ANIMATE_COLOR);
	}

	static public boolean canBeShown() {
		Screen screen = Minecraft.getInstance().screen;
		return canShowOverlayMessage(screen);
	}

	static private boolean canShowOverlayMessage(Screen screen) {
		return screen == null || screen instanceof ChatScreen;
	}
}
