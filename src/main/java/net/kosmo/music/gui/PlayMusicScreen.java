package net.kosmo.music.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kosmo.music.ClientMusic;
import net.kosmo.music.MusicController;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class PlayMusicScreen extends Screen {
    protected static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier("textures/gui/social_interactions.png");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.mn.tab_home");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.mn.tab_history");
    private static final Text SELECTED_ALL_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HIDDEN_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SEARCH_TEXT = Text.translatable("gui.socialInteractions.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private static final Text BLOCKING_TEXT = Text.translatable("gui.socialInteractions.blocking_hint");

    private TrackListWidget musicTrackListWidget;
    private TextFieldWidget searchBox;
    private String currentSearch = "";
    private Tab currentTab = Tab.HOME;
    private ButtonWidget homeTab;
    private ButtonWidget historyTab;

    private boolean initialized = false;


    private final MusicController musicController = ClientMusic.musicController;

    public PlayMusicScreen() {
        super(Text.translatable("kosmo.musicnotification.gui"));
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        if (this.initialized) {} else {
//            this.musicTrackListWidget = new TrackListWidget(this, this.client, this.width, this.getScreenHeight(), 80, this.height - 32, 24);
        }

        int i = this.musicTrackListWidget.getRowWidth() / 2;
        int j = this.musicTrackListWidget.getRowLeft();
        int k = this.musicTrackListWidget.getRowRight();
        int l = this.textRenderer.getWidth(BLOCKING_TEXT) + 40;
        this.homeTab = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions(j, 45, i, 20).build());
        this.historyTab = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(k - i + 1, 45, i, 20).build());

        String string = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.getSearchBoxX() + 29, 75, 198, 13, SEARCH_TEXT);
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(string);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(this::onSearchChange);

        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.musicTrackListWidget);

        this.setCurrentTab(this.currentTab);

        this.initialized = true;

    }

    private void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;

//        ClientMusic.LOGGER.info("Current tab: " + currentTab);

        switch (currentTab) {
            case HOME:
                this.homeTab.setMessage(SELECTED_ALL_TAB_TITLE);
                this.historyTab.setMessage(HISTORY_TAB_TITLE);

//                this.musicTrackListWidget.update(this.musicController.getDiscs());

                break;
            case HISTORY:
                this.homeTab.setMessage(HOME_TAB_TITLE);
                this.historyTab.setMessage(SELECTED_HIDDEN_TAB_TITLE);
                break;
        }
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
        context.drawCenteredTextWithShadow(client.textRenderer, Text.translatable("gui.mn.title"), this.width / 2, 35, -1);

        this.searchBox.render(context, mouseX, mouseY, delta);
        this.musicTrackListWidget.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && ClientMusic.keyBinding.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onSearchChange(String search) {
        if (!(search = search.toLowerCase(Locale.ROOT)).equals(this.currentSearch)) {
            this.currentSearch = search;
//            this.musicTrackListWidget.setCurrentSearch(search);
            this.setCurrentTab(this.currentTab);
        }
    }

    @Environment(value = EnvType.CLIENT)
    public static enum Tab {
        HOME, HISTORY;
    }
}
