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
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.kosmo.music.gui.JukeboxScreen;
import net.kosmo.music.mixin.IMixinMusicTracker;
import net.kosmo.music.toast.MusicToast;
import net.kosmo.music.utils.ModConfig;
import net.kosmo.music.utils.MusicHistory;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.RecordItem;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");
    public static final ResourceLocation MUSICS_JSON_ID = new ResourceLocation(MOD_ID, "musics.json");

    public static KeyMapping keyBinding;
    public static SoundManager soundManager;
    public static Minecraft client;
    public static MusicManager musicManager;
    public static ModConfig config;

    public static MusicHistory musicHistory = new MusicHistory();

    @Nullable
    public static SoundInstance currentlyPlaying;

    public static SoundEventListener SoundListener = (soundInstance, soundSet, range) -> {
        if (soundInstance.getSource() != SoundSource.MUSIC) return;

        Sound sound = soundInstance.getSound();
        ResourceLocation identifier = sound.getLocation();

        MusicManager.Music music = ClientMusic.musicManager.get(identifier);

        if (music != null) {
            MusicToast.show(Minecraft.getInstance().getToasts(), music);
        } else {
            LOGGER.info("Unknown music {}", identifier.toString());

            AtomicReference<String> namespace = new AtomicReference<>(identifier.getNamespace());
            FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));

            String[] idSplit = identifier.getPath().split("/");
            MusicManager.Music m = new MusicManager.Music(
                    identifier,
                    null,
                    idSplit[idSplit.length - 1],
                    namespace.get(),
                    identifier.toString(),
                    Objects.equals(namespace.get(), "Minecraft") ? AlbumCover.GENERIC : AlbumCover.MODDED,
                    false
            );
            MusicToast.show(Minecraft.getInstance().getToasts(), m);
        }
    };


    @Override
    public void onInitializeClient() {
        LOGGER.info("Music Notification initialized");
        client = Minecraft.getInstance();

        // Resource Loader
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return MUSICS_JSON_ID;
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                musicManager.reload();
            }
        });

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.musicnotification.open_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.musicnotification.categories"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                client.setScreen(new JukeboxScreen(client.screen));
            }
        });
    }

    public static void onClientInit() {
        soundManager = client.getSoundManager();
        soundManager.addListener(SoundListener);
        musicManager = new MusicManager(client.getResourceManager());
        musicManager.reload();
    }

    /**
     * Inject when a music disc is played
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null) {
            RecordItem musicDiscItem = RecordItem.getBySound(song);
            if (musicDiscItem != null) {
                MusicManager.Music music = ClientMusic.musicManager.get(musicDiscItem.getSound().getLocation());

                if (music != null) {
                    MusicToast.show(Minecraft.getInstance().getToasts(), music);
                } else {
                    LOGGER.info("Unknown music disc {}", musicDiscItem.getSound().getLocation());

                    AtomicReference<String> namespace = new AtomicReference<>(musicDiscItem.getSound().getLocation().getNamespace());
                    FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));


                    String[] string = musicDiscItem.getDisplayName().getString().split(" - ");
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep;
                    MusicManager.Music m = new MusicManager.Music(
                            musicDiscItem.getSound().getLocation(),
                            null,
                            string[0],
                            string[1],
                            namespace.get(),
                            AlbumCover.GENERIC,
                            false
                    );
                    MusicToast.show(Minecraft.getInstance().getToasts(), m);
                }
            }
            LOGGER.debug("Playing music disc: {}", song.getLocation());
        }
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
        };

        SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent);
        client.getSoundManager().stop(null, SoundSource.MUSIC);
        IMixinMusicTracker musicTracker = (IMixinMusicTracker) client.getMusicManager();
        musicTracker.setCurrentMusic(soundInstance);
//        musicTracker.setTimeUntilNextSong(Integer.MAX_VALUE);
        client.getSoundManager().play(soundInstance);
//        ClientMusic.musicHistory.addMusic(music);
        ClientMusic.currentlyPlaying = soundInstance;
    }


    /**
     * Draw a scrollable text
     */
    public static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow) {
        drawScrollableText(context, textRenderer, text, centerX, startX, startY, endX, endY, color, shadow, startX, startY, endX, endY);
    }

    public static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow, int clipAreaX1, int clipAreaY1, int clipAreaX2, int clipAreaY2) {
        int i = textRenderer.width(text);
        int j = (startY + endY - textRenderer.lineHeight) / 2 + 1;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, (double) l);

            context.enableScissor(clipAreaX1, clipAreaY1, clipAreaX2, clipAreaY2);
//            context.fill(clipAreaX1,clipAreaY1,clipAreaX2,clipAreaY2, Colors.RED);
            context.drawString(textRenderer, text.getVisualOrderText(), startX - (int) g, j, color, shadow);
            context.disableScissor();
        } else {
            int l = Mth.clamp(centerX, startX + i / 2, endX - i / 2);

            FormattedCharSequence orderedText = text.getVisualOrderText();
            context.drawString(textRenderer, orderedText, l - textRenderer.width(orderedText) / 2, j, color, shadow);
        }
    }
}