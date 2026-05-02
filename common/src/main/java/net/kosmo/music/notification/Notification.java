package net.kosmo.music.notification;

import com.mojang.logging.LogUtils;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class Notification {
	Logger LOGGER = LogUtils.getLogger();

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
