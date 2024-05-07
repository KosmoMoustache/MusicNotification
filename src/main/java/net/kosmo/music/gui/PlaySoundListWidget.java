package net.kosmo.music.gui;

import com.google.common.collect.Lists;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PlaySoundListWidget extends ElementListWidget<ListEntry> {
    public final JukeboxScreen parent;
    private final List<ListEntry> entries = Lists.newArrayList();
    @Nullable
    private String currentSearch;

    public PlaySoundListWidget(JukeboxScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.setRenderBackground(false);
    }

    private void refresh(Collection<ListEntry> values, double scrollAmount) {
        this.entries.clear();
        this.entries.addAll(values);
        this.filterSounds();
        this.replaceEntries(this.entries);
        this.setScrollAmount(scrollAmount);
    }

    public <T> void update(Collection<T> entries, double scrollAmount) {
        HashMap<Identifier, ListEntry> map = new LinkedHashMap<>();

        for (T entry : entries) {
            if (entry instanceof MusicManager.Music music) {
                map.put(music.identifier, new MusicListEntry(this.client, this, music));
            } else if (entry instanceof MusicManager.Sound sound) {
                map.put(sound.identifier, new SoundListEntry(this.client, this, sound));
            }
        }

        this.refresh(map.values(), scrollAmount);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public void setCurrentSearch(@Nullable String currentSearch) {
        this.currentSearch = currentSearch;
    }

    private void filterSounds() {
        if (this.currentSearch != null) {
            String[] searchTerms = this.currentSearch.toLowerCase(Locale.ROOT).split(" ");
            AtomicReference<String> matchPredicate = new AtomicReference<>(null);
            this.entries.removeIf(entry -> {
                if (entry instanceof MusicListEntry musicEntry) {
                    matchPredicate.set(musicEntry.entry.getTitle() + " " + musicEntry.entry.getAuthor() + " " + musicEntry.entry.getAlbumName());
                } else if (entry instanceof SoundListEntry soundEntry) {
                    matchPredicate.set(soundEntry.entry.identifier.toString());
                }

                if (matchPredicate.get() == null) return false;
                return !Arrays.stream(searchTerms).allMatch(term -> matchPredicate.get().toLowerCase(Locale.ROOT).contains(term));
            });
            this.replaceEntries(this.entries);
        }
    }
}
