package net.kosmo.nowplaying.mixin;

import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.toast.NowPlayingToast;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ToastManager.Entry.class)
public abstract class MixinToastManagerEntry<T extends Toast> {
    @Shadow
    @Final
    int topIndex;
    @Shadow
    @Final
    private T instance;

    /**
     * This method is called when a toast is drawn to the screen
     * This mixins modify the argument `y` of MatrixStack.translate(x, y, z) to return the correct y position when drawing a MusicToast
     *  TODO: Other toast can overlap the {@link NowPlayingToast} toast when showing soundtrack
     */
    @ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), index = 1)
    public float modifyY(float y) {
        if (this.instance instanceof NowPlayingToast && NowPlaying.config.SHOW_SOUNDTRACK) {
            return this.topIndex * this.instance.getHeight();
        }
        return this.topIndex * 32;
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/Toast$Visibility;playSound(Lnet/minecraft/client/sound/SoundManager;)V"))
    public void playSound(Toast.Visibility visibility, SoundManager soundManager) {
        // TODO: uncomment when ModMenu and cloth config are updated
        // Make game crash when hot swapping
//        switch (NowPlaying.config.DISABLE_TOAST_SOUND) {
//            case MUTE_ALL:
//                break;
//            case MUTE_MOD:
//                if (this.instance instanceof NowPlayingToast) break;
//            case VANILLA:
//                visibility.playSound(soundManager);
//        }
    }
}