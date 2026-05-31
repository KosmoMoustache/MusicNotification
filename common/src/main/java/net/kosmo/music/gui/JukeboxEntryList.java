package net.kosmo.music.gui;

import com.google.common.collect.Lists;
import net.kosmo.music.resource.SoundData;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class JukeboxEntryList extends ContainerObjectSelectionList<ListEntry> {
	protected final JukeboxScreen parent;
	private final List<ListEntry> entries = Lists.newArrayList();
	@Nullable
	private String filter;

	public JukeboxEntryList(JukeboxScreen jukeboxScreen, Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight/*? <=1.20.1 {*//*, 0*//*?}*/);
		this.parent = jukeboxScreen;
	}

	// Prevent Background
	@Override
	//~ if >26 renderListBackground -> extractListBackground
	protected void extractListBackground(GuiGraphicsExtractor graphics) {
	}
	// Prevent Header & Footer separator
	@Override
	//~ if >26 renderListSeparators -> extractListSeparators
	protected void extractListSeparators(GuiGraphicsExtractor graphics) {
	}

	public int getRowWidth() {
		return 220;
//		return 320;
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
			if (entry instanceof TrackData td) {
				map.put(td.getResolvedId(), new MusicEntry(this.minecraft, this, td));
			}
			if (entry instanceof SoundData sd) {
				map.put(sd.id(), new SoundEntry(this.minecraft, this, sd));
			}
		}

		this.refresh(map.values(), scrollAmount);
	}

	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	public void setFilter(@Nullable String filter) {
		this.filter = filter;
	}

	private void filterSounds() {
		if (this.filter != null) {
			String[] searchTerms = this.filter.toLowerCase(Locale.ROOT).split(" ");
			AtomicReference<String> matchPredicate = new AtomicReference<>(null);
			this.entries.removeIf(entry -> {
				if (entry instanceof MusicEntry musicEntry) {
					matchPredicate.set(String.format("%s %s %s %s",
						musicEntry.entry.key(),
						musicEntry.entry.title(),
						musicEntry.entry.author(),
						(musicEntry.entry.album().isPresent() ? musicEntry.entry.album().get() : "")
					));
				}
				if (entry instanceof SoundEntry soundEntry) {
					matchPredicate.set(soundEntry.entry.id().toString());
				}

				if (matchPredicate.get() == null) return false;
				return !Arrays.stream(searchTerms).allMatch(term -> matchPredicate.get().toLowerCase(Locale.ROOT).contains(term));
			});
			this.replaceEntries(this.entries);
		}
	}

}
