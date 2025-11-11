package net.kosmo.music.notification;

import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;

public class VanillaNotification extends Notification {

	static public boolean canBeShown() {
		return false;
	}

	public void show(ResourceLocation resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
//		ToastManager tm = Minecraft.getInstance().getToastManager();
//		tm.createNowPlayingToast();
//		super.show(resourceLocation, trackData);
	}

	public static class Enhanced extends VanillaNotification {
	}
}
