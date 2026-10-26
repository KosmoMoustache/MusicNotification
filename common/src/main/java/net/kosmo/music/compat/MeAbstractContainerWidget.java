package net.kosmo.music.compat;

import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.network.chat.Component;
//? if >=26.2 {
//? } elif >=26.1 {
/*import net.minecraft.client.gui.components.AbstractScrollArea;
 *///? }

public abstract class MeAbstractContainerWidget extends AbstractContainerWidget {
	public MeAbstractContainerWidget(int x, int y, int width, int height, Component message) {
		//? if >=26.2 {
		super(x, y, width, height, message, ScrollbarSettings.NO_SCROLL);
		//? } elif >=26.1 {
		/*super(x, y, width, height, message, AbstractScrollArea.defaultSettings(0));
		 *///? } else {
		/*super(x, y, width, height, message);
		 *///? }
	}

	//? if >1.21.1 && <=1.21.11 {
	/*@Override
	protected double scrollRate() {
		return 0;
	}
	*///? }
}
