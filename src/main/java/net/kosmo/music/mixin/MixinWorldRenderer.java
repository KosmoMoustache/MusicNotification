package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.utils.resource.MusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    /**
     * Called when a Music Disc is used on a Jukebox.
     * It responsible for showing the disc name in the Game HUD above the action bar
     */
    @Inject(method = "playSong", at = @At("HEAD"))
    private void onPlaySong(SoundEvent song, BlockPos songPosition, CallbackInfo ci) {
        ClientMusic.onDiscPlay(song);
    }
}
