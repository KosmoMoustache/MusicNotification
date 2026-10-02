package net.kosmo.music.gui.v2;

import net.kosmo.music.resource.AlbumCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlbumEntry extends ContainerObjectSelectionList.Entry<AlbumEntry> {
	private static final int SELECTED_COLOR = ARGB.color(255, 80, 80, 80);
	private static final int HOVER_COLOR = ARGB.color(90, 255, 255, 255);

	private final JukeboxScreenV2 screen;
	@Nullable
	private final String album;
	@Nullable
	private final AlbumCover cover;

	public AlbumEntry(JukeboxScreenV2 screen, @Nullable String album, @Nullable AlbumCover cover) {
		this.screen = screen;
		this.album = album;
		this.cover = cover;
	}

	//? if <=1.21.8 {
	/*@Override
	public void render(GuiGraphicsExtractor graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
		renderContent(graphics, mouseX, mouseY, hovering, partialTick, left, top, width, height);
	}
	*///? } else {
	@Override
	//~ if >26 renderContent -> extractContent
	public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
		renderContent(graphics, mouseX, mouseY, isHovering, partialTick, this.getContentX(), this.getContentY(), this.getContentWidth(), this.getContentHeight());
	}
	//? }

	private void renderContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick, int x, int y, int width, int height) {
		int right = x + width;
		int bottom = y + height;

		if (this.album != null && this.album.equals(this.screen.getSelectedAlbum())) {
			graphics.fill(x, y, right, bottom, SELECTED_COLOR);
		} else if (hovered) {
			graphics.fill(x, y, right, bottom, HOVER_COLOR);
		}

		if (this.album == null) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(Minecraft.getInstance().font, Component.translatable("gui.musicnotification.jukebox.album_all"), x + width / 2, y + height / 2 - 4, CommonColors.WHITE);
		} else if (this.cover != null) {
			int coverX = x + (width - this.cover.getWidth()) / 2;
			int coverY = y + (height - this.cover.getHeight()) / 2;
			this.cover.drawCover(graphics, coverX, coverY);
		}
	}

	// Returning false so the click is not consumed; this lets the list handle
	// scrollbar dragging when the click lands on the scrollbar area.
	//? if >=1.21.9 {
	@Override
	public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
		this.screen.selectAlbum(this.album);
		return false;
	}
	//? } else {
	/*@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.screen.selectAlbum(this.album);
		return false;
	}
	*///? }

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of();
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return List.of();
	}
}
