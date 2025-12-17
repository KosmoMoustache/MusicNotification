package net.kosmo.music.util;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TextRender {
	/**
	 * Draw a scrollable text
	 */
	public static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int centerX, int minX, int minY, int maxX, int maxY, int color, boolean shadow) {
		int i = textRenderer.width(text);
		int j = (minY + maxY - textRenderer.lineHeight) / 2 + 1;
		int k = maxX - minX;
		if (i > k) {
			int l = i - k;
			double d = (double) Util.getMillis() / 1000.0;
			double e = Math.max((double) l * 0.5, 3.0);
			double f = Math.sin(Math.PI / 2D * Math.cos((Math.PI * 2D) * d / e)) / 2.0 + 0.5;
			double g = Mth.lerp(f, 0.0, l);

			context.enableScissor(minX, minY, maxX, maxY);
//			context.fill(minX, minY, maxX, maxY, CommonColors.RED);
			context.drawString(textRenderer, text.getVisualOrderText(), minX - (int) g, j, color, shadow);
			context.disableScissor();
		} else {
//			context.fill(0, 0, 1, 10, CommonColors.RED);
			int l = Mth.clamp(centerX, minX + i / 2, maxX - i / 2);

			FormattedCharSequence orderedText = text.getVisualOrderText();
			context.drawString(textRenderer, orderedText, l - textRenderer.width(orderedText) / 2, j, color, shadow);
		}
	}
}
