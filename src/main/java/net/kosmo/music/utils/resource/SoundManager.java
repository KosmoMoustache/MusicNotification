package net.kosmo.music.utils.resource;

import com.google.common.collect.Maps;
import net.kosmo.music.ClientMusic;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public class SoundManager {
    public Map<Identifier, SoundManager.Sound> sounds;

    public SoundManager() {
        this.sounds = Maps.newHashMap();
        this.fetchDataFromRegistry();
    }

    public void clear() {
        this.sounds.clear();
    }

    public void fetchDataFromRegistry() {
        this.sounds.clear();
        Registries.SOUND_EVENT.forEach(sound -> {
            ClientMusic.LOGGER.info("Adding sound: " + sound.getId());
            this.sounds.put(sound.getId(), new Sound(sound.getId()));
        });
    }

    public static class Sound {
        public final Identifier identifier;

        public Sound(Identifier identifier) {
            this.identifier = identifier;
        }
    }
}
