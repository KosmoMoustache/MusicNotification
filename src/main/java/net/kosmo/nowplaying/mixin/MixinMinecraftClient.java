package net.kosmo.nowplaying.mixin;

import net.kosmo.nowplaying.NowPlaying;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract  class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At(value="TAIL", target = "Lnet/minecraft/client/MinecraftClient;<init>(Lnet/minecraft/client/RunArgs;)V"))
    private void onInit(RunArgs args, CallbackInfo info) {
        NowPlaying.onClientInit();
    }
}
