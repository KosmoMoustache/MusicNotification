package net.kosmo.music.notification;

import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;

public class VanillaNotification extends Notification {

	public void show(ResourceLocation resourceLocation, TrackData trackData) {
//		ToastManager tm = Minecraft.getInstance().getToastManager();
//		tm.createNowPlayingToast();
//		super.show(resourceLocation, trackData);
	}

	public static class Enhanced extends VanillaNotification {
	}
}
