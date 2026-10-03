package net.kosmo.music.gui.v2;

import com.google.common.collect.Lists;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.TrackHistory;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.SoundData;
import net.kosmo.music.resource.TrackData;
import net.kosmo.music.resource.TrackDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//? >=1.21.2 && <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///? } elif >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
 //?}

public class JukeboxScreenV2 extends Screen {
	private static final Identifier SEARCH_ICON_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "icon/search");

	private static final int PANEL_WIDTH = 320;
	private static final int ALBUM_COLUMN_WIDTH = 44;
	private static final int TAB_Y = 43;
	private static final int TAB_HEIGHT = 20;
	private static final int TAB_GAP = 2;
	private static final int SEARCH_Y = 74;
	private static final int LIST_Y = 88;
	private static final int ITEM_HEIGHT = 40;
	private static final int ALBUM_ITEM_HEIGHT = 26;
	private static final int PANEL_COLOR = ARGB.color(200, 12, 12, 12);

	private static final Component TITLE = Component.translatable("gui.musicnotification.jukebox.title");
	private static final Component HOME_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_home");
	private static final Component HISTORY_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_history");
	private static final Component MUTE_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_mute");
	private static final Component DEBUG_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_debug");
	private static final Component SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);
	private static final Component SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);
	private static final Component SELECTED_MUTE_TAB_TITLE = MUTE_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);
	private static final Component SELECTED_DEBUG_TAB_TITLE = DEBUG_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);

	private static final Component STOP_SOUND_BUTTON = Component.translatable("gui.musicnotification.jukebox.stop_sound_button");
	private static final Component MASTER_VOLUME_ZERO = Component.translatable("gui.musicnotification.jukebox.master_volume_zero");
	private static final Component MUSIC_VOLUME_ZERO = Component.translatable("gui.musicnotification.jukebox.music_volume_zero");
	private static final Component CLEAR_HISTORY = Component.translatable("gui.musicnotification.jukebox.clear_history");

	private static final Component EXTENDED_FILTER = Component.translatable("gui.musicnotification.jukebox.extended_filter");

	private static final Component SEARCH_TEXT = Component.translatable("gui.musicnotification.jukebox.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	private static final Component EMPTY_SEARCH_TEXT = Component.translatable("gui.musicnotification.jukebox.search_empty").withStyle(ChatFormatting.GRAY);
	private static final Component EMPTY_HISTORY_TEXT = Component.translatable("gui.musicnotification.jukebox.history_empty").withStyle(ChatFormatting.GRAY);
	private static final Component EMPTY_MUTE_TEXT = Component.translatable("gui.musicnotification.jukebox.mute_empty").withStyle(ChatFormatting.GRAY);

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

	Tab currentTab = Tab.HOME;
	private EditBox searchBox;
	private JukeboxEntryList soundList;
	private AlbumList albumList;
	@Nullable
	private String filter;
	@Nullable
	private String selectedAlbum;
	private Set<String> renderedAlbums = new LinkedHashSet<>();

	private Button homeButton;
	private Button historyButton;
	private Button muteButton;
	private Button debugButton;
	private Button stopSoundButton;
	private Button clearHistoryButton;
	private ExtendedFilter extendedFilter;

	public JukeboxScreenV2() {
		super(TITLE);
	}

	@Override
	public void init() {
		this.soundList = new JukeboxEntryList(this, this.minecraft, this.trackListWidth(), this.listEnd() - LIST_Y, LIST_Y, ITEM_HEIGHT, this.rowWidth());
		this.albumList = new AlbumList(this, this.minecraft, this.albumColumnWidth(), this.listEnd() - LIST_Y, LIST_Y, ALBUM_ITEM_HEIGHT);

		this.homeButton = this.addRenderableWidget(Button.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).bounds(0, TAB_Y, 0, TAB_HEIGHT).build());
		this.historyButton = this.addRenderableWidget(Button.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).bounds(0, TAB_Y, 0, TAB_HEIGHT).build());
		this.muteButton = this.addRenderableWidget(Button.builder(MUTE_TAB_TITLE, button -> this.setCurrentTab(Tab.MUTE)).bounds(0, TAB_Y, 0, TAB_HEIGHT).build());
		this.debugButton = this.addRenderableWidget(Button.builder(DEBUG_TAB_TITLE, button -> this.setCurrentTab(Tab.DEBUG)).bounds(0, TAB_Y, 0, TAB_HEIGHT).build());
		this.debugButton.visible = Config.options().DEBUG_MOD;

		this.stopSoundButton = this.addRenderableWidget(Button.builder(STOP_SOUND_BUTTON, button -> {
			if (this.minecraft.options.getSoundSourceVolume(SoundSource.MASTER) == 0f || this.minecraft.options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) {
				this.minecraft.gui.setScreen(new SoundOptionsScreen(this, this.minecraft.options));
			}
			this.minecraft.getSoundManager().stop(null, SoundSource.MUSIC);
			MusicNotificationClient.currentlyPlaying = null;
//			this.setCurrentTab(this.currentTab);
		}).bounds(this.marginX() + 1, this.listEnd() + (Config.options().WIP_FILTER_IN_JUKEBOX ? 12 : 10), PANEL_WIDTH - 2, 20).build());

		this.clearHistoryButton = this.addRenderableWidget(Button.builder(CLEAR_HISTORY, button -> {
			TrackHistory.getInstance().clear();
			this.setCurrentTab(Tab.HISTORY);
		}).bounds(10, 10, this.font.width(CLEAR_HISTORY) + 8, 20).build());
		this.clearHistoryButton.visible = false;

		this.extendedFilter = new ExtendedFilter(this, this.font, this.marginX() + 13, 73, PANEL_WIDTH - 26, 15, EXTENDED_FILTER, this::onSearchChange);

		this.searchBox = new EditBox(this.font, this.trackListLeft() + 22, SEARCH_Y, PANEL_WIDTH - ALBUM_COLUMN_WIDTH - 26, 15, SEARCH_TEXT);
		this.searchBox.setMaxLength(255);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(CommonColors.WHITE);
		this.searchBox.setValue("");
		this.searchBox.setHint(SEARCH_TEXT);
		this.searchBox.setResponder(this::onSearchChange);

		updateStopSoundButtonMessage();

		this.addRenderableWidget(this.searchBox);
		if (Config.options().WIP_FILTER_IN_JUKEBOX) {
			this.addRenderableWidget(this.extendedFilter);
		}
		this.addRenderableWidget(soundList);
		this.addRenderableWidget(albumList);
		this.setCurrentTab(Tab.HOME);
		this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).build());

		this.layout.visitWidgets(this::addRenderableWidget);

		this.repositionElements();
	}

	@Override
	public void added() {
		super.added();
		updateStopSoundButtonMessage();
	}

	private void updateStopSoundButtonMessage() {
		if (this.stopSoundButton != null) {
			if (this.minecraft.options.getSoundSourceVolume(SoundSource.MASTER) == 0f) {
				this.stopSoundButton.setMessage(MASTER_VOLUME_ZERO);
			} else if (this.minecraft.options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) {
				this.stopSoundButton.setMessage(MUSIC_VOLUME_ZERO);
			} else {
				this.stopSoundButton.setMessage(STOP_SOUND_BUTTON);
			}
		}
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		int listY = Config.options().WIP_FILTER_IN_JUKEBOX ? 110 : LIST_Y;
		int listHeight = this.listEnd() - (Config.options().WIP_FILTER_IN_JUKEBOX ? 103 : LIST_Y);

		positionList(this.soundList, this.trackListLeft(), listY, this.trackListWidth(), listHeight);
		positionList(this.albumList, this.albumColumnLeft(), listY, this.albumColumnWidth(), listHeight);

		this.searchBox.setPosition(this.trackListLeft() + 22, (Config.options().WIP_FILTER_IN_JUKEBOX ? 93 : SEARCH_Y));
		this.stopSoundButton.setPosition(this.marginX() + 1, this.listEnd() + 12);

		this.repositionTabs();
		this.clearHistoryButton.setPosition(10, 10);
	}

	private static void positionList(net.minecraft.client.gui.components.AbstractSelectionList<?> list, int x, int y, int width, int height) {
		//? if >=1.21.10 {
		list.updateSizeAndPosition(width, height, x, y);
		//? } else {
		/*list.updateSizeAndPosition(width, height, y);
		list.setX(x);
		*///? }
	}

	private void repositionTabs() {
		List<Button> tabs = new ArrayList<>();
		tabs.add(this.homeButton);
		tabs.add(this.historyButton);
		tabs.add(this.muteButton);
		if (this.debugButton.visible) {
			tabs.add(this.debugButton);
		}

		int left = this.marginX() + 8;
		int total = PANEL_WIDTH - 16;
		int width = (total - (tabs.size() - 1) * TAB_GAP) / tabs.size();
		for (int i = 0; i < tabs.size(); i++) {
			tabs.get(i).setWidth(width);
			tabs.get(i).setPosition(left + i * (width + TAB_GAP), TAB_Y);
		}
	}

	@Override
		//~ if >26 renderBackground -> extractBackground {
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
		//~}
		int left = this.marginX();
		int top = 64;
		int bottom = 64 + this.getScreenHeight() + (Config.options().WIP_FILTER_IN_JUKEBOX ? 24 : 16);
		graphics.fill(left, top, left + PANEL_WIDTH, bottom, PANEL_COLOR);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_ICON_TEXTURE, this.trackListLeft() + 6, (Config.options().WIP_FILTER_IN_JUKEBOX ? 95 : SEARCH_Y + 2), 12, 12);
	}

	@Override
		//~ if >26 render -> extractRenderState {
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		//~}

		if (!this.soundList.isEmpty()) {
			// lists are rendered by super.extractRenderState
		} else if (!this.searchBox.getValue().isEmpty()) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(this.minecraft.font, EMPTY_SEARCH_TEXT, this.trackListLeft() + this.trackListWidth() / 2, (72 + this.listEnd()) / 2, CommonColors.WHITE);
		} else if (this.currentTab == Tab.HISTORY) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(this.minecraft.font, EMPTY_HISTORY_TEXT, this.trackListLeft() + this.trackListWidth() / 2, (72 + this.listEnd()) / 2, CommonColors.WHITE);
		} else if (this.currentTab == Tab.MUTE) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(this.minecraft.font, EMPTY_MUTE_TEXT, this.trackListLeft() + this.trackListWidth() / 2, (72 + this.listEnd()) / 2, CommonColors.WHITE);
		}
	}

	private void setCurrentTab(Tab currentTab) {
		if (this.currentTab != currentTab) {
			this.searchBox.setValue("");
		}

		this.currentTab = currentTab;
		this.homeButton.setMessage(HOME_TAB_TITLE);
		this.historyButton.setMessage(HISTORY_TAB_TITLE);
		this.muteButton.setMessage(MUTE_TAB_TITLE);
		this.debugButton.setMessage(DEBUG_TAB_TITLE);

		this.clearHistoryButton.visible = false;

		switch (currentTab) {
			case HOME: {
				this.homeButton.setMessage(SELECTED_HOME_TAB_TITLE);
				Collection<TrackData> col = TrackDataManager.getInstance().tracks.values();
				this.updateAlbumList(col);
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(col, this.soundList.scrollAmount());
				break;
			}
			case DEBUG: {
				this.debugButton.setMessage(SELECTED_DEBUG_TAB_TITLE);
				this.albumList.visible = false;
				Collection<SoundData> col = Lists.newArrayList();
				this.minecraft.getSoundManager().getAvailableSounds().forEach(id -> col.add(new SoundData(id)));
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(col, this.soundList.scrollAmount());
				break;
			}
			case HISTORY: {
				this.historyButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
				this.clearHistoryButton.visible = true;
				Collection<TrackData> col = TrackHistory.getInstance().getHistory();
				this.updateAlbumList(col);
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(col, this.soundList.scrollAmount());
				break;
			}
			case MUTE: {
				this.muteButton.setMessage(SELECTED_MUTE_TAB_TITLE);
				this.albumList.visible = false;
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(List.of(), this.soundList.scrollAmount());
				break;
			}
		}
	}

	private void updateAlbumList(Collection<TrackData> tracks) {
		Map<String, TrackData> albums = new TreeMap<>();
		for (TrackData track : tracks) {
			track.album()
				.map(Component::getString)
				.filter(name -> !name.isEmpty())
				.ifPresent(name -> albums.putIfAbsent(name, track));
		}

		if (this.selectedAlbum != null && !albums.containsKey(this.selectedAlbum)) {
			this.selectedAlbum = null;
		}

		// Only rebuild the album list when the album set actually changes,
		// so selecting an album does not reset the album rail scroll position.
		if (!albums.keySet().equals(this.renderedAlbums)) {
			this.renderedAlbums = new LinkedHashSet<>(albums.keySet());

			List<AlbumEntry> entries = new ArrayList<>();
			entries.add(new AlbumEntry(this, null, null));
			for (Map.Entry<String, TrackData> album : albums.entrySet()) {
				entries.add(new AlbumEntry(this, album.getKey(), album.getValue().getAlbumCover()));
			}
			this.albumList.update(entries);
		}
		this.albumList.visible = true;
		this.soundList.setAlbumFilter(this.selectedAlbum);
	}

	public void selectAlbum(@Nullable String album) {
		if (album != null && album.equals(this.selectedAlbum)) {
			this.selectedAlbum = null;
		} else {
			this.selectedAlbum = album;
		}
		this.setCurrentTab(this.currentTab);
		this.soundList.setScrollAmount(0);
	}

	@Nullable
	public String getSelectedAlbum() {
		return this.selectedAlbum;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public String getFilter() {
		return filter;
	}

	private void onSearchChange(String filter) {
		if (!(filter = filter.toLowerCase(Locale.ROOT)).equals(this.filter)) {
			this.soundList.setFilter(filter);
			this.filter = filter;
			this.setCurrentTab(this.currentTab);
			this.searchBox.setValue(filter);
		}
	}

	private int rowWidth() {
		return this.trackListWidth() - 8;
	}

	private int marginX() {
		return (this.width - PANEL_WIDTH) / 2;
	}

	private int albumColumnLeft() {
		return this.marginX() + 4;
	}

	private int albumColumnWidth() {
		return ALBUM_COLUMN_WIDTH - 8;
	}

	private int trackListLeft() {
		return this.marginX() + ALBUM_COLUMN_WIDTH;
	}

	private int trackListWidth() {
		return PANEL_WIDTH - ALBUM_COLUMN_WIDTH;
	}

	private int getScreenHeight() {
		return Math.max(52, this.height - 128 - 16);
	}

	private int listEnd() {
		return 80 + this.getScreenHeight() - 8;
	}

	enum Tab {
		HOME,
		MUTE,
		HISTORY,
		DEBUG
	}
}
