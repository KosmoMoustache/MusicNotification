package net.kosmo.music.notification;

import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notification {
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification/Notification");

	public static boolean canBeShown() {
		return true;
	}

	void show(Identifier resourceLocation, TrackData trackData, Class<? extends Notification> aClass) {
		LOGGER.info("Showing notification of {} using {} with {}", resourceLocation, aClass, trackData.toString());
	}

	void show(Identifier resourceLocation, TrackData trackData) {
		show(resourceLocation, trackData, this.getClass());
	}
}
