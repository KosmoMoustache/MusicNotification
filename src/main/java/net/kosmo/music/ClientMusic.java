package net.kosmo.music;

import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kosmo.music.gui.PlaySoundScreen;
import net.kosmo.music.toast.MusicToast;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.ModConfig;
import net.kosmo.music.utils.resource.KResourceManager;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");
    public static final Identifier MUSICS_JSON_ID = new Identifier(MOD_ID, "musics.json");

    public static KeyBinding keyBinding;
    public static SoundManager soundManager;
    public static MinecraftClient client;
    public static KResourceManager kResourceManager;
    public static ModConfig config;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Music Notification initialized");
        client = MinecraftClient.getInstance();

        // Resource Loader
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return MUSICS_JSON_ID;
            }

            @Override
            public void reload(ResourceManager manager) {
               kResourceManager.reload();
            }
        });

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void onClientInit() {
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        kResourceManager = new KResourceManager(client.getResourceManager());
        kResourceManager.reload();
    }

    /**
     * When music disc is played
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                MusicManager.Music music = kResourceManager.musicManager.get(musicDiscItem.getSound().getId());
                if (music != null) {
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), music);
                } else {
                    LOGGER.warn("Unknown {} music disc in musics.json", musicDiscItem.getSound().getId());
                    String[] string = musicDiscItem.getDescription().getString().split(" - ");
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(musicDiscItem.getSound().getId().getNamespace()), AlbumCover.GENERIC);
                }
            }
            LOGGER.info("Playing music disc: {}", song.getId());
        }
    }

    public static SoundInstanceListener SoundListener = (soundInstance, soundSet, range) -> {
        if (soundInstance.getCategory() == SoundCategory.MUSIC) {
            MusicToast.show(soundInstance.getSound());
        }
    };

    /**
     * Check if the toast should be shown
     */
    public static boolean canShowToast() {
        if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MUSIC) == 0f) return false;
        if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER) == 0f) return false;
        return true;
    }
}