package net.kosmo.music.gui;

import net.kosmo.music.Helper;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.resource.SoundData;
import net.kosmo.music.util.TextRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SoundEntry extends ListEntry {
	public static final int GRAY_COLOR = ARGB.color(255, 74, 74, 74);
	private static final WidgetSprites PLAY_BUTTON_TEXTURE = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_disabled"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_focused"));
	public final SoundData entry;
	private final List<AbstractWidget> children;
	private final ImageButton playButton;

	public SoundEntry(Minecraft client, JukeboxEntryList parent, SoundData entry) {
		this.client = client;
		this.parent = parent;
		this.entry = entry;

		this.playButton = new ImageButton(0, 0, 20, 20, PLAY_BUTTON_TEXTURE, button -> {
			play(entry);
		}, Component.translatable("gui.musicnotification.jukebox.play_sound")) {
			@Override
			public void playDownSound(SoundManager soundManager) {
			}
		};

		this.children = new ArrayList<>();
		this.children.add(playButton);
	}

	private void play(SoundData entry) {
		SoundEvent soundEvent = Helper.getSoundEvent(this.client, entry.id());
		if (soundEvent != null) {
			SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent, 10);
			this.client.getSoundManager().stop(null, SoundSource.MASTER);
			this.client.getSoundManager().play(soundInstance);
		} else {
			MusicNotificationClient.LOGGER.warn("Unable to play unknown sound with id: {}", entry.id());
		}
	}

	//? if >=1.21.9 {
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

	//? } else {
	/*@Override
	 *///? }
	public void render(GuiGraphics guiGraphics, int do_not_use_index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
		guiGraphics.fill(left, top, left + width, top + height, GRAY_COLOR);

		this.playButton.setX(left + (width - this.playButton.getWidth() - 4) - 4);
		this.playButton.setY(top + (height - this.playButton.getHeight()) / 2);
		this.playButton.active = !Helper.isVolumeZero();
		this.playButton.render(guiGraphics, mouseX, mouseY, partialTick);

		TextRender.drawScrollableText(guiGraphics, this.client.font, Component.literal(entry.id().toString()), left + 4, left + 4, top + (height - this.client.font.lineHeight) / 2, left + width - 24 - 8, top + (height - this.client.font.lineHeight) / 2 + this.client.font.lineHeight, CommonColors.WHITE, true);
	}

	@Override
	public @NotNull List<? extends NarratableEntry> narratables() {
		return this.children;
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return this.children;
	}
}
