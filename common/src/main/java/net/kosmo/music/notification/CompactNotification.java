package net.kosmo.music.notification;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;

public class CompactNotification extends Notification {

	public static boolean canBeShown() {
		Screen screen = Minecraft.getInstance().gui.screen();
		return screen == null || screen instanceof ChatScreen;
	}

	public static void render(GuiGraphicsExtractor graphics, @NotNull DeltaTracker deltaTracker, Component message, int time) {
		Font font = Minecraft.getInstance().font;

		if (message != null) {
			int f = (int) (time - deltaTracker.getGameTimeDeltaPartialTick(false));
			int i = (int) (f * 255.0F / 20.0F);
			if (i >= 255) {
				i = 255;
			}

			if (i > 0) {
				graphics.pose().pushMatrix();
				graphics.pose().translate(graphics.guiWidth(), graphics.guiHeight() - 8/*? >=1.21.6 {*//*?} else {*//*, 0*//*?}*/);

				int width = font.width(message);
				int alpha = Config.options().STYLE_COMPACT_ALPHA;
				if (f < 10.0F) {
					alpha = Math.min(alpha, i);
				}
				//~ if >26 drawStringWithBackdrop -> textWithBackdrop
				graphics.textWithBackdrop(font, message, -width - 10, -4, width, ARGB.color(alpha, CommonColors.WHITE));
				graphics.pose().popMatrix();
			}
		}
	}

	public void show(Identifier resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
		MusicNotificationClient.PLATFORM_HELPER.setCompactNotification(trackData, Config.options().STYLE_COMPACT_TIME);
	}
}
