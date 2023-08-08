package net.kosmo.nowplaying.gui;

import net.kosmo.nowplaying.NowPlaying;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundListEntry extends ElementListWidget.Entry<SoundListEntry> {
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);

    private final MinecraftClient client;
    private final PlaySoundScreen parent;
    private final Identifier identifier;

    private final TexturedButtonWidget playButton;
    private final TexturedButtonWidget stopButton;
    private final List<ClickableWidget> buttons;


    public SoundListEntry(MinecraftClient client, PlaySoundScreen parent, Identifier identifier) {
        this.client = client;
        this.parent = parent;
        this.identifier = identifier;

        this.playButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 38, 20, PlaySoundScreen.TEXTURE, 256, 256, button -> {
            this.onButtonClick();
        }, Text.translatable("gui.nowplaying.playsound.play", NowPlaying.nowPlaying));
        this.stopButton = new TexturedButtonWidget(0, 0, 20, 20, 20, 38, 20, PlaySoundScreen.TEXTURE, 256, 256, button -> {
            this.onButtonClick();
        }, Text.translatable("gui.nowplaying.playsound.stop"));
        this.buttons = new ArrayList<ClickableWidget>();
        this.buttons.add(this.playButton);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int l;
        int i = x + 4;
        int j = y + (entryHeight - 24) / 2;
        int k = i + 24 + 4;
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        l = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
        context.drawText(this.client.textRenderer, this.identifier.toString(), k, l, WHITE_COLOR, false);
        this.stopButton.setX(x + (entryWidth - this.stopButton.getWidth() - 4) - 20 - 4);
        this.stopButton.setY(y + (entryHeight - this.stopButton.getHeight()) / 2);
        this.playButton.setX(x + (entryWidth - this.playButton.getWidth() - 4) - 20 - 4);
        this.playButton.setY(y + (entryHeight - this.playButton.getHeight()) / 2);

        if (NowPlaying.nowPlaying != null && NowPlaying.nowPlaying.getId() == this.identifier) {
            this.stopButton.render(context, mouseX, mouseY, tickDelta);
        } else {
            this.playButton.render(context, mouseX, mouseY, tickDelta);
        }

        float u = 0f;
        float v = 78f;
        if (this.identifier.getNamespace().contains("nowplaying")) u = 0f;

        if (this.identifier.getNamespace().contains("minecraft")) u = 20f;

        if (this.identifier.getPath().contains("music_disc")) u = 40f;

        context.drawTexture(PlaySoundScreen.TEXTURE, x + 20 / 2, this.playButton.getY(), u, v, 20, 20, 256, 256);
    }

    private void onButtonClick() {
        PositionedSoundInstance soundInstance = PositionedSoundInstance.music(SoundEvent.of(this.identifier));
        this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        this.client.getSoundManager().stop(NowPlaying.nowPlaying);
        this.client.getSoundManager().play(soundInstance);
        NowPlaying.nowPlaying = soundInstance;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    @Override
    public List<? extends Element> children() {
        return this.buttons;
    }

    public String getName() {
        return this.identifier.toString();
    }
}
