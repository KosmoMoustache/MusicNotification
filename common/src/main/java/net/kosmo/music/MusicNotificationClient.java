package net.kosmo.music;

import net.kosmo.music.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MusicNotificationClient {
	public static final String MOD_ID = "musicnotification";
	public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

	public static PlatformHelper PLATFORM_HELPER;

	public static final KeyBinding keyBinding = new KeyBinding();

//    public static SoundManager soundManager;

//    public static boolean isDarkModeEnabled = false;

	@Nullable
	public static SoundInstance currentlyPlaying;
	public static boolean initialized;

//    public static String lastSong;
//    public static String currentSong;

	public static void init(PlatformHelper platformHelper) {
		LOGGER.info("Music Notification initialized");
		Config.getAndSave();
		MusicNotificationClient.PLATFORM_HELPER = platformHelper;
	}

	public static void tick(Minecraft minecraft) {
		MusicNotificationClient.tick();
	}

	public static void tick() {
		KeyBinding.tick();

//        currentSong = minecraft.getMusicManager().getCurrentMusicTranslationKey();
//        if (currentSong != null && !currentSong.equals(lastSong)) {
//            lastSong = currentSong;
//            LOGGER.info(currentSong, getNowPlayingString(currentSong).getString());
//        }

//        minecraft.getSoundManager().getAvailableSounds();
	}

	static Component getNowPlayingString(String currentSong) {
		return currentSong == null ? Component.empty() : Component.translatable(currentSong.replace("/", "."));
	}

	public void isDarkModeEnabled() {
		Minecraft.getInstance().getResourceManager().listPacks().anyMatch(resourcePack -> resourcePack.packId().equals("mod/" + ResourceLocation.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "resourcepacks/dark_mode")));
	}

	static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(
			MOD_ID,
			path
		);
	}


}
