package net.kosmo.music.gui.v3;

import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;


public class HomeTab extends AbstractJukeboxTab {
	HomeTab(Minecraft minecraft, JukeboxScreenV3 screen, int width, int height) {
		super(minecraft, screen, width, height);
	}

	@Override
	protected List<? extends AbstractJukeboxEntryContainerWidget> buildEntries() {
		return buildTrackEntries(TrackDataManager.getInstance().tracks.values());
	}

	@Override
	public @NonNull Component getTabTitle() {
		return Component.translatable("gui.mn.jukebox.home.title");
	}

	@Override
	public @NonNull Component getTabExtraNarration() {
		return Component.empty();
	}

	@Override
	Component emptyListMessage() {
		return Component.translatable("gui.mn.jukebox.home.empty");
	}

}
