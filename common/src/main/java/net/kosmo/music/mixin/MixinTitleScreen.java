package net.kosmo.music.mixin;

import net.kosmo.music.config.Config;
import net.kosmo.music.gui.JukeboxScreenSelector;
import net.kosmo.music.util.CommonComponents;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

	protected MixinTitleScreen(Component title) {
		super(title);
	}

	@Inject(method = "init()V", at = @At("RETURN"))
	private void insertJukeboxMenu(CallbackInfo ci) {
		if (Config.options().SHOW_JUKEBOX_MENU_BUTTON) {
			SpriteIconButton openJukebox = this.addRenderableWidget(CommonComponents.jukebox(20, button -> this.minecraft.gui.setScreen(JukeboxScreenSelector.create()), true));
			openJukebox.setPosition(12, 12);
		}
	}
}
