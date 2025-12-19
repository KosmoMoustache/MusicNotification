package net.kosmo.music.fabric.compat;

//? if modMenu {

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kosmo.music.config.ClothScreenProvider;

@Environment(EnvType.CLIENT)
public class ModMenuCompatibility implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return ClothScreenProvider::getConfigScreen;
	}
}
//?}