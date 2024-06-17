package net.kosmo.music.impl;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class RenderHelper {
    /**
     * Draw a scrollable text
     */
    public static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow) {
        drawScrollableText(context, textRenderer, text, centerX, startX, startY, endX, endY, color, shadow, startX, startY, endX, endY);
    }

    public static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int centerX, int startX, int startY, int endX, int endY, int color, boolean shadow, int clipAreaX1, int clipAreaY1, int clipAreaX2, int clipAreaY2) {
        int i = textRenderer.width(text);
        int j = (startY + endY - textRenderer.lineHeight) / 2 + 1;
        int k = endX - startX;
        if (i > k) {
            int l = i - k;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double) l * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, (double) l);

            context.enableScissor(clipAreaX1, clipAreaY1, clipAreaX2, clipAreaY2);
//            context.fill(clipAreaX1,clipAreaY1,clipAreaX2,clipAreaY2, Colors.RED);
            context.drawString(textRenderer, text.getVisualOrderText(), startX - (int) g, j, color, shadow);
            context.disableScissor();
        } else {
            int l = Mth.clamp(centerX, startX + i / 2, endX - i / 2);

            FormattedCharSequence orderedText = text.getVisualOrderText();
            context.drawString(textRenderer, orderedText, l - textRenderer.width(orderedText) / 2, j, color, shadow);
        }
    }
}
