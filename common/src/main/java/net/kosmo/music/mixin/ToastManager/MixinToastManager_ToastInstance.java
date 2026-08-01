//? if <=1.21.4 {
/*package net.kosmo.music.mixin.ToastManager;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kosmo.music.config.Config;
import net.kosmo.music.notification.toast.MusicToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ToastManager.ToastInstance.class)
class MixinToastManager_ToastInstance<T extends Toast> {
	@Shadow
	@Final
	private T toast;

	@WrapOperation(
		//~ if >=1.21.4 'render' -> 'update'
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;playSound(Lnet/minecraft/client/sounds/SoundManager;)V"
		)
	)
	private void playSoundMute(Toast.Visibility instance, SoundManager handler, Operation<Void> original) {
		if (Config.options().DISABLE_TOAST_SOUND == Config.Options.DisableToastSound.MUTE_ALL) {
			return;
		}
		if (Config.options().DISABLE_TOAST_SOUND == Config.Options.DisableToastSound.MUTE_SELF && this.toast instanceof MusicToast) {
			return;
		}
		original.call(instance, handler);
	}
}
*///? }