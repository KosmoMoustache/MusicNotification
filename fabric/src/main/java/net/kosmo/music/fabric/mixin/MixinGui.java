//? if <26.2 {
/*package net.kosmo.music.fabric.mixin;

import net.kosmo.music.fabric.GuiAccessor;
import net.kosmo.music.notification.CompactNotification;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <=1.21.5 {
/^import net.minecraft.client.gui.LayeredDraw;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
^///? }

@Mixin(Gui.class)
public abstract class MixinGui implements GuiAccessor {
	@Unique
	private Component musicnotification$compact$message;
	@Unique
	private int musicnotification$compact$time;
	//? if <=1.21.5 {
	/^@Final
	@Shadow
	private LayeredDraw layers;
	^///? }

	@Inject(method = "tick()V", at = @At(value = "TAIL"))
	private void musicNotification$tickInject(CallbackInfo ci) {
		if (this.musicnotification$compact$time > 0) {
			this.musicnotification$compact$time--;
			if (this.musicnotification$compact$time == 0) {
				this.musicnotification$compact$message = null;
			}
		}
	}

	//? if >=1.21.6 {
	@Inject(
		//~ if >26 render -> extractRenderState
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			//? if >=1.21.10 {
			//~ if >26 renderSubtitleOverlay -> extractSubtitleOverlay
			target = "Lnet/minecraft/client/gui/Gui;extractSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Z)V", ordinal = 0
			//?} else {
			/^target = "Lnet/minecraft/client/gui/Gui;renderSubtitleOverlay(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
			^///?}
		)
	)
	private void musicNotification$renderSubtitleOverlayInject(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		CompactNotification.render(graphics, deltaTracker, musicnotification$compact$message, musicnotification$compact$time);
	}
	//? } else {
	/^@Inject(method = "<init>", at = @At("TAIL"))
	private void musicNotification$addRenderLayer(Minecraft minecraft, CallbackInfo ci) {
		this.layers.add(this::musicNotification$renderCompactNotification);
	}
	@Unique
	private void musicNotification$renderCompactNotification(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		CompactNotification.render(graphics, deltaTracker, musicnotification$compact$message, musicnotification$compact$time);
	}
	^///?}


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
*///? }