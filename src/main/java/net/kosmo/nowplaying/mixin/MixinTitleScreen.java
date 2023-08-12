package net.kosmo.nowplaying.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.kosmo.nowplaying.NowPlaying;
import net.kosmo.nowplaying.gui.PlaySoundScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    @Unique
    private final Text TITLE_SCREEN_BUTTON = Text.translatable("gui.mn.gui.title_screen");

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (NowPlaying.config.SHOW_TITLE_SCREEN_BUTTON) {
            this.addDrawableChild(ButtonWidget.builder(TITLE_SCREEN_BUTTON, (button) -> this.client.setScreen(new PlaySoundScreen(this.client.currentScreen))).dimensions(10, 10, this.textRenderer.getWidth(TITLE_SCREEN_BUTTON), 20).build());
        }
    }
}
