package net.kosmo.music;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kosmo.music.toast.MusicToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class ClientMusic implements ClientModInitializer {
    public static final String MOD_ID = "musicnotification";
    public static final Logger LOGGER = LoggerFactory.getLogger("MusicNotification");

    public static SoundManager soundManager;
    public static ResourceManager resourceManager;
    public static ModConfig config;
    public static MinecraftClient client;
    public static Collection<SoundInstance> musicHistory = new ArrayList<>();
    public static SoundInstance nowPlaying;
    public static MusicManager musicManager;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Music Notification initialized");
        client = MinecraftClient.getInstance();

        KeyInputHandler.register();

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void onClientInit() {
        resourceManager = client.getResourceManager();
        soundManager = client.getSoundManager();
        soundManager.registerListener(SoundListener);
        musicManager = new MusicManager(new DataManager(resourceManager).reader(DataManager.Type.MUSIC));
    }

    /**
     * Used to music discs
     */
    public static void onDiscPlay(SoundEvent song) {
        if (song != null) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
            if (musicDiscItem != null) {
                String namespace = musicDiscItem.getSound().getId().getNamespace();
                Text text = musicDiscItem.getDescription();
                String[] string = text.getString().split(" - ");
                if (namespace.equals("minecraft")) { // use musics.json to get the author and soundtrack
                    MusicManager.Entry entry = musicManager.getEntry(string[1].toLowerCase());
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), entry);
                } else {
                    MusicToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(namespace), MusicToast.AlbumCover.MODDED_CD);
                }
            }
        }
    }

    public static SoundInstanceListener SoundListener = (soundInstance, soundSet) -> {
        if (soundInstance.getCategory() == SoundCategory.MUSIC) {
            MusicToast.show(soundInstance);
        }
    };
}