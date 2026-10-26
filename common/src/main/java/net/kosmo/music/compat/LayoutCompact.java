package net.kosmo.music.compat;

import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;

public class LayoutCompact {
	/**
	 * removeChildren is absent in 26.1 and earlier, this is a workaround
	 */
	public static void removeChildren(LinearLayout layout) {
		removeChildrenImpl(layout);
	}

	private static <T extends Layout> void removeChildrenImpl(T layout) {
		//? if >=26.2 {
		layout.removeChildren();
		//? } else {
		/*if (layout instanceof ILayoutCompat compat) {
			compat.musicnotification$removeChildren();
		}
		*///? }
	}
}
