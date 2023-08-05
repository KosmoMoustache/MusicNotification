package net.kosmo.music.mixin;

import net.kosmo.music.gui.PlaySoundScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {


    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("PLAYMUSICGUI"), (button) -> {
            this.client.setScreen(new PlaySoundScreen());
        }).dimensions(10, 10, 20, 20).build());
    }
}
