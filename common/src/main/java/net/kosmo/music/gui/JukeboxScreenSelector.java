package net.kosmo.music.gui;

import net.kosmo.music.config.Config;
import net.kosmo.music.gui.v2.JukeboxScreenV2;
import net.minecraft.client.gui.screens.Screen;

public class JukeboxScreenSelector {
	public static Screen create() {
		if (Config.options().USE_V2_UI) {
			return new JukeboxScreenV2();
		}
		return new JukeboxScreen();
	}
}
