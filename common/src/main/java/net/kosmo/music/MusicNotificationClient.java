package net.kosmo.music;

import net.kosmo.music.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MusicNotificationClient {
	public static final String MOD_ID = "musicnotification";
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

	public static PlatformHelper PLATFORM_HELPER;
	public static @Nullable SoundInstance currentlyPlaying;

	public static void init(PlatformHelper platformHelper) {
		LOGGER.info("Music Notification initialized");
		Config.getAndSave();
		MusicNotificationClient.PLATFORM_HELPER = platformHelper;
	}

	public static void tick() {
		tick(Minecraft.getInstance());
	}

	public static void tick(Minecraft minecraft) {
		KeyBinding.tick();
	}
}
