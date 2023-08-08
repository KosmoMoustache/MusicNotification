package net.kosmo.nowplaying;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kosmo.nowplaying.gui.PlaySoundScreen;
import net.kosmo.nowplaying.mixin.IMixinMusicTracker;
import net.kosmo.nowplaying.music.MusicManager;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class NowPlaying implements ClientModInitializer {
    public static final String MOD_ID = "nowplaying";
    public static final Logger LOGGER = LoggerFactory.getLogger("NowPlaying");

    public static KeyBinding keyBinding;
    public static SoundManager soundManager;
    public static ResourceManager resourceManager;
    public static MinecraftClient client;
    public static MusicManager musicManager;
    public static NowPlayingConfig config;

    public static MusicController musicController;

    @Nullable
    public static SoundInstance nowPlaying;

    public static void onClientInit() {
        resourceManager = client.getResourceManager();
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        musicManager = new MusicManager(ResourceLoader.loader(resourceManager));
        musicController = new MusicController();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Now Playing initialized");
        client = MinecraftClient.getInstance();

        // Config
        AutoConfig.register(NowPlayingConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(NowPlayingConfig.class).getConfig();

        // Resource Loader
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceLoader.ResourceReloadListener());

        // TODO: Move to ModConfig ?
        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nowplaying.open_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "nowplaying.key.category"
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
    public static boolean shouldShowToast(NowPlayingToast.Type type) {
        if (!config.ENABLE_MOD) return false;
        if (NowPlaying.client.options.getSoundVolume(SoundCategory.MASTER) == 0) return false;

        if (type == NowPlayingToast.Type.DISC && NowPlaying.client.options.getSoundVolume(SoundCategory.RECORDS) == 0)
            return false;
        if (type == NowPlayingToast.Type.DEFAULT && NowPlaying.client.options.getSoundVolume(SoundCategory.MUSIC) == 0)
            return false;

        return true;
    }

    /**
     * Used to music discs
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null && NowPlaying.shouldShowToast(NowPlayingToast.Type.DISC)) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                String namespace = musicDiscItem.getSound().getId().getNamespace();
                Text text = musicDiscItem.getDescription();
                String[] string = text.getString().split(" - ");
                if (namespace.equals("minecraft")) { // use music_list.json to get the author and soundtrack
                    MusicManager.Entry entry = musicManager.getEntry(string[1].toLowerCase());
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    NowPlayingToast.show(MinecraftClient.getInstance().getToastManager(), entry, NowPlayingToast.Type.DISC);
                } else {
                    NowPlayingToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(namespace), NowPlayingToast.AlbumCover.MODDED_CD, NowPlayingToast.Type.DISC);
                }
            }
            LOGGER.info("Playing music disc: {}", song.getId());
        }
    }

    public static SoundInstanceListener SoundListener = (soundInstance, soundSet) -> {
        if (soundInstance.getCategory() == SoundCategory.MUSIC && NowPlaying.shouldShowToast(NowPlayingToast.Type.DEFAULT)) {
            NowPlayingToast.show(soundInstance, NowPlayingToast.Type.DEFAULT);
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
     * Cast to RegistryEntry.Reference<T> to avoid unchecked cast warning
     */
    @SuppressWarnings("unchecked")
    public static <T> RegistryEntry.Reference<T> castRegistryReference(Object obj) {
        return (RegistryEntry.Reference<T>) obj;
    }
}