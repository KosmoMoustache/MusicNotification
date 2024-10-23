package net.kosmo.music.impl.gui;

import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.MusicManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Locale;


public class JukeboxScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "jukebox/background");
    private static final ResourceLocation SEARCH_ICON_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "icon/search");

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

    private final MutableComponent header;
    private final Screen parent;
    public Tab currentTab;
    private EditBox searchBox;
    private PlaySoundListWidget soundList;
    private Button homeTabButton;
    private Button historyTabButton;
    private Button soundTabButton;
    private Button stopSoundButton;
    private Button clearHistoryButton;
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
            this.soundList.setRectangle(this.width, this.getSoundListBottom() - 88, 0, 88);
        } else {
            this.soundList = new PlaySoundListWidget(this, this.minecraft, this.width, this.getSoundListBottom() - 88, 88, 36);
        }

        int middle = this.soundList.getRowWidth() / 2;
        int rowLeft = this.soundList.getRowLeft();
        int rowRigth = this.soundList.getRowRight();

        if (ClientMusic.config.DEBUG_MOD) middle = this.soundList.getRowWidth() / 3;

        this.homeTabButton = this.addRenderableWidget(Button.builder(HOME_TAB_TITLE, button -> this.setCurrentTab(Tab.HOME)).bounds(rowLeft, 43, middle, 20).build());
        this.historyTabButton = this.addRenderableWidget(Button.builder(HISTORY_TAB_TITLE, button -> this.setCurrentTab(Tab.HISTORY)).bounds(rowLeft + middle + 1, 43, middle, 20).build());
        this.soundTabButton = Button.builder(SOUND_TAB_TITLE, button -> this.setCurrentTab(Tab.SOUND)).bounds(rowRigth - middle + 1, 43, middle, 20).build();
        if (ClientMusic.config.DEBUG_MOD) {
            this.addRenderableWidget(this.soundTabButton);
        }

        this.stopSoundButton = this.addRenderableWidget(Button.builder(STOP_SOUND_BUTTON, button -> {
            this.minecraft.getSoundManager().stop(null, SoundSource.MUSIC);
            ClientMusic.currentlyPlaying = null;
            this.setCurrentTab(this.currentTab);
        }).bounds(this.soundList.getRowLeft(), this.getSoundListBottom() + 10, this.soundList.getRowRight() - this.soundList.getRowLeft() - 1 - 50, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> {
            this.onClose();
        }).bounds(this.soundList.getRowRight() - 50, this.getSoundListBottom() + 10, 50, 20).build());

        this.clearHistoryButton = this.addRenderableWidget(Button.builder(CLEAR_HISTORY, button -> {
            ClientMusic.musicHistory.clear();
            this.setCurrentTab(Tab.HISTORY);
        }).bounds(10, 10, this.font.width(CLEAR_HISTORY) + 8, 20).build());
        this.clearHistoryButton.visible = false;

        String string = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.getSearchBoxX() + 28, 74, 200, 15, SEARCH_TEXT) {
            @Override
            protected MutableComponent createNarrationMessage() {
                if (!JukeboxScreen.this.searchBox.getValue().isEmpty() && JukeboxScreen.this.soundList.isEmpty()) {
                    return super.createNarrationMessage().append(", ").append(EMPTY_SEARCH_TEXT);
                }
                return super.createNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(255);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(0xFFFFFF);
        this.searchBox.setValue(string);
        this.searchBox.setHint(SEARCH_TEXT);
        this.searchBox.setResponder(this::onSearchChange);

        this.addWidget(this.searchBox);
        this.addWidget(this.soundList);
        this.initialized = true;
        this.setCurrentTab(this.currentTab);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        int i = this.getSearchBoxX() + 3;
        context.blitSprite(RenderType::guiTextured, BACKGROUND_TEXTURE, i, 64, 236, this.getScreenHeight() + 16);
        context.blitSprite(RenderType::guiTextured, SEARCH_ICON_TEXTURE, i + 10, 76, 12, 12);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawString(this.font, this.header, this.getSearchBoxX() + 8, 30, -1);

        if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) == 0f) {
            this.stopSoundButton.setMessage(MASTER_VOLUME_ZERO);
        } else if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) == 0f) {
            this.stopSoundButton.setMessage(MUSIC_VOLUME_ZERO);
        }

        if (!this.soundList.isEmpty()) {
            this.soundList.render(context, mouseX, mouseY, delta);
        } else if (!this.searchBox.getValue().isEmpty()) {
            context.drawCenteredString(this.minecraft.font, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getSoundListBottom()) / 2, CommonColors.WHITE);
        } else if (this.currentTab == Tab.HISTORY) {
            context.drawCenteredString(this.minecraft.font, EMPTY_HISTORY_TEXT, this.width / 2, (72 + this.getSoundListBottom()) / 2, CommonColors.WHITE);
        }

        this.searchBox.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.searchBox.moveCursorToEnd(false);
        this.searchBox.setHighlightPos(0);
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
            this.searchBox.setValue("");
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
                BuiltInRegistries.SOUND_EVENT.keySet().forEach(id -> col.add(new MusicManager.Sound(id)));
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

        GameNarrator narratorManager = this.minecraft.getNarrator();
        if (!this.searchBox.getValue().isEmpty() && this.soundList.isEmpty() && !this.searchBox.isFocused()) {
            narratorManager.sayNow(EMPTY_SEARCH_TEXT);
        } else if (listEmpty) {
            if (currentTab == Tab.HOME || currentTab == Tab.SOUND) {
                narratorManager.sayNow(EMPTY_SEARCH_TEXT);
            } else { // currentTab is Tab.HISTORY
                narratorManager.sayNow(EMPTY_HISTORY_TEXT);
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
