package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.music.MusicEntry;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class SoundListEntry extends ElementListWidget.Entry<SoundListEntry> {
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);
    public static final int DARK_LIGHT_GRAY_COLOR = ColorHelper.Argb.getArgb(155, 255, 255, 255);
    public static final int LIGHT_GRAY_COLOR = ColorHelper.Argb.getArgb(140, 255, 255, 255);
    private final MinecraftClient client;
    private final MusicEntry entry;
    private final TexturedButtonWidget playButton;
    private final TexturedButtonWidget stopButton;
    private final List<ClickableWidget> buttons;
    private final SoundListWidget parent;
    private int rotation;

    public SoundListEntry(MinecraftClient client, SoundListWidget parent, MusicEntry entry) {
        this.client = client;
        this.parent = parent;
        this.entry = entry;

        this.playButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 38, 20, PlaySoundScreen.TEXTURE, 256, 256, button -> {
            this.onButtonClick();
        }, Text.translatable("gui.nowplaying.playsound.play")) {
            @Override
            protected MutableText getNarrationMessage() {
                return SoundListEntry.this.getNarrationMessage(super.getNarrationMessage());
            }
        };
        this.stopButton = new TexturedButtonWidget(0, 0, 20, 20, 20, 38, 20, PlaySoundScreen.TEXTURE, 256, 256, button -> {
            this.onButtonClick();
        }, Text.translatable("gui.nowplaying.playsound.stop"));
        this.buttons = new ArrayList<>();
        this.buttons.add(this.playButton);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int l;
        int i = x + 4;
        int k = i + 24 + 4;

        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        if (hovered) {
            context.drawBorder(x, y, entryWidth - 1, entryHeight, LIGHT_GRAY_COLOR);
        }

        int fontHeight = this.client.textRenderer.fontHeight;
        l = y + (entryHeight - (fontHeight + fontHeight)) / 2;

        context.drawText(this.client.textRenderer, String.format("%s - %s", this.entry.title, this.entry.author), k, l, WHITE_COLOR, false);
        context.drawText(this.client.textRenderer, this.entry.soundtrack, k, l + 12, DARK_LIGHT_GRAY_COLOR, false);

        this.stopButton.setPosition(x + (entryWidth - this.stopButton.getWidth() - 4) - 10 - 4, y + (entryHeight - this.stopButton.getHeight()) / 2);
        this.playButton.setPosition(x + (entryWidth - this.playButton.getWidth() - 4) - 10 - 4, y + (entryHeight - this.playButton.getHeight()) / 2);

        if (NowPlaying.tracker.getNowPlaying().isPlaying() && NowPlaying.tracker.getNowPlaying().getSound().getId() == this.entry.identifier) {
            this.stopButton.render(context, mouseX, mouseY, tickDelta);
        } else {
            this.playButton.render(context, mouseX, mouseY, tickDelta);
        }

        // TODO: icon rotation when playing
        this.entry.albumCover.drawIcon(context, x + 4, y + 4);
    }

    private void onButtonClick() {
        PositionedSoundInstance soundInstance = PositionedSoundInstance.music(SoundEvent.of(this.entry.identifier));
        NowPlaying.LOGGER.info(soundInstance.toString());
        this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        this.client.getSoundManager().stop(NowPlaying.tracker.getNowPlaying().getSound());
        this.client.getSoundManager().play(soundInstance);
        NowPlaying.tracker.getNowPlaying().setSound(soundInstance);
    }

    @Override
    public List<ClickableWidget> selectableChildren() {
        return this.buttons;
    }

    @Override
    public List<ClickableWidget> children() {
        return this.buttons;
    }

    public String getIdentifier() {
        return this.entry.identifier.toString();
    }

    MutableText getNarrationMessage(MutableText text) {
        return Text.literal(this.entry.title).append(", ").append(this.entry.author).append(", ").append(text);
    }
}
