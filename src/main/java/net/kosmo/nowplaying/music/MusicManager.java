package net.kosmo.nowplaying.music;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kosmo.nowplaying.NowPlaying;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.*;

public class MusicManager {
    private final ArrayList<Identifier> musicFieldIdentifiers = new ArrayList<>();
    private final ArrayList<Identifier> discFieldIdentifiers = new ArrayList<>();
    private final HashMap<String, MusicEntry> LIST = new HashMap<>();

    public MusicManager(JsonObject json) {
        this.update(json);
    }

    /**
     * Clear LIST and putAll from json object
     * Then get all music events from {@link SoundEvents}
     */
    public void update(JsonObject json) {
        this.LIST.clear();
        this.LIST.putAll(this.parseJson(json));
        this.getFromSoundEvents();
    }

    public HashMap<String, MusicEntry> getEntries() {
        // TODO Add fields
        return this.LIST;
    }

    public Collection<MusicEntry> getEntriesValue() {
        return this.LIST.values();
    }

    public MusicEntry getByIdentifier(Identifier identifier) {
        return this.LIST.get(identifier);
    }


    public MusicEntry getByKey(String string) {
        return this.LIST.get(string);
    }


    private Map<String, MusicEntry> parseJson(JsonObject json) {
        Map<String, MusicEntry> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), new MusicEntry(entry.getKey(), (JsonObject) entry.getValue()));
        }
        return map;
    }

    private void getFromSoundEvents() {
        this.getFromSoundEvents(false);
    }

    private void getFromSoundEvents(boolean findAll) {
        Arrays.stream(SoundEvents.class.getFields()).filter(field -> findAll || field.getName().contains("MUSIC")).forEach(field -> {
            try {
                if (field.getType().equals(SoundEvent.class)) {
                    SoundEvent soundEvent = (SoundEvent) field.get(field);
                    this.discFieldIdentifiers.add(soundEvent.getId());
                }

                if (field.getType().equals(RegistryEntry.Reference.class)) {
                    RegistryEntry.Reference<SoundEvent> reference = NowPlaying.castRegistryReference(field.get(field));
                    MusicSound a = new MusicSound(reference, 0, 0, true);
                    this.musicFieldIdentifiers.add(a.getSound().value().getId());
                }
            } catch (IllegalAccessException e) {
                NowPlaying.LOGGER.info("Error when getting fields from SoundEvents: {}", e.getMessage());
            }
        });

    }
}
