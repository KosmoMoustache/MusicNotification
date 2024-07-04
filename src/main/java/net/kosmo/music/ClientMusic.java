package net.kosmo.music;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.gui.JukeboxScreen;
import net.kosmo.music.mixin.IMixinMusicTracker;
import net.kosmo.music.toast.MusicToast;
import net.kosmo.music.utils.ModConfig;
import net.kosmo.music.utils.MusicHistory;
import net.kosmo.music.utils.Parser;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");
    public static final ResourceLocation MUSICS_JSON_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "musics.json");

    public static KeyMapping keyBinding;
    public static SoundManager soundManager;
    public static Minecraft client;
    public static MusicManager musicManager;
    public static ModConfig config;
    public static MusicHistory musicHistory = new MusicHistory();
    public static boolean isDarkModeEnabled = false;
    @Nullable
    public static SoundInstance currentlyPlaying;


    public static void onClientInit() {
        soundManager = client.getSoundManager();
        soundManager.addListener((soundInstance, soundSet, range) -> (new SoundManagerSoundEventListener()).onPlaySound(soundInstance, soundSet, range));
        musicManager = new MusicManager(client.getResourceManager());
        musicManager.reload();
    }

    /**
     * Parse a JSON Resource
     */
    public static JsonObject parseJSONResource(Resource resource) throws IOException, JsonParseException {
        BufferedReader reader = resource.openAsReader();
        return GsonHelper.parse(reader);
    }

    /**
     * Return false if either MASTER or MUSIC volume is set to 0
     */
    public static boolean isVolumeZero() {
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) return false;
        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) == 0f) return false;
        return true;
    }

    public static void playAndResetTracker(Minecraft client, MusicManager.Music music) {
        SoundEvent soundEvent = music.getSoundEvent(ClientMusic.soundManager);
        if (soundEvent == null) {
            ClientMusic.LOGGER.warn("Unable to play unknown sound with id: {}", music.customId == null ? music.identifier : music.customId);
            return;
        }

        SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent);
        client.getSoundManager().stop(null, SoundSource.MUSIC);
        IMixinMusicTracker musicTracker = (IMixinMusicTracker) client.getMusicManager();
        musicTracker.setCurrentMusic(soundInstance);
        client.getSoundManager().play(soundInstance);
        ClientMusic.currentlyPlaying = soundInstance;
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Music Notification initialized");
        client = Minecraft.getInstance();

        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.musicnotification.open_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.musicnotification.categories"));

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Register built-in resource pack
        FabricLoader.getInstance().getModContainer(MOD_ID)
                .ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode"), container,
                        Component.translatable("text.musicnotification.resourcepack.dark_mode.name"),
                        ResourcePackActivationType.NORMAL)
                );

        // Event Listeners
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ClientResourceListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ServerDataResourceListener());
        ClientPlayConnectionEvents.JOIN.register(new ClientPlayConnectionEventJoin());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });
    }

    public static class ClientResourceListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "client_resource_listener");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            ClientMusic.LOGGER.debug("ClientResource: Reloading MusicManager");
            ClientMusic.musicManager.reload();
            isDarkModeEnabled = manager.listPacks().anyMatch(resourcePack -> resourcePack.packId().equals(ResourceLocation.fromNamespaceAndPath(MOD_ID, "dark_mode").toString()));
        }
    }

    public static class ServerDataResourceListener implements SimpleSynchronousResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "server_data_listener");
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            ClientMusic.LOGGER.debug("ServerData: Reloading MusicManager");
            ClientMusic.musicManager.reload();
        }
    }

    public static class ClientPlayConnectionEventJoin implements ClientPlayConnectionEvents.Join {
        @Override
        public void onPlayReady(ClientPacketListener handler, PacketSender sender, Minecraft client) {
            ClientMusic.LOGGER.debug("ClientPlayConnectionEventJoin: Reloading MusicManager");
            ClientMusic.musicManager.reload();
        }
    }

    public static class SoundManagerSoundEventListener implements SoundEventListener {
        public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents, float f) {
            if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.RECORDS) {
                return;
            }

            // Ignore sound events in the ignore list
            if (isMatchedInList(soundInstance.getLocation().toString(), ClientMusic.config.TOAST_CONFIG.IGNORE_SOUND_EVENT)) {
                ClientMusic.LOGGER.info("DEBUG: Sound Event ignored: {}", soundInstance.getLocation());
                return;
            }

            ClientMusic.LOGGER.info("DEBUG: SoundManagerSoundEventListener: onPlaySound: {} {}", soundInstance.getLocation(), soundInstance.getSound().getLocation());

            ResourceLocation identifier1 = soundInstance.getSound().getLocation();
            ResourceLocation identifier2 = soundInstance.getLocation();

            MusicManager.Music music = ClientMusic.musicManager.get(identifier1);
            if (music == null) {
                ClientMusic.LOGGER.info("Music not found with identifier: {} trying with {}", identifier1, identifier2);
                music = ClientMusic.musicManager.get(identifier2);
            }

            if (music != null) {
                MusicToast.show(Minecraft.getInstance().getToasts(), music);
            } else {
                ClientMusic.LOGGER.info("Unknown music {}", identifier1);

                AtomicReference<String> namespace = new AtomicReference<>(identifier1.getNamespace());
                FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));

                Parser.ResourceLocationParser parsed = new Parser.ResourceLocationParser(identifier1);
                MusicManager.Music m = new MusicManager.Music(
                        identifier1,
                        null,
                        parsed.title,
                        namespace.get(),
                        identifier1.toString(),
                        AlbumCover.getDefaultCover(identifier1),
                        false
                );
                MusicToast.show(Minecraft.getInstance().getToasts(), m);
            }
        }
    }

    public static boolean isMatchedInList(String input, List<String> patterns) {
        for (String pattern : patterns) {
            // Replace * with .* to create a regex pattern
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            if (Pattern.matches(regex, input)) {
                return true;
            }
        }
        return false;
    }
}