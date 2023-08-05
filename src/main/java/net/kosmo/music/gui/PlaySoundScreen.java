package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.mixin.IMixinMusicTracker;
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
    protected static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier("textures/gui/social_interactions.png");

    private static final Text HOME_TAB_TITLE = Text.translatable("gui.mn.tab_title");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.mn.tab_history");
    private static final Text SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);

    private static final Text NOW_PLAYING_TEXT = Text.translatable("gui.mn.play", ClientMusic.nowPlaying);

    private static final Text SEARCH_TEXT = Text.translatable("gui.mn.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private static final Text BLOCKING_TEXT = Text.translatable("gui.socialInteractions.blocking_hint");

    SoundListWidget soundList;

    private Tab currentTab = Tab.HOME;

    TextFieldWidget searchBox;
    private String currentSearch = "";

    private ButtonWidget currentPlaying;

    private ButtonWidget homeTabButton;
    private ButtonWidget historyTabButton;
    private ButtonWidget resetMusicTrackerTimerButton;


    private boolean initialized = false;

    public PlaySoundScreen() {
        super(Text.translatable("kosmo.mn.gui"));
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

        int i = this.soundList.getRowWidth() / 3;
        int j = this.soundList.getRowLeft();
        int k = this.soundList.getRowRight();
        int l = this.textRenderer.getWidth(BLOCKING_TEXT) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;

        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions((j + k - i) / 2 + 1, 45, i, 20).build());
        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(k - i + 1, 45, i, 20).build());
    this.resetMusicTrackerTimerButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.mn.reset_music_tracker_timer"), button -> {
            this.client.getSoundManager().stopAll();
        ((IMixinMusicTracker) this.client.getMusicTracker()).setTimeUntilNextSong(0);
        }).dimensions(n, m+20, l, 20).build());

        String search = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.getSearchBoxX() + 29, 75, 198, 13, SEARCH_TEXT);
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(search);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(this::onSearchChange);

        this.currentPlaying = this.addDrawableChild(ButtonWidget.builder(NOW_PLAYING_TEXT, button -> this.client.getSoundManager().stop(ClientMusic.nowPlaying)).dimensions(n, m, l, 20).build());

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
        context.drawNineSlicedTexture(SOCIAL_INTERACTIONS_TEXTURE, i, 64, 236, this.getScreenHeight() + 16, 8, 236, 34, 1, 1);
        context.drawTexture(SOCIAL_INTERACTIONS_TEXTURE, i + 10, 76, 243, 1, 12, 12);
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
        switch (this.currentTab) {
            case HOME:
                this.homeTabButton.setMessage(SELECTED_HOME_TAB_TITLE);
                Collection<Identifier> collection = ClientMusic.musicController.getEntries().keySet();
                this.soundList.update(collection, this.soundList.getScrollAmount());
                break;
            case HISTORY:
                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
                break;
        }


        this.currentTab = tab;
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
