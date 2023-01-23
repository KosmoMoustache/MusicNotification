package net.kosmo.music.mixin;

import net.kosmo.music.ClientMusic;
import net.kosmo.music.toast.MusicToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ToastManager.Entry.class)
public abstract class MixinToastManagerEntry<T extends Toast> {
    @Shadow @Final int topIndex;
    @Shadow @Final private T instance;

    /**
     *  This method is called when a toast is drawn to the screen
     *  This mixins modify the argument `y` of MatrixStack.translate(x, y, z) to return the correct y position when drawing a MusicToast
     *  TODO: Other toast can overlap the {@link MusicToast} toast when showing soundtrack
     */
    @ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"), index = 1)
    public double modifyY(double y) {
        if (this.instance instanceof MusicToast && !ClientMusic.config.showSoundtrackName) {
            return this.topIndex * this.instance.getHeight();
        }
        return this.topIndex * 32;
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/Toast$Visibility;playSound(Lnet/minecraft/client/sound/SoundManager;)V"))
    public void playSound(Toast.Visibility visibility, net.minecraft.client.sound.SoundManager soundManager) {
        switch (ClientMusic.config.disableToastSound) {
            case DISABLE_ALL:
                break;
            case DISABLE_MUSIC:
                if (this.instance instanceof MusicToast) {
                    break;
                }
            case ENABLED_ALL:
                visibility.playSound(soundManager);
        }
    }
}