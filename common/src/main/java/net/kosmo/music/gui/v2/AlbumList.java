package net.kosmo.music.gui.v2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

import java.util.List;

public class AlbumList extends ContainerObjectSelectionList<AlbumEntry> {
	public AlbumList(JukeboxScreenV2 parent, Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
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
		return this.getWidth() - 12;
	}

	public void update(List<AlbumEntry> entries) {
		//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
		double scroll = this.scrollAmount();
		this.clearEntries();
		this.replaceEntries(entries);
		this.setScrollAmount(scroll);
	}
}
