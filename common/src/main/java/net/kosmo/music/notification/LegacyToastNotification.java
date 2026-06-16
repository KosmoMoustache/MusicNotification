package net.kosmo.music.notification;

import net.kosmo.music.notification.toast.MusicToast;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.resources.Identifier;

public class LegacyToastNotification extends Notification {
	public void show(Identifier resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
		ToastManager tm = Minecraft.getInstance()/*? >1.21.1 {*/.gui.toastManager();/*?} else {*//*.getToasts();*//*?}*/
		MusicToast toast = tm.getToast(MusicToast.class, MusicToast.NO_TOKEN);
		if (toast == null) {
			tm.addToast(new MusicToast(trackData));
		} else {
			toast.setContent(trackData);
		}
	}
}
