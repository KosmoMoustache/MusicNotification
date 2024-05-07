package net.kosmo.music;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
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
    public static final Identifier MUSICS_JSON_ID = new Identifier(MOD_ID, "musics.json");

    public static KeyBinding keyBinding;
    public static SoundManager soundManager;
    public static MinecraftClient client;
    public static MusicManager musicManager;
    public static ModConfig config;

    public static MusicHistory musicHistory = new MusicHistory();

    @Nullable
    public static SoundInstance currentlyPlaying;

    public static SoundInstanceListener SoundListener = (soundInstance, weightedSoundSet, range) -> {
        if (soundInstance.getCategory() != SoundCategory.MUSIC) return;

        Sound sound = soundInstance.getSound();
        Identifier identifier = sound.getIdentifier();

        MusicManager.Music music = ClientMusic.musicManager.get(identifier);

        if (music != null) {
            MusicToast.show(MinecraftClient.getInstance().getToastManager(), music);
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
            MusicToast.show(MinecraftClient.getInstance().getToastManager(), m);
        }
    };


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
                musicManager.reload();
            }
        });

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Key Binding
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.musicnotification.open_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.musicnotification.categories"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(new JukeboxScreen(client.currentScreen));
            }
        });
    }

    public static void onClientInit() {
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        musicManager = new MusicManager(client.getResourceManager());
        musicManager.reload();
    }

    /**
     * Inject when a music disc is played
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                MusicManager.Music music = ClientMusic.musicManager.get(musicDiscItem.getSound().getId());

                if (music != null) {
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), music);
                } else {
                    LOGGER.info("Unknown music disc {}", musicDiscItem.getSound().getId());

                    AtomicReference<String> namespace = new AtomicReference<>(musicDiscItem.getSound().getId().getNamespace());
                    FabricLoader.getInstance().getModContainer(namespace.get()).ifPresent(modContainer -> namespace.set(modContainer.getMetadata().getName()));


                    String[] string = musicDiscItem.getDescription().getString().split(" - ");
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep;
                    MusicManager.Music m = new MusicManager.Music(
                            musicDiscItem.getSound().getId(),
                            null,
                            string[0],
                            string[1],
                            namespace.get(),
                            AlbumCover.GENERIC,
                            false
                    );
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), m);
                }
            }
            LOGGER.debug("Playing music disc: {}", song.getId());
        }
    }

    /**
     * Parse a JSON Resource
     */
    public static JsonObject parseJSONResource(Resource resource) throws IOException, JsonParseException {
        BufferedReader reader = resource.getReader();
        return JsonHelper.deserialize(reader);
    }

    /**
     * Return false if either MASTER or MUSIC volume is set to 0
     */
    public static boolean isVolumeZero() {
        if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MUSIC) == 0f) return false;
        if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER) == 0f) return false;
        return true;
    }

    public static void playAndResetTracker(MinecraftClient client, MusicManager.Music music) {
        SoundEvent soundEvent = music.getSoundEvent(ClientMusic.soundManager);
        if (soundEvent == null) {
            ClientMusic.LOGGER.warn("Unable to play unknown sound with id: {}", music.customId == null ? music.identifier : music.customId);
            return;
        }

        PositionedSoundInstance soundInstance = PositionedSoundInstance.music(soundEvent);
        client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        IMixinMusicTracker musicTracker = (IMixinMusicTracker) client.getMusicTracker();
        musicTracker.setCurrent(soundInstance);
//        musicTracker.setTimeUntilNextSong(Integer.MAX_VALUE);
        client.getSoundManager().play(soundInstance);
//        ClientMusic.musicHistory.addMusic(music);
        ClientMusic.currentlyPlaying = soundInstance;
    }


    /**
     * Draw a scrollable text
     */
    public static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow) {
        drawScrollableText(context, textRenderer, text, centerX, startX, startY, endX, endY, color, shadow, startX, startY, endX, endY);
    }

    public static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow, int clipAreaX1, int clipAreaY1, int clipAreaX2, int clipAreaY2) {
        int i = textRenderer.getWidth(text);
        int j = (startY + endY - textRenderer.fontHeight) / 2 + 1;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMeasuringTimeMs() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = MathHelper.lerp(f, 0.0, l);

            context.enableScissor(clipAreaX1, clipAreaY1, clipAreaX2, clipAreaY2);
//            context.fill(clipAreaX1,clipAreaY1,clipAreaX2,clipAreaY2, Colors.RED);
            context.drawText(textRenderer, text.asOrderedText(), startX - (int) g, j, color, shadow);
            context.disableScissor();
        } else {
            int l = MathHelper.clamp(centerX, startX + i / 2, endX - i / 2);

            OrderedText orderedText = text.asOrderedText();
            context.drawText(textRenderer, orderedText, l - textRenderer.getWidth(orderedText) / 2, j, color, shadow);
        }
    }
}