package net.kosmo.music.gui.v3.filter;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.gui.v3.JukeboxScreenV3;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.*;
import java.util.function.Consumer;

/**
 * Not a widget: exposes a vertical layout (namespace pager + scrollable album list)
 * whose widgets are registered directly with the screen, like a {@code Tab}.
 */
public class FilterPanel {
	private static final int NAMESPACE_HEIGHT = 20;
	private static final int SPACING = 4;
	private static final int BACKGROUND_COLOR = ARGB.color(200, 12, 12, 12);

	private final Minecraft minecraft;
	private final JukeboxScreenV3 screen;
	private final NamespaceFilterWidget namespaceFilter;
	private final LinearLayout trackScrollableContent;
	private final ScrollableLayout scrollableLayout;
	private final LinearLayout layout;
	private final int width;
	private final List<AlbumFilterWidgetEntry> albumEntries = new ArrayList<>();
	private boolean visible = true;

	public FilterPanel(Minecraft minecraft, JukeboxScreenV3 screen, int width, int height) {
		this.minecraft = minecraft;
		this.screen = screen;
		this.width = width;

		this.layout = LinearLayout.vertical().spacing(SPACING);
		this.layout.defaultCellSetting().alignHorizontallyCenter();

		this.namespaceFilter = new NamespaceFilterWidget(minecraft, width, NAMESPACE_HEIGHT, this::onNamespaceSelected);
		this.layout.addChild(this.namespaceFilter);

		this.trackScrollableContent = LinearLayout.vertical();
		this.scrollableLayout = new ScrollableLayout(minecraft, this.trackScrollableContent, height - NAMESPACE_HEIGHT - SPACING, ScrollableLayout.ReserveStrategy.RIGHT);
		this.scrollableLayout.setScrollbarSpacing(2);

		this.layout.addChild(new StringWidget(Component.translatable("gui.mn.jukebox.filter.title"), minecraft.font), s -> s.paddingTop(3).alignHorizontallyCenter());
		this.layout.addChild(this.scrollableLayout);
		this.layout.addChild(Button.builder(Component.literal("Clear Filter"), _ -> {
			MusicNotificationClient.LOGGER.info("Clear Filter");
		}).build());

		this.setHeight(height);
		this.refresh();
	}

	private void onNamespaceSelected(String namespace) {
//		this.namespace = namespace == null ? "" : namespace;
		this.rebuildAlbums();
	}

	private void onAlbumSelected(AlbumFilterWidgetEntry selected, boolean hasMultiSelection) {
		if (hasMultiSelection) return;
		// TODO: Add to track filter

		for (AlbumFilterWidgetEntry entry : this.albumEntries) {
			if (entry != selected) {
				entry.setSelected(false);
			}
		}
	}

	private void rebuildAlbums() {
		Collection<TrackData> tracks = TrackDataManager.getInstance().tracks.values();
		Map<String, TrackData> albums = new TreeMap<>();
		for (TrackData track : tracks) {
//			if (!this.namespace.isEmpty() && !track.key().getNamespace().equals(this.namespace)) {
//				continue;
//			}
			track.album().map(Component::getString).filter(name -> !name.isEmpty()).ifPresent(name -> albums.putIfAbsent(name, track));
		}

		int contentWidth = this.getContentWidth();
		this.albumEntries.clear();
		for (Map.Entry<String, TrackData> album : albums.entrySet()) {
			this.albumEntries.add(new AlbumFilterWidgetEntry(this.minecraft, contentWidth, 36, album.getKey(), album.getValue().getAlbumCover(), this::onAlbumSelected));
		}

		this.trackScrollableContent.removeChildren();
		this.albumEntries.forEach(this.trackScrollableContent::addChild);
		if (this.albumEntries.isEmpty()) {
			this.trackScrollableContent.addChild(this.createEmptyListFrame(contentWidth));
		}
		this.scrollableLayout.arrangeElements();
	}

	private FrameLayout createEmptyListFrame(int contentWidth) {
		StringWidget stringWidget = new StringWidget(
			contentWidth, 20, Component.translatable("gui.mn.jukebox.filter.album.empty"), this.minecraft.font);

		FrameLayout frame = new FrameLayout(contentWidth, stringWidget.getHeight());
		frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
		frame.addChild(stringWidget);
		return frame;
	}

	private int getContentWidth() {
		return this.width - JukeboxScreenV3.SCROLLBAR_RESERVE;
	}

	public void visitChildren(Consumer<AbstractWidget> visitor) {
		this.layout.visitWidgets(visitor);
	}

	public void arrangeElements() {
		this.layout.arrangeElements();
	}

	public void setPosition(int x, int y) {
		this.layout.setPosition(x, y);
	}

	public void setHeight(int height) {
		int scrollableHeight = height - NAMESPACE_HEIGHT - SPACING;
		this.scrollableLayout.setMinHeight(scrollableHeight);
		this.scrollableLayout.setMaxHeight(scrollableHeight);
	}

	public void extractBackground(GuiGraphicsExtractor graphics) {
		graphics.fill(this.layout.getX(), this.layout.getY(), this.layout.getX() + this.layout.getWidth(), this.layout.getY() + this.layout.getHeight(), BACKGROUND_COLOR);
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		this.namespaceFilter.visible = visible;
		this.layout.visitWidgets(widget -> widget.visible = visible);
	}

	public void refresh() {
		this.namespaceFilter.refresh();
		this.rebuildAlbums();
	}
}
