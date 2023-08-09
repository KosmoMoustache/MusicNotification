package net.kosmo.nowplaying.music;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class NowPlayingSoundListener implements SoundInstanceListener {

    public NowPlayingSoundListener(SoundManager soundManager) {
        soundManager.registerListener(this);
    }

    public static void onDiscPlay(@Nullable SoundEvent sound) {
        if (sound != null && NowPlaying.config.SHOULD_SHOW_JUKEBOX && NowPlaying.shouldShowToast(NowPlayingToast.Type.DISC)) {
            MusicDiscItem musicDiscItem = MusicDiscItem.bySound(sound);
            if (musicDiscItem != null) {
                String namespace = musicDiscItem.getSound().getId().getNamespace();
                String description = musicDiscItem.getDescription().getString();
                String[] string = description.split(" - ");
                if (namespace.equals("minecraft")) {
                    // use music_list.json to get the author and soundtrack
                    // string[0] = title / string[1] = author | Now playing: Lena Raine - Pigstep
                    MusicManager.Entry entry = NowPlaying.musicManager.getEntry(string[1].toLowerCase());
                    NowPlayingToast.show(MinecraftClient.getInstance().getToastManager(), entry, NowPlayingToast.Type.DISC);
                } else {
                    NowPlayingToast.show(MinecraftClient.getInstance().getToastManager(), Text.literal(string[1]), Text.literal(string[0]), Text.literal(namespace), NowPlayingToast.AlbumCover.MODDED_CD, NowPlayingToast.Type.DISC);
                }
            }
            NowPlaying.LOGGER.info("Playing music disc: {}", sound.getId());
        }
    }

    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        if (sound.getCategory() == SoundCategory.MUSIC && NowPlaying.shouldShowToast(NowPlayingToast.Type.DEFAULT)) {
            NowPlaying.tracker.getHistory().add(sound);
            NowPlaying.LOGGER.info("Now playing: {}", sound.getSound().getIdentifier());
            NowPlayingToast.show(sound, NowPlayingToast.Type.DEFAULT);
        }
    }
}
