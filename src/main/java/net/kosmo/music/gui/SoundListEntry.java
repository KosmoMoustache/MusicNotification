package net.kosmo.music.gui;

import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import net.kosmo.music.ClientMusic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoundListEntry extends ElementListWidget.Entry<SoundListEntry> {
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);

    private final MinecraftClient client;
    private final PlaySoundScreen parent;
    private final Identifier identifier;

    private final TexturedButtonWidget playButton;
    private final List<ClickableWidget> buttons;


    public SoundListEntry(MinecraftClient client, PlaySoundScreen parent, Identifier identifier) {
        this.client = client;
        this.parent = parent;
        this.identifier = identifier;

        this.playButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 38, 20, PlaySoundScreen.SOCIAL_INTERACTIONS_TEXTURE, 256, 256, button -> {
            this.onButtonClick(identifier);
        }, Text.translatable("gui.socialInteractions.hide"));

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
        this.playButton.setX(x + (entryWidth - this.playButton.getWidth() - 4) - 20 - 4);
        this.playButton.setY(y + (entryHeight - this.playButton.getHeight()) / 2);
        this.playButton.render(context, mouseX, mouseY, tickDelta);
        context.drawTexture(ClickableWidget.WIDGETS_TEXTURE, this.playButton.getX() + 5, this.playButton.getY() + 1, 182.0f, 24.0f, 15, 15, 256, 256);

    }

    private void onButtonClick(Identifier identifier) {
        PositionedSoundInstance soundInstance = PositionedSoundInstance.music(SoundEvent.of(identifier));
        this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        this.client.getSoundManager().stop(ClientMusic.nowPlaying);
        this.client.getSoundManager().play(soundInstance);
        ClientMusic.nowPlaying = soundInstance;
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
