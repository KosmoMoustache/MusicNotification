package net.kosmo.music.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.Layout;
//? if <=26.1 {
/*import net.kosmo.music.mixin.compat.ScrollableLayoutAccessor;
*///? }

public class MeScrollableLayout extends ScrollableLayout {
	public MeScrollableLayout(Minecraft minecraft, Layout content, int maxHeight) {
		super(minecraft, content, maxHeight/*? >=26.2 {*/,ScrollableLayout.ReserveStrategy.RIGHT/*?} else {*//*?}*/);
	}

	//? if <=26.1 {
	/*public void setScrollbarSpacing(int scrollbarSpacing) {
		((ScrollableLayoutAccessor) (Object) this)
			.musicnotification$setScrollbarSpacing(scrollbarSpacing);
	}
	*///?}
}
