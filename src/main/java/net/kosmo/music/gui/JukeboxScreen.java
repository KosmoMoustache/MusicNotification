package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;


public class JukeboxScreen extends Screen {
    public static final Identifier JUKEBOX_ICON_TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/jukebox_icon_button.png");
    public static final Identifier JUKEBOX_PLAY_TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/jukebox_play_button.png");
    public static final Identifier JUKEBOX_STOP_TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/jukebox_stop_button.png");
    public static final Identifier JUKEBOX_TEXTURE = new Identifier(ClientMusic.MOD_ID, "textures/gui/jukebox.png");

    private static final Text TITLE = Text.translatable("gui.musicnotification.jukebox.title");
    private static final Text HOME_TAB_TITLE = Text.translatable("gui.musicnotification.jukebox.tab_home");
    private static final Text SOUND_TAB_TITLE = Text.translatable("gui.musicnotification.jukebox.tab_sound");
    private static final Text HISTORY_TAB_TITLE = Text.translatable("gui.musicnotification.jukebox.tab_history");
    private static final Text SELECTED_HOME_TAB_TITLE = HOME_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_SOUND_TAB_TITLE = SOUND_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HISTORY_TAB_TITLE = HISTORY_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);

    private static final Text STOP_SOUND_BUTTON = Text.translatable("gui.musicnotification.jukebox.stop_sound_button");
    private static final Text MASTER_VOLUME_ZERO = Text.translatable("gui.musicnotification.jukebox.master_volume_zero");
    private static final Text MUSIC_VOLUME_ZERO = Text.translatable("gui.musicnotification.jukebox.music_volume_zero");
    private static final Text CLEAR_HISTORY = Text.translatable("gui.musicnotification.jukebox.clear_history");

    private static final Text SEARCH_TEXT = Text.translatable("gui.musicnotification.jukebox.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private static final Text EMPTY_SEARCH_TEXT = Text.translatable("gui.musicnotification.jukebox.search_empty").formatted(Formatting.GRAY);
    private static final Text EMPTY_HISTORY_TEXT = Text.translatable("gui.musicnotification.jukebox.history_empty").formatted(Formatting.GRAY);

    private final MutableText header;
    public final Screen parent;
    public Tab currentTab;
    private TextFieldWidget searchBox;
    private PlaySoundListWidget soundList;
    private ButtonWidget homeTabButton;
    private ButtonWidget historyTabButton;
    private ButtonWidget soundTabButton;
    private ButtonWidget stopSoundButton;
    private ButtonWidget clearHistoryButton;
    private String currentSearch;
    private boolean initialized;

    public JukeboxScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.header = this.title.copy();
        this.currentTab = Tab.HOME;
        this.currentSearch = "";
    }

    protected void init() {
        if (this.initialized) {
            this.soundList.updateSize(this.width, this.getSoundListBottom() - 88, 0, 88);
        } else {
            this.soundList = new PlaySoundListWidget(this, this.client, this.width, this.height, 88, this.getSoundListBottom() /* - 88*/, 36);
        }

        int middle = this.soundList.getRowWidth() / 2;
        int rowLeft = this.soundList.getRowLeft();
        int rowRigth = this.soundList.getRowRight();

        if (ClientMusic.config.JUKEBOX_CONFIG.DEBUG_MOD) middle = this.soundList.getRowWidth() / 3;

        this.homeTabButton = this.addDrawableChild(ButtonWidget.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).dimensions(rowLeft, 43, middle, 20).build());
        this.historyTabButton = this.addDrawableChild(ButtonWidget.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).dimensions(rowLeft + middle + 1, 43, middle, 20).build());
        this.soundTabButton = ButtonWidget.builder(SOUND_TAB_TITLE, button -> this.setCurrentTab(Tab.SOUND)).dimensions(rowRigth - middle + 1, 43, middle, 20).build();
        if (ClientMusic.config.JUKEBOX_CONFIG.DEBUG_MOD) {
            this.addDrawableChild(this.soundTabButton);
        }

        this.stopSoundButton = this.addDrawableChild(ButtonWidget.builder(STOP_SOUND_BUTTON, button -> this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC)).dimensions(this.soundList.getRowLeft() + 5, this.getSoundListBottom() + 10, this.soundList.getRowWidth() - 10, 20).build());
        this.clearHistoryButton = this.addDrawableChild(ButtonWidget.builder(CLEAR_HISTORY, button -> ClientMusic.musicHistory.clear()).dimensions(10, 10, this.textRenderer.getWidth(CLEAR_HISTORY) + 8, 20).build());
        this.clearHistoryButton.visible = false;

        String string = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.getSearchBoxX() + 28, 74, 200, 15, SEARCH_TEXT) {
            @Override
            protected MutableText getNarrationMessage() {
                if (!JukeboxScreen.this.searchBox.getText().isEmpty() && JukeboxScreen.this.soundList.isEmpty()) {
                    return super.getNarrationMessage().append(", ").append(EMPTY_SEARCH_TEXT);
                }
                return super.getNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(255);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(string);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(this::onSearchChange);

        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.soundList);
        this.initialized = true;
        this.setCurrentTab(this.currentTab);
    }

    @Override
    public void renderBackground(DrawContext context) {
        int i = this.getSearchBoxX() + 3;
        super.renderBackground(context);
        context.drawNineSlicedTexture(JUKEBOX_TEXTURE, i, 64, 236, this.getScreenHeight() + 16, 8, 236, 34, 1, 1);
        context.drawTexture(JUKEBOX_TEXTURE, i + 10, 76, 243, 1, 12, 12);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.drawTextWithShadow(this.textRenderer, this.header, this.getSearchBoxX() + 8, 30, -1);

        if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER) == 0f) {
            this.stopSoundButton.setMessage(MASTER_VOLUME_ZERO);
        } else if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MUSIC) == 0f) {
            this.stopSoundButton.setMessage(MUSIC_VOLUME_ZERO);
        }

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        } else if (!this.searchBox.getText().isEmpty()) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getSoundListBottom()) / 2, Colors.WHITE);
        } else if (this.currentTab == Tab.HISTORY) {
            context.drawCenteredTextWithShadow(this.client.textRenderer, EMPTY_HISTORY_TEXT, this.width / 2, (72 + this.getSoundListBottom()) / 2, Colors.WHITE);
        }

        this.searchBox.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.searchBox.setCursorToEnd();
        this.searchBox.setSelectionEnd(0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void onSearchChange(String currentSearch) {
        if (!(currentSearch = currentSearch.toLowerCase(Locale.ROOT)).equals(this.currentSearch)) {
            this.soundList.setCurrentSearch(currentSearch);
            this.currentSearch = currentSearch;
            this.setCurrentTab(this.currentTab);
        }
    }

    private void setCurrentTab(Tab currentTab) {
        if (this.currentTab != currentTab) {
            this.searchBox.setText("");
        }

        this.currentTab = currentTab;
        this.homeTabButton.setMessage(HOME_TAB_TITLE);
        this.historyTabButton.setMessage(HISTORY_TAB_TITLE);
        this.soundTabButton.setMessage(SOUND_TAB_TITLE);
        this.clearHistoryButton.visible = false;

        boolean listEmpty = false;
        switch (currentTab) {
            case HOME: {
                this.homeTabButton.setMessage(SELECTED_HOME_TAB_TITLE);
                header.append(" - ").append(HOME_TAB_TITLE);
                Collection<MusicManager.Music> col = ClientMusic.musicManager.musics.values();
                listEmpty = col.isEmpty();
                this.soundList.update(col, this.soundList.getScrollAmount());
                break;
            }
            case SOUND: {
                this.soundTabButton.setMessage(SELECTED_SOUND_TAB_TITLE);
                header.append(" - ").append(SOUND_TAB_TITLE);
                Collection<MusicManager.Sound> col = Lists.newArrayList();
                Registries.SOUND_EVENT.getIds().forEach(id -> col.add(new MusicManager.Sound(id)));
                listEmpty = col.isEmpty();
                this.soundList.update(col, this.soundList.getScrollAmount());
                break;
            }
            case HISTORY: {
                this.historyTabButton.setMessage(SELECTED_HISTORY_TAB_TITLE);
                this.clearHistoryButton.visible = true;
                header.append(" - ").append(HISTORY_TAB_TITLE);
                Collection<MusicManager.Music> col = ClientMusic.musicHistory.getHistory();
                listEmpty = col.isEmpty();
                this.soundList.update(col, this.soundList.getScrollAmount());
            }
        }

        NarratorManager narratorManager = this.client.getNarratorManager();
        if (!this.searchBox.getText().isEmpty() && this.soundList.isEmpty() && !this.searchBox.isFocused()) {
            narratorManager.narrate(EMPTY_SEARCH_TEXT);
        } else if (listEmpty) {
            if (currentTab == Tab.HOME || currentTab == Tab.SOUND) {
                narratorManager.narrate(EMPTY_SEARCH_TEXT);
            } else if (currentTab == Tab.HISTORY) {
                narratorManager.narrate(EMPTY_HISTORY_TEXT);
            }
        }
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int getSoundListBottom() {
        return 80 + this.getScreenHeight() - 8;
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    public enum Tab {
        HOME,
        SOUND,
        HISTORY
    }
}
