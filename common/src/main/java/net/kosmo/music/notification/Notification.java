package net.kosmo.music.notification;

import com.mojang.logging.LogUtils;
import net.kosmo.music.resource.TrackData;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class Notification {
	Logger LOGGER = LogUtils.getLogger();

	void show(ResourceLocation resourceLocation, TrackData trackData) {
		LOGGER.info("Showing notification of {} using {} - {}", resourceLocation, this, trackData.toString());
	}

	public static boolean canBeShown() {
		return true;
	}
}
