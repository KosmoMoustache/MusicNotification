package net.kosmo.nowplaying.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SoundListWidget extends ElementListWidget<SoundListEntry> {
    private final PlaySoundScreen parent;
    private final List<SoundListEntry> sounds = Lists.newArrayList();
    @Nullable
    private String currentSearch;


    public SoundListWidget(PlaySoundScreen parent, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);

        this.parent = parent;
        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
    }

    public void update(Collection<Identifier> identifiers, double scrollAmount) {
        HashMap<Identifier, SoundListEntry> map = new HashMap<Identifier, SoundListEntry>();
        this.setIdentifier(identifiers, map);
        this.refresh(map.values(), scrollAmount);
    }

    private void refresh(Collection<SoundListEntry> values, double scrollAmount) {

        this.sounds.clear();
        this.sounds.addAll(values);
        this.filterPlayers();
        this.replaceEntries(this.sounds);
        this.setScrollAmount(scrollAmount);
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    private void filterPlayers() {
        if (this.currentSearch != null) {
            this.sounds.removeIf(sound -> !sound.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch));
            this.replaceEntries(this.sounds);
        }
    }

    private void setIdentifier(Collection<Identifier> identifiers, HashMap<Identifier, SoundListEntry> map) {
        for (Identifier identifier : identifiers) {
            map.put(identifier, new SoundListEntry(this.client, this.parent, identifier));
        }
    }

    public boolean isEmpty() {
        return this.sounds.isEmpty();
    }

}
