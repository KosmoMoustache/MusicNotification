package net.kosmo.music.mixin;

import net.kosmo.music.GuiAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui implements GuiAccessor {
	@Shadow
	public abstract Font getFont();

	@Unique
	Component musicnotification$compact$message;
	@Unique
	int musicnotification$compact$time;

	@Inject(method = "tick()V", at = @At(value = "TAIL"))
	private void tickInject(CallbackInfo ci) {
		if (this.musicnotification$compact$time > 0) {
			this.musicnotification$compact$time--;
		}
	}


	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	private void renderSubtitleOverlayInject(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Font font = this.getFont();
		if (musicnotification$compact$message != null) {
			Profiler.get().push("musicnotification:compact");
			float f = this.musicnotification$compact$time - deltaTracker.getGameTimeDeltaPartialTick(false);
			int i = (int) (f * 255.0F / 20.0F);
			if (i > 255) {
				i = 255;
			}

			if (i > 0) {
				guiGraphics.nextStratum();
				guiGraphics.pose().pushMatrix();
				guiGraphics.pose().translate(guiGraphics.guiWidth(), guiGraphics.guiHeight() - 8);

				int width = font.width(musicnotification$compact$message);
				guiGraphics.drawStringWithBackdrop(font, musicnotification$compact$message, -width - 10, -4, width, CommonColors.WHITE);
				guiGraphics.pose().popMatrix();
			}
		}

		Profiler.get().pop();
	}

	@Override
	public void musicNotification$setCompactNotificationMessage(Component message) {
		musicnotification$compact$message = message;
	}

	@Override
	public Component musicNotification$getCompactNotificationMessage() {
		return musicnotification$compact$message;
	}

	@Override
	public void musicNotification$setCompactNotificationTime(int message) {
		musicnotification$compact$time = message;
	}

	@Override
	public int musicNotification$getCompactNotificationTime() {
		return musicnotification$compact$time;
	}
}
