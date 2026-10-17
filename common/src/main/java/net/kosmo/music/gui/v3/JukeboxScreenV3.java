package net.kosmo.music.gui.v3;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.TrackHistory;
import net.kosmo.music.config.Config;
import net.kosmo.music.gui.v3.filter.FilterPanel;
import net.kosmo.music.resource.TrackFilter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class JukeboxScreenV3 extends Screen {
	public static final int SCROLLBAR_RESERVE = AbstractScrollArea.SCROLLBAR_WIDTH + 2; // 6 + 2 = 8
	public static final int OVERLAY_WIDTH = 228; // 220 entries + 8 reserve
	private static final int FILTER_WIDTH = 150;

	private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("friends/background");
	private static final Component TITLE = Component.translatable("gui.musicnotification.jukebox.title");
	private static final Component CLEAR_HISTORY = Component.translatable("gui.musicnotification.jukebox.clear_history");
	private static final Component STOP_SOUND_BUTTON = Component.translatable("gui.musicnotification.jukebox.stop_sound_button");

	private final Screen parent;
	private final TabManager tabManager;
	private final Button clearHistoryButton;
	private final Button stopSoundButton;
	private final LinearLayout layout = LinearLayout.vertical();
	private final FilterPanel filterPanel;
	private TrackFilter trackFilter;
	private @Nullable HomeTab homeTab;
	private @Nullable HistoryTab historyTab;
	private @Nullable MuteTab muteTab;
	private @Nullable DebugTab debugTab;
	private @Nullable TabNavigationBar tabNavigationBar;
	private @Nullable LinearLayout contentLayout;

	public JukeboxScreenV3(@Nullable Screen parent) {
		super(TITLE);
		this.parent = parent;
		this.tabManager = new TabManager(this::addRenderableWidget, this::removeWidget, this::selectTab, this::deselectTab);
		this.trackFilter = TrackFilter.empty();
		this.filterPanel = new FilterPanel(this.minecraft, this, FILTER_WIDTH, this.height - 80);

		this.clearHistoryButton = this.addRenderableWidget(Button.builder(CLEAR_HISTORY, button -> {
			TrackHistory.getInstance().clear();
			this.refreshLists();
		}).bounds(10, 10, this.font.width(CLEAR_HISTORY) + 8, 20).build());
		this.clearHistoryButton.visible = false;

		this.stopSoundButton = this.addRenderableWidget(Button.builder(STOP_SOUND_BUTTON, button -> {
			if (this.minecraft.options.getSoundSourceVolume(SoundSource.MASTER) == 0f || this.minecraft.options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) {
				this.minecraft.gui.setScreen(new SoundOptionsScreen(this, this.minecraft.options));
			}
			this.minecraft.getSoundManager().stop(null, SoundSource.MUSIC);
			MusicNotificationClient.currentlyPlaying = null;
			button.setFocused(false);
		}).build());
	}

	@Override
	public void init() {
		if (this.parent != null) {
			this.parent.init(this.width, this.height);
		}

		this.homeTab = new HomeTab(this.minecraft, this, getOverlayWidth(), 220);
		this.historyTab = new HistoryTab(this.minecraft, this, getOverlayWidth(), 220);
		this.muteTab = new MuteTab(this.minecraft, this, getOverlayWidth(), 220);
		this.debugTab = new DebugTab(this.minecraft, this, getOverlayWidth(), 220);

		boolean showMute = Config.options().SHOW_MUTE;
		boolean showDebug = Config.options().DEBUG_MOD;
		int visibleTabs = 2 + (showMute ? 1 : 0) + (showDebug ? 1 : 0);
		int tabWidth = getOverlayWidth() / visibleTabs;

		TabNavigationBar.Builder builder = TabNavigationBar.builder(this.tabManager, 0, 0, getOverlayWidth(), 20)
			.addTab(new JukeboxTabButton(this.tabManager, this.homeTab, tabWidth, 20), homeTab)
			.addTab(new JukeboxTabButton(this.tabManager, this.historyTab, tabWidth, 20), historyTab);

		if (showMute)
			builder.addTab(new JukeboxTabButton(this.tabManager, this.muteTab, tabWidth, 20), muteTab);
		if (showDebug)
			builder.addTab(new JukeboxTabButton(this.tabManager, this.debugTab, tabWidth, 20), debugTab);

		this.tabNavigationBar = builder.build();
		this.addRenderableWidget(tabNavigationBar);

		this.filterPanel.visitChildren(this::addRenderableWidget);
		this.filterPanel.setVisible(false);

		this.contentLayout = this.layout.addChild(LinearLayout.vertical());
		this.tabManager.setCurrentTab(this.homeTab, false, false);
		this.layout.visitWidgets(this::addRenderableWidget);
		this.repositionElements();
	}

	@Override
	protected void repositionElements() {

		this.homeTab.setHeight(this.height - 80);
		this.historyTab.setHeight(this.height - 80);
		this.muteTab.setHeight(this.height - 80);
		this.debugTab.setHeight(this.height - 80);

		this.layout.arrangeElements();

		FrameLayout.alignInRectangle(this.layout, this.getRectangle(), 0.5F, 0.5F);
		this.tabNavigationBar.setPosition(this.layout.getX(), this.layout.getY() - 20 - 7);
		this.tabNavigationBar.arrangeElements(this.width);

		this.filterPanel.setHeight(this.height - 80);
		this.filterPanel.arrangeElements();
		this.filterPanel.setPosition(this.layout.getX() - FILTER_WIDTH - 16, this.layout.getY() - 20 - 7);

		conditionalVisibility(this.tabManager.getCurrentTab());
		this.stopSoundButton.setPosition(this.layout.getX() + this.getContentWidth() / 2 - this.stopSoundButton.getWidth() / 2, this.layout.getY() + this.layout.getHeight() + 5);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);

		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			BACKGROUND_SPRITE,
			this.layout.getX() - 8,
			this.layout.getY() - 8,
			this.layout.getWidth() + 16,
			this.layout.getHeight() + 16 + 1
		);

		if (this.filterPanel.isVisible()) {
			this.filterPanel.extractBackground(graphics);
		}
	}

	/**
	 * Tab Manager onSelected
	 */
	private void selectTab(Tab tab) {
		if (this.contentLayout != null) {
			this.contentLayout.addChild(tab.getLayout());
		}
		// The search query is shared by the tabs that use TrackFilter: keep their search box in sync.
		if (tab instanceof AbstractJukeboxTab jukeboxTab) {
			jukeboxTab.setSearchText(this.trackFilter.query());
		}
		this.refreshLists();
		this.filterPanel.refresh();
		this.conditionalVisibility(tab);
	}

	public TrackFilter getTrackFilter() {
		return this.trackFilter;
	}

	/**
	 * Applies a change to the track filter, then refreshes the current list.
	 */
	public void updateFilter(UnaryOperator<TrackFilter> change) {
		this.trackFilter = change.apply(this.trackFilter);
		this.refreshLists();
		this.filterPanel.refresh();
	}

	/**
	 * Resets every filter (query, namespace, albums) and syncs the UI.
	 */
	public void clearFilters() {
		this.updateFilter(f -> TrackFilter.empty());
		if (this.tabManager.getCurrentTab() instanceof AbstractJukeboxTab tab) {
			tab.setSearchText("");
		}
	}

	private void conditionalVisibility(Tab tab) {
		this.clearHistoryButton.visible = tab instanceof HistoryTab;
		this.filterPanel.setVisible(tab instanceof HomeTab && this.minecraft.getWindow().getGuiScaledWidth() / 2 > OVERLAY_WIDTH / 2 + FILTER_WIDTH + 16 && Config.options().SHOW_FILTER_PANEL);
	}

	/**
	 * TabManager onDeselected
	 */
	private void deselectTab(Tab tab) {
		if (this.contentLayout != null) {
			this.contentLayout.removeChildren();
		}
	}

	public int getOverlayWidth() {
		// panel width
		return OVERLAY_WIDTH;
	}

	public int getContentWidth() {
		// 220, entries
		return OVERLAY_WIDTH - SCROLLBAR_RESERVE;
	}

	/**
	 * Refresh the current tab's list and reposition elements.
	 */
	public void refreshLists() {
		if (this.tabManager.getCurrentTab() instanceof AbstractJukeboxTab tab) {
			tab.refresh();
		}
		this.repositionElements();
	}
}
