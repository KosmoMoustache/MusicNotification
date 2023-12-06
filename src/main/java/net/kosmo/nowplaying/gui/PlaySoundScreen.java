package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.mixin.IMixinMusicTracker;
import net.kosmo.nowplaying.music.MusicEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaySoundScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("nowplaying/background");
    private static final Identifier SEARCH_ICON_TEXTURE = new Identifier("icon/search");
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);

    private static final Text TITLE = Text.translatable("gui.nowplaying.playsound.title");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_home");
    private static final Text SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    // TODO History tab
//    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.nowplaying.playsound.tab_history");
//    private static final Text SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text RESET_MUSIC_TRACKER = Text.translatable("gui.nowplaying.playsound.reset_music_tracker");
    private static final Text SEARCH_TEXT = Text.translatable("gui.nowplaying.playsound.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);

    private Tab currentTab = Tab.HOME;
    private String currentSearch = "";
    SoundListWidget soundList;
    TextFieldWidget searchBox;

//    private ButtonWidget currentPlaying;
    private ButtonWidget homeTabButton;

    private boolean initialized;

    public PlaySoundScreen(Screen parent) {
        super(TITLE);
    }

    private static List<MusicEntry> getEntries() {
        return new ArrayList<>(NowPlaying.musicManager.getEntriesValue());
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int getEntryListBottom() {
        return 80 + this.getScreenHeight() - 8;
    }

    @Override
    public void init() {
        if (this.initialized) {
//            this.soundList.updateSize(this.width, this.height, 88, this.getEntryListBottom());
        } else {
            this.soundList = new SoundListWidget(this, this.client, this.width, this.height, 88, this.getEntryListBottom(), 36);
        }

//        Text NOW_PLAYING_TEXT = getNowPlayingText();
        Text STOP_SOUND_TEXT = Text.translatable("gui.nowplaying.playsound.stop_sound");

        int i = this.soundList.getRowWidth() / 2;
        int j = this.soundList.getRowLeft();
        int k = this.soundList.getRowRight();
        int l = this.textRenderer.getWidth(STOP_SOUND_TEXT) + 40;
        int l1 = this.textRenderer.getWidth(RESET_MUSIC_TRACKER) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;
        int n1 = (this.width - l1) / 2 + 3;

        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions(j + 2, 45, i, 20).build());
//        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(k - i + 2, 45, i, 20).build());

//        this.currentPlaying = this.addDrawableChild(ButtonWidget.builder(NOW_PLAYING_TEXT, button -> {
//            if (NowPlaying.tracker.getNowPlaying().isPlaying())
//                this.client.getSoundManager().stop(NowPlaying.tracker.getNowPlaying().getSound());
//        }).dimensions(n, m, l, 20).build());
        /*this.currentPlaying = */this.addDrawableChild(ButtonWidget.builder(STOP_SOUND_TEXT, button -> {
            this.client.getSoundManager().stopAll();
        }).dimensions(n, m, l, 20).build());

        this.addDrawableChild(ButtonWidget.builder(RESET_MUSIC_TRACKER, button -> {
            this.client.getSoundManager().stopAll();
            ((IMixinMusicTracker) this.client.getMusicTracker()).setTimeUntilNextSong(0);
        }).dimensions(n1, m + 20, l1, 20).build());

        // TODO sort by album (? artist)
        // Album cover list
//        GridWidget gridWidget = new GridWidget();
//        gridWidget.getMainPositioner().marginX(1).marginBottom(1).alignHorizontalCenter();
//        GridWidget.Adder adder = gridWidget.createAdder(1);
//        adder.add(EmptyWidget.ofHeight(26), 1);
//
//        NowPlayingToast.AlbumCover.values();
//        for (NowPlayingToast.AlbumCover albumCover : NowPlayingToast.AlbumCover.values()) {
//            adder.add(new TexturedButtonWidget(this.width / 2 - 124, l + 72 + 12, 20, 20, albumCover.getTextureSlotX(), albumCover.getTextureSlotY(), 0, NowPlayingToast.TEXTURE, 256, 256, (button) -> {
//                NowPlaying.LOGGER.info("Button pressed");
//            }, Text.translatable(albumCover.name())));
//        }
//        gridWidget.refreshPositions();
//        gridWidget.forEachChild(this::addDrawableChild);

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

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getSearchBoxX() + 3;
        super.renderBackground(context, mouseX, mouseY, delta);

        // TODO: change to the new gui texture system
        context.drawGuiTexture(BACKGROUND_TEXTURE, i, 64, 236, this.getScreenHeight() + 16, 8, 236, 34, 1, 1);
        context.drawGuiTexture(SEARCH_ICON_TEXTURE, i + 10, 76, 12, 12);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTextWithShadow(this.textRenderer, this.currentTab.name(), this.getSearchBoxX() + 8, 35, -1);

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        }

        assert client != null;
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
//        this.historyTabButton.setMessage(HISTORY_TAB_TITLE);

        switch (this.currentTab) {
            case HOME:
                this.homeTabButton.setMessage(SELECTED_HOME_TAB_TITLE);
                this.soundList.update(getEntries(), this.soundList.getScrollAmount());
                break;
//            case HISTORY:
//                NowPlaying.LOGGER.info("History: " + NowPlaying.tracker.getHistory().getEntries());
//                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
//                this.soundList.update(NowPlaying.tracker.getHistory().getEntries(), this.soundList.getScrollAmount());
//                break;
        }
    }

//    public void renderNowPlayingButton() {
//        int l = this.textRenderer.getWidth(getNowPlayingText()) + 40;
//        int m = 64 + this.getScreenHeight();
//        int n = (this.width - l) / 2 + 3;
//
//        if (NowPlaying.tracker.getNowPlaying().isPlaying()) {
//            this.currentPlaying.setMessage(getNowPlayingText());
//        } else {
//            this.currentPlaying.setMessage(Text.translatable("gui.nowplaying.playsound.now_playing_none"));
//        }
//        this.currentPlaying.setWidth(this.textRenderer.getWidth(this.currentPlaying.getMessage()) + 40);
//        this.currentPlaying.setPosition(n, m);
//    }

    // TODO store in class variable
//    public Text getNowPlayingText() {
//        SoundInstance sound = NowPlaying.tracker.getNowPlaying().getSound();
//        Optional<MusicEntry> entry = Optional.empty();
//        if (sound != null) {
//            entry = NowPlaying.musicManager.getByKey(NowPlaying.getLastSegmentOfPath(sound.getId()));
//        }
//        NowPlaying.LOGGER.info("{} {}", sound, entry);
//        if (entry.isPresent()) {
//            return Text.translatable("gui.nowplaying.playsound.now_playing", entry.get().title, entry.get().author, entry.get().soundtrack);
//        }
//        return Text.translatable("gui.nowplaying.playsound.now_playing_none");
//    }

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

    public enum Tab {
        HOME,
//        HISTORY;
    }
}
