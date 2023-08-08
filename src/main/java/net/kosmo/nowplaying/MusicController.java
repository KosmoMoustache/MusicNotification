package net.kosmo.nowplaying;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MusicController {
    private final ArrayList<Identifier> discEntriesByIdentifier = new ArrayList<>();
    private final ArrayList<Identifier> referenceEntriesBySoundEvent = new ArrayList<>();

    public MusicController() {
        this.getFieldFromSoundSoundEvents();
    }

    public List<Identifier> getDiscs() {
        return this.discEntriesByIdentifier;
    }

    public List<Identifier> getReferences() {
        return this.referenceEntriesBySoundEvent;
    }

    public List<Identifier> getEntries() {
        List<Identifier> entries = new ArrayList<>();
        entries.addAll(this.discEntriesByIdentifier);
        entries.addAll(this.referenceEntriesBySoundEvent);

        return entries;
    }

    private void getFieldFromSoundSoundEvents() {
        Arrays.stream(SoundEvents.class.getFields()).filter(field -> (field.getName().contains("MUSIC"))).forEach(field -> {
            try {
                if (field.getType().equals(SoundEvent.class)) {
                    SoundEvent soundEvent = (SoundEvent) field.get(null);
//                    if (soundEvent == null) return;
                    this.discEntriesByIdentifier.add(soundEvent.getId());
                }

                if (field.getType().equals(RegistryEntry.Reference.class)) {
                    RegistryEntry.Reference<SoundEvent> reference = NowPlaying.castRegistryReference(field.get(null));
//                    if (reference == null) return;
                    MusicSound a = new MusicSound(reference, 0,0, true);
                    this.referenceEntriesBySoundEvent.add(a.getSound().value().getId());
                }
            } catch (IllegalAccessException e) {
                NowPlaying.LOGGER.info("Error when getting fields from SoundEvents: {}", e.getMessage());
            }
        });
    }
}
