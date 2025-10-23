package net.kosmo.music.notification;

import net.kosmo.music.GuiAccessor;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CompactNotification extends Notification {

	public void show(ResourceLocation resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData);
		GuiAccessor gui = (GuiAccessor) Minecraft.getInstance().gui;
		gui.musicNotification$setCompactNotificationMessage(
			Component.literal(trackData.title() + " - " + trackData.author())
		);
		gui.musicNotification$setCompactNotificationTime(60);
	}

	public static boolean canBeShown() {
		return true;
	}
}
