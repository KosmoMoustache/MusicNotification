package net.kosmo.music.gui.v3;

import net.kosmo.music.gui.component.SearchBox;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.kosmo.music.resource.TrackFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class AbstractJukeboxTab implements Tab {
	private static final int SEARCH_BOX_PADDING = 3;

	protected final LinearLayout trackScrollableContent;
	protected final ScrollableLayout scrollableLayout;
	protected final LinearLayout layout;
	protected final SearchBox searchBox;
	protected final Minecraft minecraft;
	private final JukeboxScreenV3 parent;
	private @Nullable String filter;
	private int width;
	private int height;


	AbstractJukeboxTab(Minecraft minecraft, JukeboxScreenV3 parent, int width, int height) {
		this.parent = parent;
		this.minecraft = minecraft;
		this.width = width;
		this.height = height;

		this.searchBox = new SearchBox(minecraft, width, 20, this::editBoxResponder);

		this.layout = LinearLayout.vertical();
		this.layout.defaultCellSetting().alignHorizontallyCenter();

		this.trackScrollableContent = LinearLayout.vertical();
		this.scrollableLayout = new ScrollableLayout(minecraft, this.trackScrollableContent, height, ScrollableLayout.ReserveStrategy.RIGHT);
		this.scrollableLayout.setScrollbarSpacing(2);

		this.layout.addChild(searchBox.layout(), settings -> settings.paddingTop(SEARCH_BOX_PADDING).paddingBottom(SEARCH_BOX_PADDING));
		this.layout.addChild(this.scrollableLayout);
		this.rearrangeElements();
	}

	@Override
	public void visitChildren(Consumer<AbstractWidget> childrenConsumer) {
		this.layout.visitWidgets(childrenConsumer);
	}

	@Override
	public void doLayout(ScreenRectangle screenRectangle) {
		this.layout.arrangeElements();
		FrameLayout.alignInRectangle(this.layout, screenRectangle, 0.5F, 0.16666667F);
	}

	@Override
	public Layout getLayout() {
		return this.layout;
	}

	private void editBoxResponder(String value) {
		this.filter = value;
		this.parent.refreshLists();
	}

	private FrameLayout createEmptyListFrame() {
		int maxWidth = this.getListContentWidth();
		StringWidget stringWidget = new StringWidget(
			this.minecraft.font.width(this.emptyListMessage()), this.scrollableLayout.getHeight(), this.emptyListMessage(), this.minecraft.font);

		FrameLayout frame = new FrameLayout(maxWidth, stringWidget.getHeight());
		frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
		frame.addChild(stringWidget);
		return frame;
	}

	protected int getListContentWidth() {
		return this.width - JukeboxScreenV3.SCROLLBAR_RESERVE;
	}

	protected abstract List<? extends AbstractJukeboxEntryContainerWidget> buildEntries();

	protected final void updateEntries(List<? extends AbstractJukeboxEntryContainerWidget> entries) {
		this.trackScrollableContent.removeChildren();
		entries.forEach(this.trackScrollableContent::addChild);

		if (entries.isEmpty())
			this.trackScrollableContent.addChild(this.createEmptyListFrame());
	}

	protected List<TrackEntry> buildTrackEntries(Collection<TrackData> tracks) {
		List<TrackEntry> entries = new ArrayList<>(tracks.size());
		for (TrackData track : tracks) {
			if (this.filter != null && !TrackFilter.fromQuery(this.filter).test(track)) continue;
			entries.add(new TrackEntry(this.minecraft, this.parent, this.scrollableLayout, track));
		}
		return entries;
	}

	protected List<SoundEntry> buildSoundEntries(Collection<Identifier> sounds) {
		List<SoundEntry> entries = new ArrayList<>(sounds.size());
		for (Identifier id : sounds) {
			if (this.filter != null && !id.toString().toLowerCase(Locale.ROOT).contains(filter.toLowerCase())) continue; // filter fix
			entries.add(new SoundEntry(this.minecraft, this.parent, this.scrollableLayout,
				TrackDataManager.getEmpty(id, id.toString(), id.getNamespace())));
		}
		return entries;
	}

	public final void refresh() {
		this.updateEntries(this.buildEntries());
	}

	void rearrangeElements() {
		this.scrollableLayout.setMinHeight(this.height - this.searchBox.contentHeight() - SEARCH_BOX_PADDING *2);
		this.scrollableLayout.setMaxHeight(this.height - this.searchBox.contentHeight() - SEARCH_BOX_PADDING *2);
	}

	void setHeight(int height) {
		this.height = height;
		this.rearrangeElements();
	}

	abstract Component emptyListMessage();
}
