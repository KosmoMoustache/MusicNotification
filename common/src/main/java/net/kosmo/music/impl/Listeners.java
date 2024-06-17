package net.kosmo.music.impl;

import net.kosmo.music.impl.toast.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class Listeners {
    public static class SoundManagerSoundEventListener implements SoundEventListener {
        public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents, float f) {
            if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.RECORDS) {
                return;
            }

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

                String namespace = ClientMusic.getModName(identifier1);

                Helper.ResourceLocationParser parsed = new Helper.ResourceLocationParser(identifier1);
                MusicManager.Music m = new MusicManager.Music(
                        identifier1,
                        null,
                        parsed.title,
                        namespace,
                        identifier1.toString(),
                        AlbumCover.getDefaultCover(identifier1),
                        false
                );
                MusicToast.show(Minecraft.getInstance().getToasts(), m);
            }
        }
    }
}
