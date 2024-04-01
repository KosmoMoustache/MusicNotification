package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PlaySoundScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(ClientMusic.MOD_ID, "play_sound/background");
    private static final Identifier SEARCH_ICON_TEXTURE = new Identifier("icon/search");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.musicnotification.playsound.tab_home");
    private static final Text MUSIC_TAB_TITLE = Text.translatable("gui.musicnotification.playsound.tab_music");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.musicnotification.playsound.tab_history");

    private static final MutableText SELECTED_HOME_TAB_TITLE;
    private static final MutableText SELECTED_MUSIC_TAB_TITLE;
    private static final MutableText SELECTED_HISTORY_TAB_TITLE;
    private static final MutableText SEARCH_TEXT;
    private static final MutableText EMPTY_SEARCH_TEXT;

    private ButtonWidget homeTabButton;
    private ButtonWidget musicTabButton;
    private ButtonWidget historyTabButton;

    TextFieldWidget searchBox;
    private String currentSearch = "";

    private Tab currentTab;
    private SoundListWidget soundList;

    boolean initialized = false;


    public PlaySoundScreen() {
        super(Text.translatable("gui.musicnotification.playsound.title"));
        this.currentTab = Tab.MUSIC;
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.soundList.setDimensionsAndPosition(this.width, this.height, 80, this.getEntryListBottom());
        } else {
            this.soundList = new SoundListWidget(this.client, this.width, this.height, 88, this.getEntryListBottom());
        }

        int i = this.soundList.getRowWidth() / 3;
        int j = this.soundList.getRowLeft();
        int k = this.soundList.getRowRight();
        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, (button) -> {
            this.setCurrentTab(Tab.HOME);
        }).dimensions(j, 45, i, 20).build());
        this.musicTabButton = this.addDrawableChild(ButtonWidget.builder(MUSIC_TAB_TITLE, (button) -> {
            this.setCurrentTab(Tab.MUSIC);
        }).dimensions((j + k - i) / 2 + 1, 45, i, 20).build());
        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, (button) -> {
            this.setCurrentTab(Tab.HISTORY);
        }).dimensions(k - i + 1, 45, i, 20).build());

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
        super.renderBackground(context, mouseX, mouseY, delta);


        int i = this.getSearchBoxX() + 3;
        context.drawGuiTexture(BACKGROUND_TEXTURE, i, 64, 238, this.getScreenHeight() + 16);
        context.drawGuiTexture(SEARCH_ICON_TEXTURE, i + 10, 76, 12, 12);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTextWithShadow(this.textRenderer, this.currentTab.name(), this.getSearchBoxX() + 8, 35, -1);

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        } else if (!this.searchBox.getText().isEmpty()) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getEntryListBottom()) / 2, -1);
        } else if (this.currentTab == Tab.MUSIC) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getEntryListBottom()) / 2, -1);
        } else if (this.currentTab == Tab.HISTORY) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getEntryListBottom()) / 2, -1);
        }

        this.searchBox.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    private void onSearchChange(String text) {
        if (!(text = text.toLowerCase(Locale.ROOT)).equals(this.currentSearch)) {
            this.currentSearch = text;
            this.soundList.setCurrentSearch(text);
        }
    }

    private int getEntryListBottom() {
        return 80 + this.getScreenHeight() - 8;
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
//        this.soundList.update(new ArrayList<>(ClientMusic.musicManager.music.values()));

        this.homeTabButton.setMessage(HOME_TAB_TITLE);
        this.musicTabButton.setMessage(MUSIC_TAB_TITLE);
        this.historyTabButton.setMessage(HISTORY_TAB_TITLE);

        boolean bl = false;
        switch (this.currentTab) {
            case HOME:
                this.homeTabButton.setMessage(SELECTED_HOME_TAB_TITLE);
                List<MusicManager.Music> collection = new ArrayList<>(ClientMusic.kResourceManager.musicManager.musics.values());
                this.soundList.update(collection);
                break;
            case MUSIC:
                this.musicTabButton.setMessage(SELECTED_MUSIC_TAB_TITLE);
//                Set<UUID> set = this.client.getSocialInteractionsManager().getHiddenPlayers();
//                bl = set.isEmpty();
//                this.soundList.update(set, this.soundList.getScrollAmount(), false);
                break;
            case HISTORY:
                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
//                SocialInteractionsManager socialInteractionsManager = this.client.getSocialInteractionsManager();
//                Stream var10000 = this.client.player.networkHandler.getPlayerUuids().stream();
//                Objects.requireNonNull(socialInteractionsManager);
//                Set<UUID> set2 = (Set)var10000.filter(socialInteractionsManager::isPlayerBlocked).collect(Collectors.toSet());
//                bl = set2.isEmpty();
//                this.soundList.update(set2, this.soundList.getScrollAmount(), false);
        }

        NarratorManager narratorManager = this.client.getNarratorManager();
        if (!this.searchBox.getText().isEmpty() && this.soundList.isEmpty() && !this.searchBox.isFocused()) {
            narratorManager.narrate(EMPTY_SEARCH_TEXT);
        } else {
            narratorManager.narrate(Text.of(this.currentTab.name()));
        }
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    static {
        SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
        SELECTED_MUSIC_TAB_TITLE = MUSIC_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
        SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
        SEARCH_TEXT = Text.translatable("gui.musicnotification.playsound.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
        EMPTY_SEARCH_TEXT = Text.translatable("gui.musicnotification.playsound.search_empty").formatted(Formatting.GRAY);
    }

    enum Tab {
        HOME,
        MUSIC,
        HISTORY
    }
}
