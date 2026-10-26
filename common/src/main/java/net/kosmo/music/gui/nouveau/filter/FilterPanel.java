//? if >=26.2 {
package net.kosmo.music.gui.nouveau.filter;

import net.kosmo.music.gui.nouveau.JukeboxScreenNouveau;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.kosmo.music.resource.TrackFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.util.*;
import java.util.function.Consumer;

public class FilterPanel {
	private static final int NAMESPACE_HEIGHT = 20;
	private static final int SPACING = 4;
	private static final int BACKGROUND_COLOR = ARGB.color(200, 12, 12, 12);

	private final Minecraft minecraft;
	private final JukeboxScreenNouveau screen;
	private final NamespaceFilterWidget namespaceFilter;
	private final LinearLayout trackScrollableContent;
	private final ScrollableLayout scrollableLayout;
	private final LinearLayout layout;
	private final int width;
	private final List<AlbumFilterWidgetEntry> albumEntries = new ArrayList<>();
	private boolean visible = true;

	public FilterPanel(Minecraft minecraft, JukeboxScreenNouveau screen, int width, int height) {
		this.minecraft = minecraft;
		this.screen = screen;
		this.width = width;

		this.layout = LinearLayout.vertical().spacing(SPACING);
		this.layout.defaultCellSetting().alignHorizontallyCenter();

		this.namespaceFilter = new NamespaceFilterWidget(minecraft, width, NAMESPACE_HEIGHT, this::onNamespaceSelected);

		this.trackScrollableContent = LinearLayout.vertical();
		this.scrollableLayout = new ScrollableLayout(minecraft, this.trackScrollableContent, height - NAMESPACE_HEIGHT - SPACING);
		this.scrollableLayout.setScrollbarSpacing(2);

		this.layout.addChild(new StringWidget(Component.translatable("gui.musicnotification.jukebox.filter.title"), minecraft.font), s -> s.paddingTop(3).alignHorizontallyCenter());
		this.layout.addChild(this.namespaceFilter);
		this.layout.addChild(this.scrollableLayout);
		this.layout.addChild(Button.builder(Component.translatable("gui.musicnotification.jukebox.filter.clear_button"), button -> this.screen.clearFilters()).build());

		this.setHeight(height);
		this.refresh();
	}

	private void onNamespaceSelected(String namespace) {
		this.screen.updateFilter(filter -> filter.withNamespace(namespace));
	}

	/**
	 * Plain click: exclusive selection (clicking the only selected album clears it).
	 * Ctrl-click: toggles the album within the current selection.
	 */
	private void onAlbumSelected(AlbumFilterWidgetEntry selected, boolean multiSelection) {
		String album = selected.getAlbum();
		boolean wasSelected = this.screen.getTrackFilter().isAlbumSelected(album);

		this.screen.updateFilter(filter -> {
			if (multiSelection) {
				return wasSelected ? filter.removeAlbum(album) : filter.addAlbum(album);
			}
			if (wasSelected && filter.albums().size() == 1) {
				return filter.clearAlbums();
			}
			return filter.clearAlbums().addAlbum(album);
		});
	}

	private void rebuildAlbums() {
		TrackFilter filter = this.screen.getTrackFilter();
		String namespace = filter.namespace();

		Collection<TrackData> tracks = TrackDataManager.getInstance().tracks.values();
		Map<String, TrackData> albums = new TreeMap<>();
		for (TrackData track : tracks) {
			if (namespace != null && !namespace.equals(track.key().getNamespace())) {
				continue;
			}
			track.album().map(Component::getString).filter(name -> !name.isEmpty()).ifPresent(name -> albums.putIfAbsent(name, track));
		}

		int contentWidth = this.getContentWidth();
		this.albumEntries.clear();
		for (Map.Entry<String, TrackData> album : albums.entrySet()) {
			this.albumEntries.add(new AlbumFilterWidgetEntry(this.minecraft, contentWidth, 36, album.getKey(), album.getValue().getAlbumCover(), this::onAlbumSelected));
		}

		trackScrollableContent.removeChildren();
		this.albumEntries.forEach(this.trackScrollableContent::addChild);
		if (this.albumEntries.isEmpty()) {
			this.trackScrollableContent.addChild(this.createEmptyListFrame(contentWidth));
		}
		this.syncAlbumSelection();
		this.scrollableLayout.arrangeElements();
	}

	private void syncAlbumSelection() {
		TrackFilter filter = this.screen.getTrackFilter();
		for (AlbumFilterWidgetEntry entry : this.albumEntries) {
			entry.setSelected(filter.isAlbumSelected(entry.getAlbum()));
		}
	}

	private FrameLayout createEmptyListFrame(int contentWidth) {
		StringWidget stringWidget = new StringWidget(
			contentWidth, 20, Component.translatable("gui.musicnotification.jukebox.filter.album.empty"), this.minecraft.font);

		FrameLayout frame = new FrameLayout(contentWidth, stringWidget.getHeight());
		frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
		frame.addChild(stringWidget);
		return frame;
	}

	private int getContentWidth() {
		return this.width - JukeboxScreenNouveau.SCROLLBAR_RESERVE;
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
		scrollableLayout.setMinWidth(scrollableHeight);
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
		this.namespaceFilter.refresh(this.screen.getTrackFilter().namespace());
		this.rebuildAlbums();
	}
}
//? }
