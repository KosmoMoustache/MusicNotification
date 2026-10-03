package net.kosmo.music.gui.v2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else {
/*import net.minecraft.client.gui.GuiGraphics;
*///? }

/**
 * A selection list that replaces the vanilla scrollbar with a thin gray bar.
 */
public abstract class ScrollbarlessList<E extends ContainerObjectSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
	private static final int BAR_WIDTH = 3;
	private static final int BAR_COLOR = 0xFF888888;

	public ScrollbarlessList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}

	//? if >=26.1 {
	@Override
	protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		if (this.scrollable()) {
			int x = this.scrollBarX() + (this.scrollbarWidth() - BAR_WIDTH) / 2;
			int y = this.scrollBarY();
			graphics.fill(x, y, x + BAR_WIDTH, y + this.scrollerHeight(), BAR_COLOR);
		}
	}
	//? } elif >=1.21.10 {
	/*@Override
	protected void renderScrollbar(GuiGraphics graphics, int mouseX, int mouseY) {
		if (this.maxScrollAmount() > 0) {
			int x = this.scrollBarX() + 1;
			int y = this.scrollBarY();
			graphics.fill(x, y, x + BAR_WIDTH, y + this.scrollerHeight(), BAR_COLOR);
		}
	}
	*///? } elif >=1.21.4 {
	/*@Override
	protected void renderScrollbar(GuiGraphics graphics) {
		if (this.maxScrollAmount() > 0) {
			int x = this.scrollBarX() + 1;
			int y = this.scrollBarY();
			graphics.fill(x, y, x + BAR_WIDTH, y + this.scrollerHeight(), BAR_COLOR);
		}
	}
	*///? }
	// 1.21.1 and below: keep the vanilla scrollbar so that dragging keeps working
	// (updateScrollingState gates on scrollbarVisible, and the drawing is not overridable).
}
