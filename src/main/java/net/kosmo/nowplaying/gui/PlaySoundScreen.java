package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.mixin.IMixinMusicTracker;
import net.kosmo.nowplaying.music.MusicEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaySoundScreen extends Screen {
    protected static final Identifier TEXTURE = new Identifier(NowPlaying.MOD_ID, "textures/gui/play_sound.png");
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);

    private static final Text TITLE = Text.translatable("gui.nowplaying.playsound.title");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_home");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_history");
    private static final Text SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);

    private static final Text RESET_MUSIC_TRACKER = Text.translatable("gui.nowplaying.playsound.reset_music_tracker");

    private static final Text SEARCH_TEXT = Text.translatable("gui.nowplaying.playsound.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private final Screen parent;

    SoundListWidget soundList;
    TextFieldWidget searchBox;
    private Tab currentTab = Tab.HOME;
    private String currentSearch = "";

    private ButtonWidget currentPlaying;

    private TexturedButtonWidget sortByDisc;

    private ButtonWidget homeTabButton;
    private ButtonWidget historyTabButton;
    private ButtonWidget resetMusicTrackerTimerButton;


    private boolean initialized;

    public PlaySoundScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @NotNull
    private static List<MusicEntry> getEntries() {
        List<MusicEntry> collection1 = new ArrayList<>();

        collection1.addAll(NowPlaying.musicManager.getEntriesValue());

        return collection1;
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
            this.soundList.updateSize(this.width, this.height, 88, this.getEntryListBottom());
        } else {
            this.soundList = new SoundListWidget(this, this.client, this.width, this.height, 88, this.getEntryListBottom(), 36);
        }

        Text NOW_PLAYING_TEXT = getNowPlayingText();

        int i = this.soundList.getRowWidth() / 2;
        int j = this.soundList.getRowLeft();
        int k = this.soundList.getRowRight();
        int l = this.textRenderer.getWidth(NOW_PLAYING_TEXT) + 40;
        int l1 = this.textRenderer.getWidth(RESET_MUSIC_TRACKER) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;
        int n1 = (this.width - l1) / 2 + 3;

        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions(j + 2, 45, i, 20).build());
        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(k - i + 2, 45, i, 20).build());

        this.currentPlaying = this.addDrawableChild(ButtonWidget.builder(NOW_PLAYING_TEXT, button -> {
            if (NowPlaying.tracker.getNowPlaying().isPlaying())
                this.client.getSoundManager().stop(NowPlaying.tracker.getNowPlaying().getSound());
        }).dimensions(n, m, l, 20).build());
        this.resetMusicTrackerTimerButton = this.addDrawableChild(ButtonWidget.builder(RESET_MUSIC_TRACKER, button -> {
            this.client.getSoundManager().stopAll();
            ((IMixinMusicTracker) this.client.getMusicTracker()).setTimeUntilNextSong(0);
        }).dimensions(n1, m + 20, l1, 20).build());

        // Create a sort widget
        // Sort button
//        this.sortByDisc = this.addDrawableChild(ButtonWidget.builder(NOW_PLAYING_TEXT, button -> {
//            this.sortBy(SortBy.DISC);
//        }).dimensions(j - 20, 45, 20, 20).build());
//        this.sortByDisc = this.addDrawableChild(new TexturedButtonWidget(0, 0, 20, 20, 0, 38, 20, PlaySoundScreen.TEXTURE, 256, 256, button -> {
//            this.sortBy(SortBy.DISC);
//            this.sortByDisc.setFocused(false);
//        }, Text.translatable("gui.nowplaying.playsound.play")));

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

    private void sortBy(SortBy sortBy) {
        NowPlaying.LOGGER.info("Sorting by " + sortBy.name());
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

        this.renderNowPlayingButton();


        context.drawTextWithShadow(this.textRenderer, this.currentTab.name(), this.getSearchBoxX() + 8, 35, -1);

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        }


        if (client.options.getSoundVolume(SoundCategory.MUSIC) == 0f) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("gui.nowplaying.playsound.volume.master"), this.width / 2, 20, WHITE_COLOR);
        } else if (client.options.getSoundVolume(SoundCategory.MASTER) == 0f) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("gui.nowplaying.playsound.volume.music"), this.width / 2, 20, WHITE_COLOR);
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
                this.soundList.update(getEntries(), this.soundList.getScrollAmount());
                break;
            case HISTORY:
                NowPlaying.LOGGER.info("History: " + NowPlaying.tracker.getHistory().getEntries());
                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
//                this.soundList.update(NowPlaying.tracker.getHistory().getEntries(), this.soundList.getScrollAmount());
                break;
        }
    }

    public void renderNowPlayingButton() {
        int l = this.textRenderer.getWidth(getNowPlayingText()) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;

        this.currentPlaying.setMessage(getNowPlayingText());
        this.currentPlaying.setWidth(this.textRenderer.getWidth(this.currentPlaying.getMessage()) + 40);
        this.currentPlaying.setPosition(n, m);

    }

    public Text getNowPlayingText() {
        return NowPlaying.tracker.getNowPlaying().isPlaying() ?
                Text.translatable("gui.nowplaying.playsound.now_playing", NowPlaying.tracker.getNowPlaying().getSound().getId().toString()) :
                Text.translatable("gui.nowplaying.playsound.now_playing_none");
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

    public static enum SortBy {
        DISC,
        NAME,
        AUTHOR,
        CATEGORY,
        DATE;
    }

    public static enum Tab {
        HOME,
        HISTORY;
    }
}
