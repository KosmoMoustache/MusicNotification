package net.kosmo.music.gui;

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
//? >=1.21.2 && <1.21.6 {
/*import net.minecraft.client.renderer.RenderType;
*///? } elif >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
 //?}
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;

import java.util.Collection;
import java.util.Locale;

public class JukeboxScreen extends Screen {
	private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(MusicNotificationClient.MOD_ID, "jukebox/background");
	private static final Identifier SEARCH_ICON_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "icon/search");

	private static final Component TITLE = Component.translatable("gui.musicnotification.jukebox.title");
	private static final Component HOME_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_home");
	private static final Component SOUND_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_sound");
	private static final Component HISTORY_TAB_TITLE = Component.translatable("gui.musicnotification.jukebox.tab_history");
	private static final Component SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);
	private static final Component SELECTED_SOUND_TAB_TITLE = SOUND_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);
	private static final Component SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.plainCopy().withStyle(ChatFormatting.UNDERLINE);

	private static final Component STOP_SOUND_BUTTON = Component.translatable("gui.musicnotification.jukebox.stop_sound_button");
	private static final Component MASTER_VOLUME_ZERO = Component.translatable("gui.musicnotification.jukebox.master_volume_zero");
	private static final Component MUSIC_VOLUME_ZERO = Component.translatable("gui.musicnotification.jukebox.music_volume_zero");
	private static final Component CLEAR_HISTORY = Component.translatable("gui.musicnotification.jukebox.clear_history");

	private static final Component SEARCH_TEXT = Component.translatable("gui.musicnotification.jukebox.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
	private static final Component EMPTY_SEARCH_TEXT = Component.translatable("gui.musicnotification.jukebox.search_empty").withStyle(ChatFormatting.GRAY);
	private static final Component EMPTY_HISTORY_TEXT = Component.translatable("gui.musicnotification.jukebox.history_empty").withStyle(ChatFormatting.GRAY);

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private final Screen parent;

	Tab currentTab = Tab.HOME;
	private EditBox searchBox;
	private JukeboxEntryList soundList;
	private String filter;

	private Button homeButton;
	private Button historyButton;
	private Button soundButton;
	private Button stopSoundButton;
	private Button clearHistoryButton;

	public JukeboxScreen() {
		super(TITLE);
	}

	@Override
	public void init() {
		this.layout.addTitleHeader(TITLE, this.font);

		this.soundList = new JukeboxEntryList(this, this.minecraft, this.width, this.listEnd() - 88, 88, 36);

		int middle = Config.options().DEBUG_MOD
			? this.soundList.getRowWidth() / 3
			: this.soundList.getRowWidth() / 2;
		int rowLeft = this.soundList.getRowLeft();
		int rowRight = this.soundList.getRowRight();

		this.homeButton = this.addRenderableWidget(Button.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).bounds(rowLeft, 43, middle, 20).build());
		this.historyButton = this.addRenderableWidget(Button.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).bounds(rowLeft + middle + 1, 43, middle, 20).build());
		this.soundButton = this.addRenderableWidget(Button.builder(SOUND_TAB_TITLE, button -> this.setCurrentTab(Tab.SOUND)).bounds(rowRight - middle + 1, 43, middle, 20).build());
		soundButton.visible = Config.options().DEBUG_MOD;

		this.stopSoundButton = this.addRenderableWidget(Button.builder(STOP_SOUND_BUTTON, button -> {
			this.minecraft.getSoundManager().stop(null, SoundSource.MUSIC);
			MusicNotificationClient.currentlyPlaying = null;
//			this.setCurrentTab(this.currentTab);
		}).bounds(this.soundList.getRowLeft() - 1, this.listEnd() + 10, this.soundList.getRowRight() - this.soundList.getRowLeft() + 1, 20).build());

		this.clearHistoryButton = this.addRenderableWidget(Button.builder(CLEAR_HISTORY, button -> {
			TrackHistory.getInstance().clear();
			this.setCurrentTab(Tab.HISTORY);
		}).bounds(10, 10, this.font.width(CLEAR_HISTORY) + 8, 20).build());
		this.clearHistoryButton.visible = false;

		this.searchBox = new EditBox(this.font, this.marginX() + 28, 74, 200, 15, SEARCH_TEXT);
		this.searchBox.setMaxLength(255);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(CommonColors.WHITE);
		this.searchBox.setValue("");
		this.searchBox.setHint(SEARCH_TEXT);
		this.searchBox.setResponder(this::onSearchChange);

		this.addRenderableWidget(this.searchBox);
		this.addWidget(soundList);
		this.setCurrentTab(Tab.HOME);
		this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).build());

		this.layout.visitWidgets(this::addRenderableWidget);
		this.layout.visitWidgets(this::addRenderableWidget);

		this.repositionElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		this.soundList.updateSizeAndPosition(this.width, this.listEnd() - 88, 88);
		this.searchBox.setPosition(this.marginX() + 28, 74);
		int middle = Config.options().DEBUG_MOD
			? this.soundList.getRowWidth() / 3
			: this.soundList.getRowWidth() / 2;
		int rowLeft = this.soundList.getRowLeft();
		int rowRight = this.soundList.getRowRight();

		this.homeButton.setPosition(rowLeft, 45);
		this.historyButton.setPosition((rowLeft + middle + 1), 45);
		this.soundButton.setPosition(rowRight - middle + 1, 45);
		this.stopSoundButton.setPosition(this.soundList.getRowLeft(), this.listEnd() + 10);
		this.clearHistoryButton.setPosition(10, 10);
	}

	@Override
	//~ if >26 renderBackground -> extractBackground {
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(graphics, mouseX, mouseY, partialTick);
	//~ }
		int i = this.marginX() + 3;

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED,BACKGROUND_TEXTURE, i, 64, 236, this.getScreenHeight() + 24);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED,SEARCH_ICON_TEXTURE, i + 10, 96, 12, 12);
	}

	@Override
	//~ if >26 render -> extractRenderState {
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
    //~ }
		if (this.minecraft.options.getSoundSourceVolume(SoundSource.MASTER) == 0f) {
			this.stopSoundButton.setMessage(MASTER_VOLUME_ZERO);
		} else if (this.minecraft.options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) {
			this.stopSoundButton.setMessage(MUSIC_VOLUME_ZERO);
		}

		if (!this.soundList.isEmpty()) {
			//~ if >26 render -> extractRenderState
			this.soundList.extractRenderState(graphics, mouseX, mouseY, delta);
		} else if (!this.searchBox.getValue().isEmpty()) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(this.minecraft.font, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.listEnd()) / 2, CommonColors.WHITE);
		} else if (this.currentTab == Tab.HISTORY) {
			//~ if >26 drawCenteredString -> centeredText
			graphics.centeredText(this.minecraft.font, EMPTY_HISTORY_TEXT, this.width / 2, (72 + this.listEnd()) / 2, CommonColors.WHITE);
		}

		//~ if >26 render -> extractRenderState
		this.searchBox.extractRenderState(guiGraphics, mouseX, mouseY, delta);
	}

	private void setCurrentTab(Tab currentTab) {
		if (this.currentTab != currentTab) {
			this.searchBox.setValue("");
		}

		this.currentTab = currentTab;
		this.homeButton.setMessage(HOME_TAB_TITLE);
		this.historyButton.setMessage(HISTORY_TAB_TITLE);
		this.soundButton.setMessage(SOUND_TAB_TITLE);

		this.clearHistoryButton.visible = false;

//		boolean listEmpty = false;
		switch (currentTab) {
			case HOME: {
				this.homeButton.setMessage(SELECTED_HOME_TAB_TITLE);
				Collection<TrackData> col = TrackDataManager.getInstance().tracks.values();
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(col, this.soundList.scrollAmount());
				break;
			}
			case SOUND: {
				this.soundButton.setMessage(SELECTED_SOUND_TAB_TITLE);
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
				//~ if >1.21.1 'getScrollAmount()' -> 'scrollAmount()'
				this.soundList.update(col, this.soundList.scrollAmount());
				break;
			}
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}


//	@Override
//	public boolean mouseClicked(double mouseX, double mouseY, int button) {
//		this.searchBox.moveCursorToEnd(false);
//		this.searchBox.setHighlightPos(0);
//		return super.mouseClicked(mouseX, mouseY, button);
//	}

//	@Override
//	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//		if (!this.searchBox.isFocused() && KeyBinding.getKeyMapping().matches(keyCode, scanCode)) {
//			this.onClose();
//			return true;
//		}
//		return super.keyPressed(keyCode, scanCode, modifiers);
//	}

	private void onSearchChange(String filter) {
		if (!(filter = filter.toLowerCase(Locale.ROOT)).equals(this.filter)) {
			this.soundList.setFilter(filter);
			this.filter = filter;
			this.setCurrentTab(this.currentTab);
		}
	}

	private int marginX() {
		return (this.width - 236) / 2;
	}

	private int getScreenHeight() {
		return Math.max(52, this.height - 128 - 16);
	}

	private int listEnd() {
		return 80 + this.getScreenHeight() - 8;
	}

	enum Tab {
		HOME,
		SOUND,
		HISTORY
	}
}
