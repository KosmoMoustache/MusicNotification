package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {
    /**
     * Called when a Music Disc is used on a Jukebox.
     * It responsible for showing the disc name in the Game HUD above the action bar
     */
    @Inject(method = "playStreamingMusic", at = @At("HEAD"))
    private void onPlaySong(SoundEvent song, BlockPos songPosition, CallbackInfo ci) {
        ClientMusic.onDiscPlay(song);
    }
}
