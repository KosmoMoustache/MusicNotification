package net.kosmo.music.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;

public class TrackListWidget extends ElementListWidget<TrackListEntry> {

    public TrackListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
    }
}
