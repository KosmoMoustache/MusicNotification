package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class MusicListEntry extends ListEntry {
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);
    private static final ButtonTextures PLAY_BUTTON_TEXTURE = new ButtonTextures(new Identifier("musicnotification", "jukebox/play_button"), new Identifier("musicnotification", "jukebox/play_button_disabled"), new Identifier("musicnotification", "jukebox/play_button_focused"));
    private static final ButtonTextures STOP_BUTTON_TEXTURE = new ButtonTextures(new Identifier("musicnotification", "jukebox/stop_button"), new Identifier("musicnotification", "jukebox/stop_button_focused"));
    public final ArrayList<ClickableWidget> buttons;
    public final MusicManager.Music entry;
    private final MinecraftClient client;
    private final PlaySoundListWidget parent;
    private final ButtonWidget playButton;
    private final ButtonWidget stopButton;

    public MusicListEntry(MinecraftClient client, PlaySoundListWidget parent, MusicManager.Music music) {
        this.client = client;
        this.parent = parent;
        this.entry = music;

        this.playButton = new TexturedButtonWidget(0, 0, 20, 20, PLAY_BUTTON_TEXTURE, button -> {
            this.onButtonClick(this.entry);
        }, Text.translatable("gui.musicnotification.jukebox.play_sound"));

        this.stopButton = new TexturedButtonWidget(0, 0, 20, 20, STOP_BUTTON_TEXTURE, button -> {
            this.onButtonClick(this.entry);
        }, Text.translatable("gui.musicnotification.jukebox.stop_sound"));

        this.buttons = new ArrayList<>();
        this.buttons.add(this.playButton);
        this.buttons.add(this.stopButton);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int y1 = y + 4;
        int y2 = y + (entryHeight / 2) + 3;
        int xMargeCover = x + 4 + 24 + 4;

        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);

        MutableText text = Text.literal(entry.getTitle()).append(" - ").withColor(Colors.WHITE).append(Text.literal(entry.getAuthor()).withColor(Colors.LIGHT_GRAY));
        ClientMusic.drawScrollableText(context, this.client.textRenderer, text, xMargeCover, xMargeCover, y1, this.playButton.getX() - 4, y1 + this.client.textRenderer.fontHeight, Colors.WHITE, true);
        context.drawText(this.client.textRenderer, entry.getAlbumName(), xMargeCover, y2, Colors.LIGHT_GRAY, false);

//        this.entry.albumCover.drawAlbumCover(context, x + 4, y + 4);
        this.entry.albumCover.drawAlbumCover(context, x + 4, y + (entryHeight - AlbumCover.getHeight()) / 2);

        boolean shouldRenderButton = this.parent.parent.currentTab != JukeboxScreen.Tab.HISTORY;

        this.playButton.setX(x + (entryWidth - this.playButton.getWidth()) - 8);
        this.playButton.setY(y + (entryHeight - this.playButton.getHeight()) / 2);
        this.playButton.active = ClientMusic.isVolumeZero() && shouldRenderButton && !isPlaying();
        if (!isPlaying() && shouldRenderButton) this.playButton.render(context, mouseX, mouseY, tickDelta);

        this.stopButton.setX(x + (entryWidth - this.stopButton.getWidth()) - 8);
        this.stopButton.setY(y + (entryHeight - this.stopButton.getHeight()) / 2);
        this.stopButton.active = isPlaying() && shouldRenderButton;
        if (isPlaying() && shouldRenderButton)
            this.stopButton.render(context, mouseX, mouseY, tickDelta);
    }

    private void onButtonClick(MusicManager.Music entry) {
        if (isPlaying()) {
            this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
            ClientMusic.currentlyPlaying = null;
        } else {
            ClientMusic.playAndResetTracker(client, entry);
        }
    }

    private boolean isPlaying() {
        if (ClientMusic.currentlyPlaying != null) {
            MusicManager.Music e = ClientMusic.musicManager.get(this.entry.identifier);
            if (e == null) return false;
            return  e.customId == null ? e.identifier == ClientMusic.currentlyPlaying.getId() : e.customId == ClientMusic.currentlyPlaying.getId();
        }
        return false;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    @Override
    public List<? extends Element> children() {
        return this.buttons;
    }
}
