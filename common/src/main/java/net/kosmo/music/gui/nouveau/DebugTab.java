//? if >=26.2 {
package net.kosmo.music.gui.nouveau;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class DebugTab extends net.kosmo.music.gui.nouveau.AbstractJukeboxTab {
	DebugTab(Minecraft minecraft, JukeboxScreenNouveau screen, int width, int height) {
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
//? }
