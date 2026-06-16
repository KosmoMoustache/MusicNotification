//? if >=26.2 {
package net.kosmo.music.fabric.mixin;

import net.kosmo.music.fabric.GuiAccessor;
import net.kosmo.music.notification.CompactNotification;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class MixinHud implements GuiAccessor {
	@Unique
	private Component musicnotification$compact$message;
	@Unique
	private int musicnotification$compact$time;

	@Inject(method = "tick()V", at = @At(value = "TAIL"))
	private void musicNotification$tickInject(CallbackInfo ci) {
		if (this.musicnotification$compact$time > 0) {
			this.musicnotification$compact$time--;
			if (this.musicnotification$compact$time == 0) {
				this.musicnotification$compact$message = null;
			}
		}
	}

	@Inject(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Hud;extractSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Z)V", ordinal = 0
		)
	)
	private void musicNotification$renderSubtitleOverlayInject(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		CompactNotification.render(graphics, deltaTracker, musicnotification$compact$message, musicnotification$compact$time);
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
	public void musicNotification$setCompactNotificationTime(int time) {
		musicnotification$compact$time = time;
	}

	@Override
	public int musicNotification$getCompactNotificationTime() {
		return musicnotification$compact$time;
	}
}
//? }