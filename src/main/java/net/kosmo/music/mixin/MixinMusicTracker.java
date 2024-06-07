package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MixinMusicTracker {
    @Shadow
    private SoundInstance currentMusic;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (ClientMusic.currentlyPlaying != null && Minecraft.getInstance().getSoundManager().isActive(ClientMusic.currentlyPlaying)) {
            currentMusic = null;
            ci.cancel();
        } else {
            ClientMusic.currentlyPlaying = null;
        }
    }
}
