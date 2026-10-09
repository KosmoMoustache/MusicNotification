package net.kosmo.music.gui.v3;

import net.kosmo.music.Helper;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class TrackEntry extends AbstractJukeboxEntryContainerWidget{
	public TrackEntry(Minecraft minecraft, JukeboxScreenV3 screen, TrackData trackData) {
		this(minecraft, screen, trackData, false);
	}
	public TrackEntry(Minecraft minecraft, JukeboxScreenV3 screen, TrackData trackData, boolean animated) {
		super(minecraft, screen, 0, 0, screen.getContentWidth(), 36, trackData, animated);
	}

	@Override
	boolean play() {
		Helper.playTrackData(this.minecraft, this.trackData);
		return true;
	}

	@Override
	protected Component getEntryNarration() {
		return Component.translatable("gui.mn.jukebox.entry.track", this.trackData.title().getString());
	}
}
