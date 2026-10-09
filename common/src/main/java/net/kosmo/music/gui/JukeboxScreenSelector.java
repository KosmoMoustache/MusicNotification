package net.kosmo.music.gui;

import net.kosmo.music.config.Config;
import net.kosmo.music.gui.v3.JukeboxScreenV3;
import net.minecraft.client.gui.screens.Screen;

public class JukeboxScreenSelector {
	public static Screen create(Screen parent) {
		if (Config.options().USE_V2_UI) {
			return new JukeboxScreenV3(parent);
		}
		return new JukeboxScreen(parent);
	}
}
