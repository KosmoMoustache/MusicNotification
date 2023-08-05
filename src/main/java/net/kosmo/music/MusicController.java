package net.kosmo.music;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;

public class MusicController {
    private final HashMap<Identifier, SoundEvent> discEntriesByIdentifier = new HashMap<>();
    private final HashMap<Identifier, String> referenceEntriesBySoundEvent = new HashMap<>();

    public MusicController() {
        this.getFieldFromSoundSoundEvents();
    }

    public HashMap<Identifier, SoundEvent> getDiscs() {
        return this.discEntriesByIdentifier;
    }

    public HashMap<Identifier, String> getReferences() {
        return this.referenceEntriesBySoundEvent;
    }

    public HashMap<Identifier, SoundEvent> getEntries() {
        HashMap<Identifier, SoundEvent> entries = new HashMap<>();
        entries.putAll(this.discEntriesByIdentifier);

//        Set<String> a = ClientMusic.musicManager.getEntries().keySet();

//        for (String s : a) {
//            Identifier identifier = new Identifier("minecraft:sounds/music/game/an_ordinary_day");
//           SoundEvent soundEvent = SoundEvent.of(identifier);
//            entries.put(identifier, soundEvent);
//        }

        return entries;
    }

    private void getFieldFromSoundSoundEvents() {
        Arrays.stream(SoundEvents.class.getFields()).filter(field -> (field.getName().contains("MUSIC"))).forEach(field -> {
            try {
                if (field.getType().equals(SoundEvent.class)) {
                    SoundEvent soundEvent = (SoundEvent) field.get(null);
                    this.discEntriesByIdentifier.put(soundEvent.getId(), soundEvent);
                }

                if (field.getType().equals(RegistryEntry.Reference.class)) {
                    RegistryEntry.Reference<SoundEvent> reference = ClientMusic.castRegistryReference(field.get(null));
                    MusicSound a = new MusicSound(reference, 0,0, true);
                    ClientMusic.LOGGER.info("{}", a.getSound().value().getId());
                    this.referenceEntriesBySoundEvent.put(reference.registryKey().getRegistry(), "?");
                }
            } catch (IllegalAccessException e) {
                ClientMusic.LOGGER.info("Error when getting fields from SoundEvents: {}", e.getMessage());
            }
        });
    }
}
