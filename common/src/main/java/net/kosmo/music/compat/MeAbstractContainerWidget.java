package net.kosmo.music.compat;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.network.chat.Component;
//? if >=26.2 {
//? } else {
/*import net.minecraft.client.gui.components.AbstractScrollArea;
*///? }

public abstract class MeAbstractContainerWidget extends AbstractContainerWidget {
	public MeAbstractContainerWidget(int x, int y, int width, int height, Component message) {
		//? if >=26.2 {
		super(x, y, width, height, message, ScrollbarSettings.NO_SCROLL);
		//? } else {
		/*super(x, y, width, height, message, AbstractScrollArea.defaultSettings(0));
		*///? }
	}
}
