package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.RenderHelper;
import net.kosmo.music.utils.resource.AlbumCover;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.List;

public class MusicListEntry extends ListEntry {
    public static final int GRAY_COLOR = FastColor.ARGB32.color(255, 74, 74, 74);
    private static final WidgetSprites PLAY_BUTTON_TEXTURE = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_disabled"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_focused"));
    private static final WidgetSprites STOP_BUTTON_TEXTURE = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/stop_button"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/stop_button_focused"));
    public final ArrayList<AbstractWidget> buttons;
    public final MusicManager.Music entry;
    private final Minecraft client;
    private final PlaySoundListWidget parent;
    private final Button playButton;
    private final Button stopButton;

    public MusicListEntry(Minecraft client, PlaySoundListWidget parent, MusicManager.Music music) {
        this.client = client;
        this.parent = parent;
        this.entry = music;

        this.playButton = new ImageButton(0, 0, 20, 20, PLAY_BUTTON_TEXTURE, button -> {
            this.onButtonClick(this.entry);
        }, Component.translatable("gui.musicnotification.jukebox.play_sound"));

        this.stopButton = new ImageButton(0, 0, 20, 20, STOP_BUTTON_TEXTURE, button -> {
            this.onButtonClick(this.entry);
        }, Component.translatable("gui.musicnotification.jukebox.stop_sound"));

        this.buttons = new ArrayList<>();
        this.buttons.add(this.playButton);
        this.buttons.add(this.stopButton);
    }

    @Override
    public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int y1 = y + 4;
        int y2 = y + (entryHeight / 2) + 3;
        int xMargeCover = x + 4 + 24 + 4;

        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);

        MutableComponent text = Component.literal(entry.getTitle()).append(" - ").withColor(CommonColors.WHITE).append(Component.literal(entry.getAuthor()).withColor(CommonColors.LIGHT_GRAY));
        RenderHelper.drawScrollableText(context, this.client.font, text, xMargeCover, xMargeCover, y1, this.playButton.getX() - 4, y1 + this.client.font.lineHeight, CommonColors.WHITE, true);
        context.drawString(this.client.font, entry.getAlbumName(), xMargeCover, y2, CommonColors.LIGHT_GRAY, false);

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
            this.client.getSoundManager().stop(null, SoundSource.MUSIC);
            ClientMusic.currentlyPlaying = null;
        } else {
            ClientMusic.playAndResetTracker(client, entry);
        }
    }

    private boolean isPlaying() {
        if (ClientMusic.currentlyPlaying != null) {
            MusicManager.Music e = ClientMusic.musicManager.get(this.entry.identifier);
            if (e == null) return false;
            return  e.customId == null ? e.identifier == ClientMusic.currentlyPlaying.getLocation() : e.customId == ClientMusic.currentlyPlaying.getLocation();
        }
        return false;
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return this.buttons;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.buttons;
    }
}
