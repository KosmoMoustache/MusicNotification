package net.kosmo.music.gui.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DebugTab extends AbstractJukeboxTab {
	DebugTab(Minecraft minecraft, JukeboxScreenV3 screen, int width, int height) {
		super(minecraft, screen, width, height);
	}

	@Override
	protected List<? extends AbstractJukeboxEntryContainerWidget> buildEntries() {
		return buildSoundEntries(this.minecraft.getSoundManager().getAvailableSounds());
	}

	@Override
	Component emptyListMessage() {
		return Component.translatable("gui.musicnotification.jukebox.debug.empty");
	}

	@Override
	public @NonNull Component getTabTitle() {
		return Component.translatable("gui.musicnotification.jukebox.debug.title");
	}

	@Override
	public @NonNull Component getTabExtraNarration() {
		return Component.empty();
	}
}
