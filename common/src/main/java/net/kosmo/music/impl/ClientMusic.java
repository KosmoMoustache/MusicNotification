package net.kosmo.music.impl;

import net.kosmo.music.impl.config.ConfigHolder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientMusic {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");
    public static final ResourceLocation MUSICS_JSON_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "musics.json");

    public static KeyMapping keyBinding;
    public static SoundManager soundManager;
    public static MusicManager musicManager;
    public static ConfigHolder config;
    public static MusicHistory musicHistory = new MusicHistory();
    public static boolean isDarkModeEnabled = false;
    @Nullable
    public static SoundInstance currentlyPlaying;
    public static IModLoader modLoader;
    public static boolean initialized;

    public static void init(KeyMapping  keyMapping, IModLoader modLoader, ConfigHolder config) {
        LOGGER.info("Music Notification initialized");

        ClientMusic.keyBinding =  keyMapping;
        ClientMusic.modLoader = modLoader;
        ClientMusic.config = config;
    }

    public static void onClientInit(ResourceManager manager) {
        soundManager = Minecraft.getInstance().getSoundManager();
        musicManager = new MusicManager(manager);
    }

    public static String getModName(ResourceLocation location) {
        return modLoader.getModName(location);
    }
}
