package net.kosmo.music.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
//? if <=1.21.10 {
/*import net.minecraft.util.Util;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
*///? }

public class TextRender {
	/**
	 * Draw a scrollable text
	 */
	public static void drawScrollableText(GuiGraphicsExtractor graphics, Font font, Component text, int left, int right, int top, int bottom, boolean shadow) {
		//? if <=1.21.10 {
		/*renderScrollingString(
			graphics,
			font,
			text,
			left + font.width(text) / 2,
			left,
			right,
			top,
			bottom,
			CommonColors.BLACK,
			shadow
		);
		*///?} else {
		if (!shadow) {
			text = text.copy().withoutShadow();
		}
		if (isTextRegionOnScreen(graphics, left, right, top, bottom)) {
			graphics.textRenderer().acceptScrolling(
				text,
				left + font.width(text) / 2,
				left,
				right,
				top,
				bottom
			);
		}
		//?}
	}

	//? if >1.21.10 {
	/**
	 * Check whether the region is visible on the screen or not
	 * This is needed because scissoring requires the region to be in visible screen (between 0 and guiWidth & 0 and guiHeight). A 0 width scissor resulted in a crash
	 * @return whether the region is visible on the screen or not
	 */
	private static boolean isTextRegionOnScreen(GuiGraphicsExtractor graphics, int left, int right, int top, int bottom) {
		if (right <= left || bottom <= top) {
			return false;
		}
		// Transform the current pose matrix into screen coordinates
		Vector2f topLeft = graphics.pose().transformPosition(left, top, new Vector2f());
		Vector2f bottomRight = graphics.pose().transformPosition(right, bottom, new Vector2f());
		return bottomRight.x > 0 && topLeft.x < graphics.guiWidth() && bottomRight.y > 0 && topLeft.y < graphics.guiHeight();
	}
	//?}


	//? if <=1.21.10 {
	/*private static void renderScrollingString(GuiGraphicsExtractor guiGraphics, Font font, Component text, int centerX, int left, int right, int top, int bottom, int color, boolean shadow) {
		int textWidth = font.width(text);
		int textY = (top + bottom - font.lineHeight) / 2 + 1;
		int availableWidth = right - left;
		if (textWidth > availableWidth) {
			int overflow = textWidth - availableWidth;
			double time = Util.getMillis() / 1000.0;
			double scrollSpeed = Math.max(overflow * 0.5, 3.0);
			double bounceFactor = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * time / scrollSpeed)) / 2.0 + 0.5;
			double scrollOffset = Mth.lerp(bounceFactor, 0.0, overflow);

			//? if <=1.21.3 {
			/^// 1.21.1-1.21.3 enableScissor ignores the pose, but the toast is drawn with a translated pose, so move the scissor by hand.
			// Also clamp it to the screen: while the toast slides in/out it is off-screen, and an out-of-bounds scissor makes the screen flicker.
			Matrix4f pose = guiGraphics.pose().last().pose();
			int x0 = Mth.clamp(left + (int) pose.m30(), 0, guiGraphics.guiWidth());
			int y0 = Mth.clamp(top + (int) pose.m31(), 0, guiGraphics.guiHeight());
			int x1 = Mth.clamp(right + (int) pose.m30(), 0, guiGraphics.guiWidth());
			int y1 = Mth.clamp(bottom + (int) pose.m31(), 0, guiGraphics.guiHeight());
			if (x1 > x0 && y1 > y0) {
				guiGraphics.enableScissor(x0, y0, x1, y1);
				guiGraphics.drawString(font, text, left - (int) scrollOffset, textY, color, shadow);
				guiGraphics.disableScissor();
			} else {
				guiGraphics.drawString(font, text, left - (int) scrollOffset, textY, color, shadow);
			}
			^///? } else {
			guiGraphics.enableScissor(left, top, right, bottom);
			guiGraphics.drawString(font, text, left - (int) scrollOffset, textY, color, shadow);
			guiGraphics.disableScissor();
			//? }
		} else {
			guiGraphics.drawString(font, text, left, textY, color, false);
		}
	}
	*///?}
}
