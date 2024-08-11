package net.kosmo.music.impl.fabric.mixin;

import net.kosmo.music.impl.fabric.ClientMusicFabric;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At(value = "TAIL", target = "Lnet/minecraft/client/Minecraft;<init>(Lnet/minecraft/client/main/GameConfig;)V"))
    private void onInit(GameConfig args, CallbackInfo info) {
        ClientMusicFabric.onMinecraftClientMixin();
    }
}
