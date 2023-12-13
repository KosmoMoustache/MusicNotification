package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.mixin.IMixinMusicTracker;
import net.kosmo.nowplaying.music.MusicEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaySoundScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(NowPlaying.MOD_ID, "play_sound/background");
    private static final Identifier SEARCH_ICON_TEXTURE = new Identifier("icon/search");

    private static final Text TEXT_STOP_SOUND = Text.translatable("gui.nowplaying.playsound.stop_sound");
    private static final Text TEXT_RESET_MUSIC_TRACKER = Text.translatable("gui.nowplaying.playsound.reset_music_tracker");

    TextFieldWidget searchBox;

    private static final Text SEARCH_TEXT = Text.translatable("gui.nowplaying.playsound.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);

    private SoundListWidget soundList;
    private Tab currentTab = Tab.HOME;
    private String currentSearch = "";
    boolean initialized = false;


    public PlaySoundScreen() {
        super(Text.translatable("gui.nowplaying.playsound.title"));
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.soundList.setDimensionsAndPosition(this.width, this.height, 80, this.getEntryListBottom());
        } else {
            this.soundList = new SoundListWidget(this.client, this.width, this.height, 88, this.getEntryListBottom());
        }

//        int i = this.soundList.getRowWidth() / 2;
//        int j = this.soundList.getRowLeft();
//        int k = this.soundList.getRowRight();
        int l = this.textRenderer.getWidth(TEXT_RESET_MUSIC_TRACKER) + 40;
        int l1 = this.textRenderer.getWidth(TEXT_RESET_MUSIC_TRACKER) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3 - l/2 - 1;
        int n1 = (this.width - l1) / 2 + 3 + l1/2 + 1;

        this.addDrawableChild(ButtonWidget.builder(TEXT_STOP_SOUND, (button) -> {
            this.client.getSoundManager().stopAll();
        }).dimensions(n, m, l, 20).build());

        this.addDrawableChild(ButtonWidget.builder(TEXT_RESET_MUSIC_TRACKER, (button) -> {
            ((IMixinMusicTracker) this.client.getMusicTracker()).setTimeUntilNextSong(0);
        }).dimensions(n1, m, l1, 20).build());

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

    private void setCurrentTab(Tab tab) {
        this.currentTab = tab;
        this.soundList.update(new ArrayList<>(NowPlaying.musicManager.getEntriesValue()));
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    enum Tab {
        HOME,
        HISTORY
    }

}
