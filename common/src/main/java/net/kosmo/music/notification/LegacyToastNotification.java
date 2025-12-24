package net.kosmo.music.notification;

import net.kosmo.music.notification.toast.MusicToast;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.resources.ResourceLocation;

public class LegacyToastNotification extends Notification {
	public void show(ResourceLocation resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
		ToastManager tm = Minecraft.getInstance().getToastManager();
		MusicToast toast = tm.getToast(MusicToast.class, MusicToast.NO_TOKEN);
		if (toast == null) {
			tm.addToast(new MusicToast(trackData));
		} else {
			toast.setContent(trackData);
		}
	}
}
