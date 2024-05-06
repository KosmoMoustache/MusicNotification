package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MixinMusicTracker {
    @Shadow
    private SoundInstance current;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (ClientMusic.currentlyPlaying != null && MinecraftClient.getInstance().getSoundManager().isPlaying(ClientMusic.currentlyPlaying)) {
            current = null;
            ci.cancel();
        } else {
            ClientMusic.currentlyPlaying = null;
        };
    }
}
