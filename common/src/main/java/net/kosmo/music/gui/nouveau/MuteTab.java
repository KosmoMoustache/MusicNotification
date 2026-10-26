//? if >=26.2 {
package net.kosmo.music.gui.nouveau;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MuteTab extends net.kosmo.music.gui.nouveau.AbstractJukeboxTab {
	MuteTab(Minecraft minecraft, JukeboxScreenNouveau screen, int width, int height) {
		super(minecraft, screen, width, height);
	}

	@Override
	protected List<? extends net.kosmo.music.gui.nouveau.AbstractJukeboxEntryContainerWidget> buildEntries() {
		return buildSoundEntries(this.minecraft.getSoundManager().getAvailableSounds());
	}

	@Override
	Component emptyListMessage() {
		return Component.translatable("gui.musicnotification.jukebox.mute.empty");
	}

	@Override
	public Component getTabTitle() {
		return Component.translatable("gui.musicnotification.jukebox.mute.title");
	}

	@Override
	public Component getTabExtraNarration() {
		return Component.empty();
	}
}
//? }
