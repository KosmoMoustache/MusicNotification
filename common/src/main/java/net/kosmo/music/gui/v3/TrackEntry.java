package net.kosmo.music.gui.v3;

import net.kosmo.music.Helper;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.network.chat.Component;

public class TrackEntry extends AbstractJukeboxEntryContainerWidget {
	public TrackEntry(Minecraft minecraft, JukeboxScreenV3 screen, ScrollableLayout scrollableLayout, TrackData trackData) {
		this(minecraft, screen, scrollableLayout, trackData, false);
	}
	public TrackEntry(Minecraft minecraft, JukeboxScreenV3 screen, ScrollableLayout scrollableLayout, TrackData trackData, boolean animated) {
		super(minecraft, screen, scrollableLayout, 0, 0, screen.getContentWidth(), 36, trackData, animated);
	}

	@Override
	boolean primaryAction() {
		Helper.playTrackData(this.minecraft, this.trackData);
		return true;
	}

	@Override
	protected Component getEntryNarration() {
		return Component.translatable("gui.mn.jukebox.entry.track", this.trackData.title().getString());
	}
}
