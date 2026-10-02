package net.kosmo.music.gui.v2;

import com.google.common.collect.Lists;
import net.kosmo.music.resource.SoundData;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class JukeboxEntryList extends ContainerObjectSelectionList<ListEntry> {
	protected final JukeboxScreenV2 parent;
	private final int rowWidth;
	private final List<ListEntry> entries = Lists.newArrayList();
	@Nullable
	private String filter;
	@Nullable
	private String albumFilter;

	public JukeboxEntryList(JukeboxScreenV2 jukeboxScreen, Minecraft minecraft, int width, int height, int y, int itemHeight, int rowWidth) {
		super(minecraft, width, height, y, itemHeight);
		this.parent = jukeboxScreen;
		this.rowWidth = rowWidth;
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

	@Override
	public int getRowWidth() {
		return this.rowWidth;
	}

	private void refresh(Collection<ListEntry> values, double scrollAmount) {
		this.entries.clear();
		this.entries.addAll(values);
		//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
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

	public void setAlbumFilter(@Nullable String albumFilter) {
		this.albumFilter = albumFilter;
	}

	private void filterSounds() {
		boolean hasSearch = this.filter != null && !this.filter.isEmpty();
		if (!hasSearch && this.albumFilter == null) {
			return;
		}

		String[] searchTerms = hasSearch ? this.filter.toLowerCase(Locale.ROOT).split(" ") : new String[0];
		this.entries.removeIf(entry -> {
			if (entry instanceof MusicEntry musicEntry) {
				if (this.albumFilter != null) {
					if (musicEntry.entry.album().isEmpty() || !musicEntry.entry.album().get().getString().equals(this.albumFilter)) {
						return true;
					}
				}
				if (searchTerms.length == 0) {
					return false;
				}
				return !matches(
					String.format("%s %s %s %s",
						musicEntry.entry.key(),
						musicEntry.entry.title(),
						musicEntry.entry.author(),
						(musicEntry.entry.album().isPresent() ? musicEntry.entry.album().get() : "")
					),
					searchTerms
				);
			}
			if (entry instanceof SoundEntry soundEntry) {
				if (searchTerms.length == 0) {
					return false;
				}
				return !matches(soundEntry.entry.id().toString(), searchTerms);
			}
			return false;
		});
		this.replaceEntries(this.entries);
	}

	private static boolean matches(String predicate, String[] searchTerms) {
		String lower = predicate.toLowerCase(Locale.ROOT);
		return Arrays.stream(searchTerms).allMatch(lower::contains);
	}

}
