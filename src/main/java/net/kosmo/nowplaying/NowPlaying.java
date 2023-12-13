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
import net.kosmo.nowplaying.music.*;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
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
    public static MusicManager musicManager;
    public static NowPlayingConfig config;
    public static Tracker tracker = new Tracker();
    private static MinecraftClient client;

    public static void onClientInit() {
        resourceManager = client.getResourceManager();
        soundManager = client.getSoundManager();
        new NowPlayingSoundListener(soundManager);
        musicManager = new MusicManager(ResourceLoader.loader(resourceManager));
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

        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.nowplaying.open_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.nowplaying.categories"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(new PlaySoundScreen());
            }
        });
    }

    /**
     * If Music or Master volume is set to 0 don't show toast since Minecraft will try to play musics even if the volume is set to 0
     */
    public static boolean shouldShowToast(NowPlayingToast.Type type) {
        if (!config.ENABLE_MOD) return false;
        if (client.options.getSoundVolume(SoundCategory.MASTER) == 0) return false;
        if (client.options.getSoundVolume(SoundCategory.RECORDS) == 0) return false;
        if (client.options.getSoundVolume(SoundCategory.MUSIC) == 0) return false;

        return true;
    }

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