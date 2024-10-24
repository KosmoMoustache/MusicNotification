package net.kosmo.music.impl.mixin;

import net.kosmo.music.impl.ClientMusic;
import net.kosmo.music.impl.gui.JukeboxScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Component title) {
        super(title);
    }

    @Inject(method = "Lnet/minecraft/client/gui/screens/TitleScreen;init()V", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (ClientMusic.config.SHOW_TITLE_SCREEN_BUTTON) {
            this.addRenderableWidget(new ImageButton(12, 0, 20, 20, new WidgetSprites(ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "jukebox/icon"), ResourceLocation.fromNamespaceAndPath(ClientMusic.MOD_ID, "jukebox/icon_focused")), (button) -> {
                this.minecraft.setScreen(new JukeboxScreen(this));
            }));
        }
    }
}
