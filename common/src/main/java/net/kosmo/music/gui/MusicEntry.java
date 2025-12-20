package net.kosmo.music.gui;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.mixin.MusicManagerAccessor;
import net.kosmo.music.resource.AlbumCover;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
	private final List<AbstractWidget> children;
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

		this.children = new ArrayList<>();
		this.children.add(this.playButton);
		this.children.add(this.stopButton);
	}

	//? if >=1.21.10 {
	@Override
	public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		render(
			guiGraphics,
			0,
			this.getContentY(),
			this.getContentX(),
			this.getContentWidth(),
			this.getContentHeight(),
			mouseX,
			mouseY,
			isHovering,
			partialTick
		);
	}

	//?} else {
	/*@Override
	 *///?}
	public void render(GuiGraphics guiGraphics, int do_not_use_index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
		int y1 = top + 4;
		int y2 = top + (height / 2) + 3;
		int xMargeCover = left + 4 + 24 + 4;

		guiGraphics.fill(left, top, left + width, top + height, GRAY_COLOR);

		MutableComponent text = entry.title().copy().append(" - ").withColor(CommonColors.WHITE).append(entry.author().copy().withColor(CommonColors.LIGHT_GRAY));
		TextRender.drawScrollableText(guiGraphics, this.client.font, text, xMargeCover, xMargeCover, y1, this.playButton.getX() - 4, y1 + this.client.font.lineHeight, CommonColors.WHITE, true);
		if (entry.album().isPresent()) {
			guiGraphics.drawString(this.client.font, entry.album().get(), xMargeCover, y2, CommonColors.LIGHT_GRAY, false);
		}

		AlbumCover albumCover = this.entry.getAlbumCover();
		albumCover.drawCover(guiGraphics, left + 4, top + (height - albumCover.getHeight()) / 2);

		boolean shouldRenderButton = this.parent.parent.currentTab != JukeboxScreen.Tab.HISTORY;
		boolean isPlaying = isPlaying();

		this.playButton.setX(left + (width - this.playButton.getWidth()) - 8);
		this.playButton.setY(top + (height - this.playButton.getHeight()) / 2);
		this.playButton.active = !isPlaying && shouldRenderButton && !Helper.isVolumeZero();
		this.playButton.visible = !isPlaying && shouldRenderButton;
		this.playButton.render(guiGraphics, mouseX, mouseY, partialTick);

		this.stopButton.setX(left + (width - this.stopButton.getWidth()) - 8);
		this.stopButton.setY(top + (height - this.stopButton.getHeight()) / 2);
		this.stopButton.active = isPlaying && shouldRenderButton;
		this.stopButton.visible = isPlaying && shouldRenderButton;
		this.stopButton.render(guiGraphics, mouseX, mouseY, partialTick);
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
