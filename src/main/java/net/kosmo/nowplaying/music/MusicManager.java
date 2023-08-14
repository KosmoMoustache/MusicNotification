package net.kosmo.nowplaying.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kosmo.nowplaying.NowPlaying;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MusicManager {
    private final HashMap<Identifier, MusicDiscItem> discItem = new HashMap<>();
    private final HashMap<Identifier, SoundEvent> soundEvent = new HashMap<>();
    private final HashMap<String, MusicEntry> MUSIC_LIST = new HashMap<>();

    public MusicManager(JsonObject json) {
        this.update(json);
        this.getDisc();
        this.getMusicFromRegistry();
    }

    /**
     * Clear LIST and putAll from json object
     * Then get all music events from {@link SoundEvents}
     */
    public void update(JsonObject json) {
        this.MUSIC_LIST.clear();
        this.MUSIC_LIST.putAll(this.parseJson(json));
    }

    public HashMap<String, MusicEntry> getEntries() {
        HashMap<String, MusicEntry> map = new HashMap<>();

       for (Map.Entry<Identifier, MusicDiscItem> entry : this.discItem.entrySet()) {
            // TODO: Optimize this
            Optional<Map.Entry<String, MusicEntry>> found = this.MUSIC_LIST.entrySet().stream().filter(entry1 -> entry.getKey().getPath().contains(entry1.getValue().key) && !entry1.getValue().isRandom).max(Map.Entry.comparingByKey());
            if (found.isPresent()) {
                map.put(found.get().getKey(), found.get().getValue());
            } else {
                String description = entry.getValue().getDescription().getString();
                String[] string = description.split(" - ");
                // string[0] = title / string[1] = author // -> Now playing: Lena Raine - Pigstep

                String title = string[0];
                String author = string.length >= 2 ?
                        string[1] : null;
                map.put(entry.getKey().getPath(), new MusicEntry(NowPlaying.getLastSegmentOfPath(entry.getKey()), entry.getKey(), title, author, null, false));
            }
        }

        for (Map.Entry<Identifier, SoundEvent> sound : this.soundEvent.entrySet()) {
            Optional<MusicEntry> found = this.getByKey(sound.getKey().getPath());

            if (found.isPresent()) {
                map.put(found.get().key, found.get());
            } else {
                String string = sound.getValue().getId().toString().replaceAll("minecraft:music.", "");
                map.put(sound.getKey().getPath(), new MusicEntry(NowPlaying.getLastSegmentOfPath(sound.getKey()), sound.getKey(), string, "Random", null, false));
            }
        }

        return map;
    }

    public Collection<MusicEntry> getEntriesValue() {
        return this.getEntries().values();
    }

    public Optional<MusicEntry> getByKey(String string) {
        return Optional.ofNullable(this.MUSIC_LIST.get(string));
    }


    private @NotNull Map<String, MusicEntry> parseJson(@NotNull JsonObject json) {
        Map<String, MusicEntry> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), new MusicEntry(entry.getKey(), (JsonObject) entry.getValue()));
        }
        return map;
    }

    /**
     * get the list of all music disc item found in the registry (work with modded music disc)
     */
    private void getDisc() {
        Set<Map.Entry<RegistryKey<Item>, Item>> set = Registries.ITEM.getEntrySet();
        for (Map.Entry<RegistryKey<Item>, Item> entry : set) {
            if (entry.getValue() instanceof MusicDiscItem) {
                this.discItem.put(((MusicDiscItem) entry.getValue()).getSound().getId(), (MusicDiscItem) entry.getValue());
            }
        }
    }

    public void getMusicFromRegistry() {
        Set<Map.Entry<RegistryKey<SoundEvent>, SoundEvent>> set = Registries.SOUND_EVENT.getEntrySet();
        for (Map.Entry<RegistryKey<SoundEvent>, SoundEvent> entry : set) {
            if (entry.getValue().getId().getPath().contains("music.")) {
                soundEvent.put(entry.getValue().getId(), entry.getValue());
            }
        }
    }
}
