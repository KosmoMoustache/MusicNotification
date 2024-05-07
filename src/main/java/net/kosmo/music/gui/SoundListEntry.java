package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundListEntry extends ListEntry {
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);

    public final MinecraftClient client;
    public final PlaySoundListWidget parent;
    public final MusicManager.Sound entry;
    private final ArrayList<ClickableWidget> buttons;
    private final ButtonWidget playButton;

    public SoundListEntry(MinecraftClient client, PlaySoundListWidget parent, MusicManager.Sound sound) {
        this.client = client;
        this.parent = parent;
        this.entry = sound;

        this.playButton = new TexturedButtonWidget(0, 0, 20, 20, 0 ,0, 20, JukeboxScreen.JUKEBOX_PLAY_TEXTURE, 64, 64, button -> this.onButtonClick(this.entry), Text.translatable("gui.musicnotification.jukebox.play_sound"));

        this.buttons = new ArrayList<>();
        this.buttons.add(this.playButton);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);

        this.playButton.setX(x + (entryWidth - this.playButton.getWidth() - 4) - 4);
        this.playButton.setY(y + (entryHeight - this.playButton.getHeight()) / 2);
        this.playButton.active = ClientMusic.isVolumeZero();
        this.playButton.render(context, mouseX, mouseY, tickDelta);

        int i = x + 4;
        int l = y + (entryHeight - this.client.textRenderer.fontHeight) / 2;
        ClientMusic.drawScrollableText(context, this.client.textRenderer, Text.literal(entry.identifier.toString()), i, i, l, this.playButton.getX() - 4, l + this.client.textRenderer.fontHeight, Colors.WHITE, true);
    }

    private void onButtonClick(MusicManager.Sound entry) {
        SoundEvent soundEvent = entry.getSoundEvent(this.client.getSoundManager());
        if (soundEvent == null) {
            ClientMusic.LOGGER.warn("Unable to play unknown sound with id: {}", entry.identifier);
        } else {
            PositionedSoundInstance soundInstance = PositionedSoundInstance.music(soundEvent);
            this.client.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
            this.client.getSoundManager().play(soundInstance);
        }

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
