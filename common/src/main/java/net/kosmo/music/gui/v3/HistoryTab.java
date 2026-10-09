package net.kosmo.music.gui.v3;

import net.kosmo.music.TrackHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class HistoryTab extends AbstractJukeboxTab {
	HistoryTab(Minecraft minecraft, JukeboxScreenV3 screen, int width, int height) {
		super(minecraft, screen, width, height);
	}

	@Override
	protected List<? extends AbstractJukeboxEntryContainerWidget> buildEntries() {
		return buildTrackEntries(TrackHistory.getInstance().getHistory());
	}

	@Override
	Component emptyListMessage() {
		return Component.translatable("gui.mn.jukebox.history.empty");
	}

	@Override
	public @NonNull Component getTabTitle() {
		return Component.translatable("gui.mn.jukebox.history.title");
	}

	@Override
	public @NonNull Component getTabExtraNarration() {
		return Component.empty();
	}
}
