package net.kosmo.music.impl.fabric.mixin;

import net.kosmo.music.impl.fabric.ClientMusicFabricMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

@Mixin(Minecraft.class)
public abstract  class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At(value="TAIL", target = "Lnet/minecraft/client/MinecraftClient;<init>(Lnet/minecraft/client/RunArgs;)V"))
    private void onInit(GameConfig args, CallbackInfo info) {
        ClientMusicFabricMod.onMinecraftClientMixin();
    }
}
