//? if <=1.21.4 {
/*package net.kosmo.music.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kosmo.music.MusicNotificationClient;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ToastManager.ToastInstance.class)
@Debug(export = true)
public class MixinTManager<T extends Toast> {
	@Shadow
	@Final
	public T toast;

	@WrapOperation(
		//~ if >=1.21.4 'render' -> 'update'
		method = "update",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;playSound(Lnet/minecraft/client/sounds/SoundManager;)V"
		)
	)
	private void playSoundMute(Toast.Visibility instance, SoundManager handler, Operation<Void> original) {
		MusicNotificationClient.TManagerMixinPlaySoundMute(instance, handler, original, toast);
	}
}
*///? }