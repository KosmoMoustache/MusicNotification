package net.kosmo.music.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public abstract class ListEntry extends ContainerObjectSelectionList.Entry<ListEntry> {
	protected Minecraft client;
	protected JukeboxEntryList parent;
}
