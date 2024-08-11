package net.kosmo.music.impl.mixin;

import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.config.ConfigHolder;
import net.kosmo.music.impl.toast.MusicToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ToastComponent.ToastInstance.class)
public abstract class MixinToastManagerEntry<T extends Toast> {
    @Shadow
    @Final
    int index;
    @Shadow
    @Final
    private T toast;

    /**
     * This method is called when a toast is drawn to the screen
     * This mixins modify the argument `y` of MatrixStack.translate(x, y, z) to return the correct y position when drawing a MusicToast
     */
//    @ModifyArg(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), index = 1)
//    public float modifyY(float y) {
//        if (this.instance instanceof MusicToast && ClientMusic.config.SHOW_SOUNDTRACK_NAME) {
//            return this.topIndex * this.instance.getHeight();
//        }
//        return this.topIndex * 32;
//    }
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/Toast$Visibility;playSound(Lnet/minecraft/client/sounds/SoundManager;)V"), require = 0)
    public void playSound(Toast.Visibility visibility, net.minecraft.client.sounds.SoundManager soundManager) {
        if (ClientMusic.config.DISABLE_TOAST_SOUND == null)
            ClientMusic.config.DISABLE_TOAST_SOUND = ConfigHolder.DisableToastSound.MUTE_SELF;
        switch (ClientMusic.config.DISABLE_TOAST_SOUND) {
            case MUTE_ALL:
                break;
            case MUTE_SELF:
                if (this.toast instanceof MusicToast) break;
            case VANILLA:
                visibility.playSound(soundManager);
        }
    }
}