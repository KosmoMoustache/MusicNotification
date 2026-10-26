//? if >=26.2 {
package net.kosmo.music.gui.nouveau;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;

public class SoundEntry extends net.kosmo.music.gui.nouveau.AbstractJukeboxEntryContainerWidget {
	public SoundEntry(Minecraft minecraft, JukeboxScreenNouveau screen, ScrollableLayout scrollableLayout, TrackData trackData) {
		this(minecraft, screen, scrollableLayout, trackData, false);
	}

	public SoundEntry(Minecraft minecraft, JukeboxScreenNouveau screen, ScrollableLayout scrollableLayout, TrackData trackData, boolean animated) {
		super(minecraft, screen, scrollableLayout, 0, 0, screen.getContentWidth(), 36, trackData, animated);
	}

	@Override
	boolean primaryAction() {
		SoundEvent soundEvent = Helper.getSoundEvent(this.minecraft, trackData.key());
		if (soundEvent != null) {
			SimpleSoundInstance soundInstance = Helper.forMusic(soundEvent);
			this.minecraft.getSoundManager().stop(null, SoundSource.MASTER);
			this.minecraft.getSoundManager().play(soundInstance);
		} else {
			MusicNotificationClient.LOGGER.warn("Unable to play unknown sound with id: {}", trackData.key());
		}
		return true;
	}

	@Override
	protected void initWidgets(int width, boolean animated) {
		this.line1Widget = new StringWidget(this.trackData.title(), this.minecraft.font);
		this.line2Widget = new StringWidget(this.trackData.author().copy().setStyle(Style.EMPTY.withColor(CommonColors.LIGHT_GRAY)), this.minecraft.font);
		this.addChild(this.line1Widget);
		this.addChild(this.line2Widget);

		this.layout.addChild(line1Widget, s -> s.paddingBottom(3));
		this.layout.addChild(line2Widget);
		this.layout.arrangeElements();
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

		this.layout.setPosition(this.getX() + 4, this.getY() + (this.getHeight() - this.layout.getHeight()) / 2);
		this.layout.visitWidgets(child -> child.extractRenderState(graphics, mouseX, mouseY, a));
	}

	@Override
	protected Component getEntryNarration() {
		return Component.translatable("gui.musicnotification.jukebox.entry.track", this.trackData.title().getString());
	}
}
//? }
