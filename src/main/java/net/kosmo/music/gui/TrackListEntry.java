package net.kosmo.music.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class TrackListEntry extends ElementListWidget.Entry<TrackListEntry> {
    public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);
    public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);

    MinecraftClient client;
    PlayMusicScreen parent;
    SoundEvent soundEvent;

    TrackListEntry(MinecraftClient client, PlayMusicScreen parent, SoundEvent soundEvent){
        this.client = client;
        this.parent = parent;
        this.soundEvent = soundEvent;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        int l;
        int i = x + 4;
        int k = i + 24 + 4;
        l = y + (entryHeight - (this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight)) / 2;
        context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
        context.drawText(this.client.textRenderer, "TEST", k, l, WHITE_COLOR, false);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {return null;    }

    @Override
    public List<? extends Element> children() {return null;
    }
}
