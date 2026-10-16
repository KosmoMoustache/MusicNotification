package net.kosmo.music.gui.v3.filter;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.gui.component.AlbumCoverWidget;
import net.kosmo.music.resource.AlbumCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AlbumFilterWidgetEntry extends AbstractContainerWidget {
	private final List<AbstractWidget> children = new ArrayList<>();

	private final AlbumCoverWidget albumCoverWidget;
	private final StringWidget albumNameWidget;
	private final BiConsumer<AlbumFilterWidgetEntry, Boolean> onSelect;
	private boolean selected;

	public AlbumFilterWidgetEntry(Minecraft minecraft, int width, int height, String album, AlbumCover cover, BiConsumer<AlbumFilterWidgetEntry, Boolean> onSelect) {
		this(minecraft, 0, 0, width, height, album, cover, onSelect);
	}

	public AlbumFilterWidgetEntry(Minecraft minecraft, int x, int y, int width, int height, String album, AlbumCover cover, BiConsumer<AlbumFilterWidgetEntry, Boolean> onSelect) {
		super(x, y, width, height, Component.empty());
		this.onSelect = onSelect;
		this.selected = false;

		this.albumCoverWidget = new AlbumCoverWidget(20, 20, cover, false);
		this.albumNameWidget = new StringWidget(Component.literal(album), minecraft.font);

		this.children.add(this.albumCoverWidget);
		this.children.add(this.albumNameWidget);
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	protected int contentHeight() {
		return this.height;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		// Divider
		graphics.fill(this.getX(), this.getY() + this.getHeight() - 2, this.getRight(), this.getY() + this.getHeight(), CommonColors.DARK_GRAY);

		if (this.isSelected() || this.isHoveredOrFocused()) {
			graphics.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight() - 1, CommonColors.WHITE);
		}
		if (this.isHoveredOrFocused()) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}

		this.albumCoverWidget.setPosition(this.getX() + 4, this.getY() + (this.getHeight() - this.albumCoverWidget.getHeight()) / 2 - 1);
		this.albumCoverWidget.extractRenderState(graphics, mouseX, mouseY, a);

		this.albumNameWidget.setPosition(
			this.albumCoverWidget.getX() + this.albumCoverWidget.getWidth() + 4,
			this.getY() + (this.getHeight() - this.albumNameWidget.getHeight()) / 2
		);
		this.albumNameWidget.extractRenderState(graphics, mouseX, mouseY, a);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return this.children;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		this.primaryAction(event.hasControlDown());
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.isConfirmation()) {
			this.primaryAction(event.hasControlDown());
		}
		return super.keyPressed(event);
	}

	public boolean primaryAction(boolean multiSelection) {
		MusicNotificationClient.LOGGER.info("AlbumFilterWidgetEntry::primaryAction");
		this.onSelect.accept(this, multiSelection);
		this.setSelected(true);
		return true;
	}
}
