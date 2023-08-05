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
import net.kosmo.music.mixin.IMixinMusicTracker;
import net.kosmo.music.toast.MusicToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

    public static KeyBinding keyBinding;
    public static SoundManager soundManager;
    public static ResourceManager resourceManager;
    public static MinecraftClient client;
    public static MusicManager musicManager;
    public static ModConfig config;

    public static MusicController musicController;

    public static SoundInstance nowPlaying;

    public static void onClientInit() {
        resourceManager = client.getResourceManager();
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        musicManager = new MusicManager(resourceLoader(resourceManager));
        musicController = new MusicController();
    }

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

        // TODO: Move to ModConfig ?
        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.musicnotification.open_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "kosmo.musicnotification.key"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(new PlaySoundScreen());
            }
        });
    }

    @Nullable
    public static SoundInstance getMusicTrackerCurrent(MinecraftClient client) {
        return ((IMixinMusicTracker) client.getMusicTracker()).getCurrent();
    }

    /**
     * If Music or Master volume is set to 0 don't show toast since Minecraft will try to play musics even if the volume is set to 0
     */
    public static boolean shouldShowToast(MusicToast.Type type) {
        if (!config.ENABLE_MOD) return false;
        if (ClientMusic.client.options.getSoundVolume(SoundCategory.MASTER) == 0) return false;

        if (type == MusicToast.Type.DISC && ClientMusic.client.options.getSoundVolume(SoundCategory.RECORDS) == 0)
            return false;
        if (type == MusicToast.Type.DEFAULT && ClientMusic.client.options.getSoundVolume(SoundCategory.MUSIC) == 0)
            return false;

        return true;
    }

    /**
     * Used to music discs
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null && ClientMusic.shouldShowToast(MusicToast.Type.DISC)) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                String namespace = musicDiscItem.getSound().getId().getNamespace();
                Text text = musicDiscItem.getDescription();
                String[] string = text.getString().split(" - ");
                if (namespace.equals("minecraft")) { // use musics.json to get the author and soundtrack
                    MusicManager.Entry entry = musicManager.getEntry(string[1].toLowerCase());
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), entry, MusicToast.Type.DISC);
                } else {
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(namespace), MusicToast.AlbumCover.MODDED_CD, MusicToast.Type.DISC);
                }
            }
            LOGGER.info("Playing music disc: {}", song.getId());
        }
    }

    public static SoundInstanceListener SoundListener = (soundInstance, soundSet) -> {
        if (soundInstance.getCategory() == SoundCategory.MUSIC && ClientMusic.shouldShowToast(MusicToast.Type.DEFAULT)) {
            MusicToast.show(soundInstance, MusicToast.Type.DEFAULT);
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

    /**
     * Cast to RegistryEntry.Reference<T> to avoid unchecked cast warning
     */
    @SuppressWarnings("unchecked")
    public static <T> RegistryEntry.Reference<T> castRegistryReference(Object obj) {
        return (RegistryEntry.Reference<T>) obj;
    }
}