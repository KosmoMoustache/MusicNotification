package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow public abstract void onResourceManagerReload(ResourceManager resourceManager);

    /**
     * Called when a Music Disc is used on a Jukebox.
     * It responsible for showing the disc name in the Game HUD above the action bar
     */
    @Inject(method = "playJukeboxSong", at = @At("HEAD"))
    private void onPlaySong(Holder<JukeboxSong> holder, BlockPos blockPos, CallbackInfo ci) {
        ClientMusic.onDiscPlay(holder.value());
    }

//    @Inject(method = "stopJukeboxSong", at = @At("HEAD"))
//    private void onPlaySong(BlockPos blockPos, CallbackInfo ci) {
//    }
}
