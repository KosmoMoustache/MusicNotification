package net.kosmo.music.notification;

import net.kosmo.music.MusicNotificationClient;
import net.kosmo.music.config.Config;
import net.kosmo.music.resource.TrackData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;

public class CompactNotification extends Notification {

	public static boolean canBeShown() {
		Screen screen = Minecraft.getInstance().screen;
		return screen == null || screen instanceof ChatScreen;
	}

	public static void render(GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker, Component message, int time) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.font;

		if (message != null) {
			float f = time - deltaTracker.getGameTimeDeltaPartialTick(false);
			int i = (int) (f * 255.0F / 20.0F);
			if (i > 255) {
				i = 255;
			}

			if (i > 0) {
				guiGraphics.pose().pushMatrix();
				guiGraphics.pose().translate(guiGraphics.guiWidth(), guiGraphics.guiHeight() - 8);

				int width = font.width(message);
				int alpha = Config.options().STYLE_COMPACT_ALPHA;
				if (f < 10.0F) {
					alpha = Math.min(alpha, i);
				}
				guiGraphics.drawStringWithBackdrop(font, message, -width - 10, -4, width, ARGB.color(alpha, CommonColors.WHITE));
				guiGraphics.pose().popMatrix();
			}
		}
	}

	public void show(ResourceLocation resourceLocation, TrackData trackData) {
		super.show(resourceLocation, trackData, this.getClass());
		MusicNotificationClient.PLATFORM_HELPER.setCompactNotification(trackData, Config.options().STYLE_COMPACT_TIME);
	}
}
