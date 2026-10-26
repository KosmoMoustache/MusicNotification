//? if >=26.2 {
package net.kosmo.music.gui.nouveau;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractJukeboxTab implements Tab {
	private static final int SEARCH_BOX_PADDING = 3;

	public final LinearLayout trackScrollableContent;
	public final ScrollableLayout scrollableLayout;
	protected final LinearLayout layout;
	protected final SearchBox searchBox;
	protected final Minecraft minecraft;
	private final JukeboxScreenNouveau parent;
	private int width;
	private int height;


	AbstractJukeboxTab(Minecraft minecraft, JukeboxScreenNouveau parent, int width, int height) {
		this.parent = parent;
		this.minecraft = minecraft;
		this.width = width;
		this.height = height;

		this.searchBox = new SearchBox(minecraft, width, 20, this::editBoxResponder);

		this.layout = LinearLayout.vertical();
		this.layout.defaultCellSetting().alignHorizontallyCenter();

		this.trackScrollableContent = LinearLayout.vertical();
		this.scrollableLayout = new ScrollableLayout(minecraft, this.trackScrollableContent, height);
		this.scrollableLayout.setScrollbarSpacing(2);

//		this.layout.addChild(searchBox.layout(), settings -> settings.paddingTop(SEARCH_BOX_PADDING).paddingBottom(SEARCH_BOX_PADDING));
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

	//? if >=26.2 {
	@Override
	//? }
	public Layout getLayout() {
		return this.layout;
	}

	private void editBoxResponder(String value) {
		// Ignore programmatic syncs (setSearchText) that do not change the query.
		if (value.equals(this.parent.getTrackFilter().query())) return;
		this.parent.updateFilter(f -> f.withQuery(value));
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
		return this.width - JukeboxScreenNouveau.SCROLLBAR_RESERVE;
	}

	protected abstract List<? extends AbstractJukeboxEntryContainerWidget> buildEntries();

	protected final void updateEntries(List<? extends AbstractJukeboxEntryContainerWidget> entries) {
		this.trackScrollableContent.removeChildren();
		entries.forEach(this.trackScrollableContent::addChild);

		if (entries.isEmpty())
			this.trackScrollableContent.addChild(this.createEmptyListFrame());
	}

	protected List<net.kosmo.music.gui.nouveau.TrackEntry> buildTrackEntries(Collection<TrackData> tracks) {
		List<net.kosmo.music.gui.nouveau.TrackEntry> entries = new ArrayList<>(tracks.size());
	protected List<TrackEntry> buildTrackEntries(Collection<TrackData> tracks) {
		List<TrackEntry> entries = new ArrayList<>(tracks.size());
		TrackFilter trackFilter = this.parent.getTrackFilter();
		for (TrackData track : tracks) {
			if (!trackFilter.test(track)) continue;
			entries.add(new TrackEntry(this.minecraft, this.parent, this.scrollableLayout, track));
		}
		return entries;
	}

	protected List<SoundEntry> buildSoundEntries(Collection<Identifier> sounds) {
		List<SoundEntry> entries = new ArrayList<>(sounds.size());
		TrackFilter trackFilter = this.parent.getTrackFilter();
		for (Identifier id : sounds) {
			if (!trackFilter.test(id)) continue;
			entries.add(new SoundEntry(this.minecraft, this.parent, this.scrollableLayout,
				TrackDataManager.getEmpty(id, id.toString(), id.getNamespace())));
		}
		return entries;
	}

	void setSearchText(String text) {
		if (!this.searchBox.editBox().getValue().equals(text)) {
			this.searchBox.editBox().setValue(text);
		}
	}

	public final void refresh() {
		this.updateEntries(this.buildEntries());
	}

	void rearrangeElements() {
		this.scrollableLayout.setMinWidth(this.height - this.searchBox.contentHeight() - SEARCH_BOX_PADDING * 2);
		this.scrollableLayout.setMaxHeight(this.height - this.searchBox.contentHeight() - SEARCH_BOX_PADDING * 2);
	}

	void setHeight(int height) {
		this.height = height;
		this.rearrangeElements();
	}

	abstract Component emptyListMessage();
}
//? }
