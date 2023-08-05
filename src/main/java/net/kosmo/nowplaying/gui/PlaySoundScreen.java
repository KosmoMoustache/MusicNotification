package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.mixin.IMixinMusicTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Locale;


public class PlaySoundScreen extends Screen {
    protected static final Identifier TEXTURE = new Identifier(NowPlaying.MOD_ID, "textures/gui/play_sound.png");

    private static final Text TITLE = Text.translatable("gui.nowplaying.playsound.title");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_home");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_history");
    private static final Text SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);

    private static final Text NOW_PLAYING_TEXT = Text.translatable("gui.nowplaying.playsound.play", NowPlaying.nowPlaying);
    private static final Text RESET_MUSIC_TRACKER = Text.translatable("gui.nowplaying.playsound.reset_music_tracker", NowPlaying.nowPlaying);

    private static final Text SEARCH_TEXT = Text.translatable("gui.nowplaying.playsound.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);

    SoundListWidget soundList;
    TextFieldWidget searchBox;
    private Tab currentTab = Tab.HOME;
    private String currentSearch = "";

    private ButtonWidget currentPlaying;

    private ButtonWidget homeTabButton;
    private ButtonWidget historyTabButton;
    private ButtonWidget resetMusicTrackerTimerButton;


    private boolean initialized = false;

    public PlaySoundScreen() {
        super(TITLE);
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
        super.tick();
    }

    @Override
    public void init() {
        if (this.initialized) {
            this.soundList.updateSize(this.width, this.height, 64 + this.getScreenHeight(), this.getEntryListBottom() - 64 - 4);
        } else {
            this.soundList = new SoundListWidget(this, this.client, this.width, this.height, 88, this.getEntryListBottom(), 36);
        }

        int i = this.soundList.getRowWidth() / 2;
        int j = this.soundList.getRowLeft();
        int k = this.soundList.getRowRight();
        int l = this.textRenderer.getWidth(RESET_MUSIC_TRACKER) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;

        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions(j + 2, 45, i, 20).build());
        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(k - i + 2, 45, i, 20).build());

        this.currentPlaying = this.addDrawableChild(ButtonWidget.builder(NOW_PLAYING_TEXT, button -> this.client.getSoundManager().stop(NowPlaying.nowPlaying)).dimensions(n, m, l, 20).build());
        this.resetMusicTrackerTimerButton = this.addDrawableChild(ButtonWidget.builder(RESET_MUSIC_TRACKER, button -> {
            this.client.getSoundManager().stopAll();
            ((IMixinMusicTracker) this.client.getMusicTracker()).setTimeUntilNextSong(0);
        }).dimensions(n, m + 20, l, 20).build());

        String search = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.getSearchBoxX() + 29, 75, 198, 13, SEARCH_TEXT);
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(search);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(this::onSearchChange);


        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.soundList);

        this.setCurrentTab(this.currentTab);
        this.initialized = true;
    }

    private int getEntryListBottom() {
        return 80 + this.getScreenHeight() - 8;
    }

    @Override
    public void renderBackground(DrawContext context) {
        int i = this.getSearchBoxX() + 3;
        super.renderBackground(context);
        context.drawNineSlicedTexture(TEXTURE, i, 64, 236, this.getScreenHeight() + 16, 8, 236, 34, 1, 1);
        context.drawTexture(TEXTURE, i + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawTextWithShadow(this.client.textRenderer, this.currentTab.name(), this.getSearchBoxX() + 8, 35, -1);

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        }

        this.searchBox.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void setCurrentTab(Tab tab) {
        this.currentTab = tab;
        this.homeTabButton.setMessage(HOME_TAB_TITLE);
        this.historyTabButton.setMessage(HISTORY_TAB_TITLE);

        switch (this.currentTab) {
            case HOME:
                this.homeTabButton.setMessage(SELECTED_HOME_TAB_TITLE);
                Collection<Identifier> collection = NowPlaying.musicController.getEntries().keySet();
                this.soundList.update(collection, this.soundList.getScrollAmount());
                break;
            case HISTORY:
                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
                break;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void onSearchChange(String text) {
        if (!(text = text.toLowerCase(Locale.ROOT)).equals(this.currentSearch)) {
            this.soundList.setCurrentSearch(text);
            this.currentSearch = text;
            this.setCurrentTab(this.currentTab);
        }
    }

    public static enum Tab {
        HOME,
        HISTORY;
    }
}
