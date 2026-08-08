//? if >=1.21.5 {
package net.kosmo.music.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToastManager.class)
public class MixinToastManager {
	@WrapOperation(
		method = "lambda$update$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;playSound(Lnet/minecraft/client/sounds/SoundManager;)V"
		)
	)
	private void playSoundMute(Toast.Visibility instance, SoundManager handler, Operation<Void> original, @Local ToastManager.ToastInstance<?> toast) {
		MusicNotificationClient.TManagerMixinPlaySoundMute(instance, handler, original, toast);
	}
}
//? }
