package net.kosmo.music.mixin;

import net.kosmo.music.SoundListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	@Shadow
	@Final
	private SoundManager soundManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	void registerSoundInstanceListener(CallbackInfo ci) {
		soundManager.addListener(new SoundListener());
	}
}
