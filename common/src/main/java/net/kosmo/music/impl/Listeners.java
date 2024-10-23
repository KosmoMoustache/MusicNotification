package net.kosmo.music.impl;

import net.kosmo.music.impl.toast.MusicToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import static net.kosmo.music.impl.Helper.isMatchedInList;

public class Listeners {
    public static class SoundManagerSoundEventListener implements SoundEventListener {
        public void onPlaySound(SoundInstance soundInstance, WeighedSoundEvents weighedSoundEvents, float f) {
            if (soundInstance.getSource() != SoundSource.MUSIC && soundInstance.getSource() != SoundSource.RECORDS) {
                return;
            }

            // Ignore sound events in the ignore list
            if (isMatchedInList(soundInstance.getLocation().toString(), ClientMusic.config.IGNORE_SOUND_EVENT)) {
                ClientMusic.LOGGER.debug("Sound Event ignored: {}", soundInstance.getLocation());
                return;
            }

            ResourceLocation identifier1 = soundInstance.getSound().getLocation();
            ResourceLocation identifier2 = soundInstance.getLocation();

            // Try 1 with identifier1
            MusicManager.Music managerMusic = ClientMusic.musicManager.get(identifier1);

            // Try 2 with identifier2
            if (managerMusic == null) {
                ClientMusic.LOGGER.info("Music not found with identifier: {} trying with {}", identifier1, identifier2);
                managerMusic = ClientMusic.musicManager.get(identifier2);
            }

            // Try 3 create a dummy music entry
            if (managerMusic == null) {
                ClientMusic.LOGGER.info("Unknown music {}", identifier1);

                String namespace = ClientMusic.getModName(identifier1);

                Helper.ResourceLocationParser parsed = new Helper.ResourceLocationParser(identifier1);
                managerMusic = new MusicManager.Music(
                        identifier1,
                        null,
                        parsed.title,
                        namespace,
                        identifier1.toString(),
                        AlbumCover.getDefaultCover(identifier1),
                        false
                );
            }

            MusicToast toast = Minecraft.getInstance().getToastManager().getToast(MusicToast.class, MusicToast.Type.DEFAULT);
            if (toast == null) {
                Minecraft.getInstance().getToastManager().addToast(new MusicToast(managerMusic));
            } else {
                toast.setContent(managerMusic);
            }

            // Add to history
            ClientMusic.musicHistory.addMusic(managerMusic);
        }
    }
}
