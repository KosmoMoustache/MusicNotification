package net.kosmo.music.gui.v3;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MuteTab extends AbstractJukeboxTab {
	MuteTab(Minecraft minecraft, JukeboxScreenV3 screen, int width, int height) {
		super(minecraft, screen, width, height);
	}

	@Override
	protected List<? extends AbstractJukeboxEntryContainerWidget> buildEntries() {
		return buildSoundEntries(this.minecraft.getSoundManager().getAvailableSounds());
	}

	@Override
	Component emptyListMessage() {
		return Component.translatable("gui.mn.jukebox.mute.empty");
	}

	@Override
	public Component getTabTitle() {
		return Component.translatable("gui.mn.jukebox.mute.title");
	}

	@Override
	public Component getTabExtraNarration() {
		return Component.empty();
	}
}
