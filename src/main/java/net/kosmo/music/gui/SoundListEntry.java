package net.kosmo.music.gui;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FastColor;
import java.util.ArrayList;
import java.util.List;

public class SoundListEntry extends ListEntry {
    private static final WidgetSprites PLAY_BUTTON_TEXTURE = new WidgetSprites(ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_disabled"), ResourceLocation.fromNamespaceAndPath("musicnotification", "jukebox/play_button_focused"));
    public static final int GRAY_COLOR = FastColor.ARGB32.color(255, 74, 74, 74);

    public final Minecraft client;
    public final PlaySoundListWidget parent;
    public final MusicManager.Sound entry;
    private final ArrayList<AbstractWidget> buttons;
    private final Button playButton;

    public SoundListEntry(Minecraft client, PlaySoundListWidget parent, MusicManager.Sound sound) {
        this.client = client;
        this.parent = parent;
        this.entry = sound;

        this.playButton = new ImageButton(0, 0, 20, 20, PLAY_BUTTON_TEXTURE, button -> {
            this.onButtonClick(this.entry);
        }, Component.translatable("gui.musicnotification.jukebox.play_sound"));

        this.buttons = new ArrayList<>();
        this.buttons.add(this.playButton);
    }

    @Override
    public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);

        this.playButton.setX(x + (entryWidth - this.playButton.getWidth() - 4) - 4);
        this.playButton.setY(y + (entryHeight - this.playButton.getHeight()) / 2);
        this.playButton.active = ClientMusic.isVolumeZero();
        this.playButton.render(context, mouseX, mouseY, tickDelta);

        int i = x + 4;
        int l = y + (entryHeight - this.client.font.lineHeight) / 2;
        ClientMusic.drawScrollableText(context, this.client.font, Component.literal(entry.identifier.toString()), i, i, l, this.playButton.getX() - 4, l + this.client.font.lineHeight, CommonColors.WHITE, true);
    }

    private void onButtonClick(MusicManager.Sound entry) {
        SoundEvent soundEvent = entry.getSoundEvent(this.client.getSoundManager());
        if (soundEvent == null) {
            ClientMusic.LOGGER.warn("Unable to play unknown sound with id: {}", entry.identifier);
        } else {
            SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent);
            this.client.getSoundManager().stop(null, SoundSource.MUSIC);
            this.client.getSoundManager().play(soundInstance);
        }

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
