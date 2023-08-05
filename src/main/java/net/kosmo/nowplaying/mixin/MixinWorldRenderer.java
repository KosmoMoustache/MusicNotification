package net.kosmo.nowplaying.mixin;

import net.kosmo.nowplaying.NowPlaying;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    /**
     * Called when a Music Disc is used on a Jukebox.
     * It responsible for showing the disc name in the Game HUD above the action bar
     */
    @Inject(method = "playSong", at = @At("HEAD"))
    private void onPlaySong(SoundEvent song, BlockPos songPosition, CallbackInfo ci) {
        NowPlaying.onDiscPlay(song);
    }
}
