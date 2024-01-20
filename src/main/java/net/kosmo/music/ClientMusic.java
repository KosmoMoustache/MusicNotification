package net.kosmo.music;

import com.google.gson.JsonObject;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kosmo.music.toast.MusicToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
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
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

    public static SoundManager soundManager;
    public static ResourceManager resourceManager;
    public static MinecraftClient client;
    public static MusicManager musicManager;
    public static ModConfig config;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Music Notification initialized");
        client = MinecraftClient.getInstance();

        // Resource Loader
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(MOD_ID, "musics.json");
            }

            @Override
            public void reload(ResourceManager manager) {
                musicManager.setMusicEntries(resourceLoader(manager));
            }
        });

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    }

    public static void onClientInit() {
        resourceManager = client.getResourceManager();
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        musicManager = new MusicManager(resourceLoader(resourceManager));
    }


    /**
     * Used to music discs
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                String[] disc_name = musicDiscItem.getSound().getId().toString().split("\\.");
                MusicManager.Entry entry = musicManager.getEntry(disc_name[disc_name.length - 1].toLowerCase());
                if (entry != null) {
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), entry);
                } else {
                    LOGGER.info("Disc not found in musics.json, showing default generic information for {}", musicDiscItem.getSound().getId());
                    String[] string = musicDiscItem.getDescription().getString().split(" - ");
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(musicDiscItem.getSound().getId().getNamespace()), MusicToast.AlbumCover.MODDED_CD);
                }
            }
            LOGGER.info("Playing music disc: {}", song.getId());
        }

    }

    public static SoundInstanceListener SoundListener = (soundInstance, soundSet, range) -> {
        if (soundInstance.getCategory() == SoundCategory.MUSIC) {
            MusicToast.show(soundInstance);
        }
    };

    /**
     * Get the last segment of an Identifier path
     */
    public static String getLastSegmentOfPath(Identifier identifier) {
        String[] path = identifier.getPath().split("/");
        return path[path.length - 1];
    }

    /**
     * Load musics.json from resource pack
     */
    public static JsonObject resourceLoader(ResourceManager manager) {
        Optional<Resource> resource = manager.getResource(new Identifier(MOD_ID, "musics.json"));
        if (resource.isPresent()) {
            Resource resource1 = resource.get();
            try (BufferedReader reader = resource1.getReader()) {
                return JsonHelper.deserialize(reader);
            } catch (IOException e) {
                ClientMusic.LOGGER.warn("Invalid musics.json in resourcepack: '{}'", resource1.getResourcePackName());
            }
        }
        return new JsonObject();
    }
}