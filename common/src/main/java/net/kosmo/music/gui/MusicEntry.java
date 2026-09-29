package net.kosmo.music.gui;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.mixin.MusicManagerAccessor;
import net.kosmo.music.resource.AlbumCover;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MusicEntry extends ListEntry {
	public static final int GRAY_COLOR = ARGB.color(255, 74, 74, 74);
	private static final WidgetSprites PLAY_BUTTON_TEXTURE = new WidgetSprites(Identifier.fromNamespaceAndPath("musicnotification", "jukebox/play_button"), Identifier.fromNamespaceAndPath("musicnotification", "jukebox/play_button_disabled"), Identifier.fromNamespaceAndPath("musicnotification", "jukebox/play_button_focused"));
	private static final WidgetSprites STOP_BUTTON_TEXTURE = new WidgetSprites(Identifier.fromNamespaceAndPath("musicnotification", "jukebox/stop_button"), Identifier.fromNamespaceAndPath("musicnotification", "jukebox/stop_button_focused"));
	public final TrackData entry;
	private final List<AbstractWidget> children = new ArrayList<>();
	private final ImageButton playButton;
	private final ImageButton stopButton;

	public MusicEntry(Minecraft client, JukeboxEntryList parent, TrackData entry) {
		this.client = client;
		this.parent = parent;
		this.entry = entry;

		this.playButton = new ImageButton(0, 0, 20, 20, PLAY_BUTTON_TEXTURE, button -> {
			Helper.playTrackData(this.client, entry);
		}, Component.translatable("gui.musicnotification.jukebox.play_sound"));

		this.stopButton = new ImageButton(0, 0, 20, 20, STOP_BUTTON_TEXTURE, button -> {
			this.client.getSoundManager().stop(null, SoundSource.MUSIC);
			MusicNotificationClient.currentlyPlaying = null;
		}, Component.translatable("gui.musicnotification.jukebox.stop_sound"));

		this.children.add(this.playButton);
		this.children.add(this.stopButton);
	}

	//? if <=1.21.8 {
	/*@Override
	public void render(GuiGraphicsExtractor graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
		generalRender(graphics, mouseX, mouseY, hovering, partialTick, left, top, height, width, left + width, top + height);
	}
	*///? } else {
	@Override
	//~ if >26 renderContent -> extractContent
	public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		generalRender(graphics, mouseX, mouseY, isHovering, partialTick, this.getContentX(), this.getContentY(), this.getContentHeight(), this.getContentWidth(), this.getContentRight(), this.getContentBottom());
	}
	//? }

	private void generalRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovering, float partialTick, int contentX, int contentY, int contentHeight, int contentWidth, int contentRight, int contentBottom) {
		graphics.fill(contentX, contentY, contentRight, contentBottom, GRAY_COLOR);

		AlbumCover albumCover = this.entry.getAlbumCover();

		int coverX = contentX + 4;
		int coverY = contentY + (contentHeight - albumCover.getWidth()) / 2;

		albumCover.drawCover(graphics, coverX, coverY);

		//	First Line
		int textStartX = coverX + 24 + 4;
		int textStopX = contentX + (contentWidth - this.playButton.getWidth()) - 8;
		int textStartY = contentY;
		int textStopY = contentY + contentHeight / 2;

//		graphics.fill(textStartX, textStartY, textStopX, textStopY, ARGB.multiplyAlpha(CommonColors.RED, 0.5F));

		TextRender.drawScrollableText(
			graphics,
			this.client.font,
			entry.title().copy().append(" - ").withColor(CommonColors.WHITE).append(entry.author().copy().withColor(CommonColors.LIGHT_GRAY)),
			textStartX, textStopX, textStartY, textStopY,
			true
		);

		// Second Line
		textStartY += contentHeight / 2;
		textStopY = contentY + contentHeight;

//		graphics.fill(textStartX, textStartY, textStopX, textStopY, ARGB.multiplyAlpha(CommonColors.GREEN, 0.5F));

		if (entry.album().isPresent()) {
			TextRender.drawScrollableText(
				graphics,
				this.client.font,
				entry.album().get().copy().setStyle(Style.EMPTY.withColor(CommonColors.LIGHT_GRAY)),
				textStartX, textStopX, textStartY, textStopY,
				false
			);
		}

		boolean shouldRenderButton = this.parent.parent.currentTab != JukeboxScreen.Tab.HISTORY;
		boolean isPlaying = isPlaying();

		this.playButton.setX(contentRight - this.playButton.getWidth() - 4);
		this.playButton.setY(contentY + contentHeight / 2 - this.playButton.getHeight() / 2);
		this.playButton.active = !isPlaying && shouldRenderButton && !Helper.isVolumeZero();
		this.playButton.visible = !isPlaying && shouldRenderButton;
		//~ if >26 render -> extractRenderState
		this.playButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

		this.stopButton.setX(contentRight - this.stopButton.getWidth() - 4);
		this.stopButton.setY(contentY + contentHeight / 2 - this.stopButton.getHeight() / 2);
		this.stopButton.active = isPlaying && shouldRenderButton;
		this.stopButton.visible = isPlaying && shouldRenderButton;
		//~ if >26 render -> extractRenderState
		this.stopButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
	}

	private boolean isPlaying() {
		Sound sound1 = MusicNotificationClient.currentlyPlaying != null ? MusicNotificationClient.currentlyPlaying.getSound() : null;
		Sound sound2 = ((MusicManagerAccessor) client.getMusicManager()).getCurrentMusic() != null ? ((MusicManagerAccessor) client.getMusicManager()).getCurrentMusic().getSound() : null;
		if (sound1 != null) {
			return sound1.getLocation().equals(entry.key());
		} else if (sound2 != null) {
			return sound2.getLocation().equals(entry.key());
		}
		return false;

	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return this.children;
	}

	@Override
	public @NotNull List<? extends NarratableEntry> narratables() {
		return this.children;
	}
}
