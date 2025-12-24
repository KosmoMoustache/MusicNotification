package net.kosmo.music.fabric.mixin;

import net.kosmo.music.fabric.GuiAccessor;
import net.kosmo.music.notification.CompactNotification;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui implements GuiAccessor {
	@Unique
	Component musicnotification$compact$message;
	@Unique
	int musicnotification$compact$time;

	@Inject(method = "tick()V", at = @At(value = "TAIL"))
	private void tickInject(CallbackInfo ci) {
		if (this.musicnotification$compact$time > 0) {
			this.musicnotification$compact$time--;
			if (this.musicnotification$compact$time == 0) {
				this.musicnotification$compact$message = null;
			}
		}
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			//? if >=1.21.10 {
			/*target = "Lnet/minecraft/client/gui/Gui;renderSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphics;Z)V", ordinal = 0
			*///?} else {
			target = "Lnet/minecraft/client/gui/Gui;renderSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"
			//?}
		)
	)
	private void renderSubtitleOverlayInject(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		CompactNotification.render(guiGraphics, deltaTracker, musicnotification$compact$message, musicnotification$compact$time);
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
